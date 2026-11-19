package com.Cabmeri3.Nasxil.domain.model

import com.Cabmeri3.Nasxil.core.config.GameConfig
import kotlin.math.sin

object SignalClock {

    fun cycleMs(layer: RouteLayer): Int = GameConfig.RED_MS + GameConfig.AMBER_MS + layer.greenMs

    fun phaseTime(layer: RouteLayer, clockMs: Long): Int {
        val cycle = cycleMs(layer).coerceAtLeast(1)
        val raw = ((clockMs + layer.phaseOffsetMs) % cycle).toInt()
        return if (raw < 0) raw + cycle else raw
    }

    fun lampPhase(layer: RouteLayer, clockMs: Long): LampPhase {
        if (layer.type != LayerType.ROAD) return LampPhase.DARK
        if (layer.brokenLamp) return LampPhase.DARK
        val t = phaseTime(layer, clockMs)
        return when {
            t < GameConfig.RED_MS -> LampPhase.RED
            t < GameConfig.RED_MS + GameConfig.AMBER_MS -> LampPhase.AMBER
            else -> LampPhase.GREEN
        }
    }

    fun msIntoGreen(layer: RouteLayer, clockMs: Long): Int =
        phaseTime(layer, clockMs) - GameConfig.RED_MS - GameConfig.AMBER_MS

    fun greenLeftMs(layer: RouteLayer, clockMs: Long): Int =
        (layer.greenMs - msIntoGreen(layer, clockMs)).coerceAtLeast(0)

    fun gateState(layer: RouteLayer, clockMs: Long): GateState {
        val cycle = GameConfig.GATE_CYCLE_MS
        val raw = ((clockMs + layer.phaseOffsetMs) % cycle).toInt()
        val t = if (raw < 0) raw + cycle else raw
        return when {
            t < GameConfig.GATE_UP_MS -> GateState.UP
            t < GameConfig.GATE_UP_MS + GameConfig.GATE_LOWER_MS -> GateState.LOWERING
            else -> GateState.DOWN
        }
    }

    fun gateAngle(layer: RouteLayer, clockMs: Long): Float {
        val cycle = GameConfig.GATE_CYCLE_MS
        val raw = ((clockMs + layer.phaseOffsetMs) % cycle).toInt()
        val t = if (raw < 0) raw + cycle else raw
        return when {
            t < GameConfig.GATE_UP_MS -> 0f
            t < GameConfig.GATE_UP_MS + GameConfig.GATE_LOWER_MS ->
                86f * (t - GameConfig.GATE_UP_MS) / GameConfig.GATE_LOWER_MS
            else -> 86f
        }
    }

    fun gateTimeInState(layer: RouteLayer, clockMs: Long): Int {
        val cycle = GameConfig.GATE_CYCLE_MS
        val raw = ((clockMs + layer.phaseOffsetMs) % cycle).toInt()
        val t = if (raw < 0) raw + cycle else raw
        return when {
            t < GameConfig.GATE_UP_MS -> t
            t < GameConfig.GATE_UP_MS + GameConfig.GATE_LOWER_MS -> t - GameConfig.GATE_UP_MS
            else -> t - GameConfig.GATE_UP_MS - GameConfig.GATE_LOWER_MS
        }
    }

    fun ferryOffset(layer: RouteLayer, clockMs: Long): Float {
        val period = layer.ferryPeriodMs.coerceAtLeast(1)
        val t = (clockMs % period).toFloat() / period
        return sin((t + layer.ferryPhase) * 2f * Math.PI.toFloat())
    }

    fun vanOffset(layer: RouteLayer, slot: Int, clockMs: Long): Float {
        val span = (GameConfig.RED_MS + GameConfig.AMBER_MS).toFloat()
        val t = phaseTime(layer, clockMs).toFloat()
        val progress = ((t / span) + slot * 0.37f) % 1f
        val travel = progress * 2.6f - 1.3f
        return if (layer.vanToRight) travel else -travel
    }

    fun vansVisible(layer: RouteLayer, clockMs: Long): Boolean {
        if (layer.type != LayerType.ROAD || layer.vanCount <= 0) return false
        if (layer.brokenLamp) return true
        return lampPhase(layer, clockMs) != LampPhase.GREEN
    }
}
