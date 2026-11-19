package com.Cabmeri3.Nasxil.domain.usecase

import com.Cabmeri3.Nasxil.core.config.GameConfig
import com.Cabmeri3.Nasxil.domain.model.CrossAction
import com.Cabmeri3.Nasxil.domain.model.CrossOutcome
import com.Cabmeri3.Nasxil.domain.model.CrossResult
import com.Cabmeri3.Nasxil.domain.model.FailCause
import com.Cabmeri3.Nasxil.domain.model.GateState
import com.Cabmeri3.Nasxil.domain.model.LampPhase
import com.Cabmeri3.Nasxil.domain.model.LayerType
import com.Cabmeri3.Nasxil.domain.model.RouteLayer
import com.Cabmeri3.Nasxil.domain.model.SignalClock
import kotlin.math.abs

class ResolveCrossingUseCase {

    operator fun invoke(
        layer: RouteLayer,
        clockMs: Long,
        action: CrossAction,
        ferryWindow: Float,
        vestAvailable: Boolean
    ): CrossResult {
        val raw = resolve(layer, clockMs, action, ferryWindow)
        if (raw.outcome == CrossOutcome.FAIL && vestAvailable) {
            return CrossResult(CrossOutcome.EDGY, null, forgiven = true)
        }
        return raw
    }

    private fun resolve(
        layer: RouteLayer,
        clockMs: Long,
        action: CrossAction,
        ferryWindow: Float
    ): CrossResult = when (layer.type) {
        LayerType.ROAD -> road(layer, clockMs, action)
        LayerType.RAIL -> rail(layer, clockMs, action)
        LayerType.FERRY -> ferry(layer, clockMs, action, ferryWindow)
    }

    private fun road(layer: RouteLayer, clockMs: Long, action: CrossAction): CrossResult {
        if (layer.brokenLamp) {
            return if (action == CrossAction.SLIDE) CrossResult(CrossOutcome.CLEAN)
            else CrossResult(CrossOutcome.FAIL, FailCause.LAMP_OUT)
        }
        if (action == CrossAction.SLIDE) return CrossResult(CrossOutcome.CLEAN)
        return when (SignalClock.lampPhase(layer, clockMs)) {
            LampPhase.GREEN ->
                if (SignalClock.msIntoGreen(layer, clockMs) < GameConfig.PERFECT_WINDOW_MS)
                    CrossResult(CrossOutcome.PERFECT)
                else CrossResult(CrossOutcome.CLEAN)
            LampPhase.AMBER -> CrossResult(CrossOutcome.EDGY)
            else -> CrossResult(CrossOutcome.FAIL, FailCause.RED_LIGHT)
        }
    }

    private fun rail(layer: RouteLayer, clockMs: Long, action: CrossAction): CrossResult {
        val state = SignalClock.gateState(layer, clockMs)
        if (state == GateState.LOWERING) return CrossResult(CrossOutcome.FAIL, FailCause.GATE_DOWN)
        if (action == CrossAction.SLIDE) return CrossResult(CrossOutcome.CLEAN)
        if (state == GateState.DOWN) return CrossResult(CrossOutcome.FAIL, FailCause.GATE_DOWN)
        val inState = SignalClock.gateTimeInState(layer, clockMs)
        return if (inState < GameConfig.PERFECT_WINDOW_MS) CrossResult(CrossOutcome.PERFECT)
        else CrossResult(CrossOutcome.CLEAN)
    }

    private fun ferry(
        layer: RouteLayer,
        clockMs: Long,
        action: CrossAction,
        ferryWindow: Float
    ): CrossResult {
        if (action == CrossAction.SLIDE) return CrossResult(CrossOutcome.FAIL, FailCause.FERRY_MISS)
        val distance = abs(SignalClock.ferryOffset(layer, clockMs))
        return when {
            distance < ferryWindow * 0.45f -> CrossResult(CrossOutcome.PERFECT)
            distance < ferryWindow -> CrossResult(CrossOutcome.CLEAN)
            else -> CrossResult(CrossOutcome.FAIL, FailCause.FERRY_MISS)
        }
    }
}
