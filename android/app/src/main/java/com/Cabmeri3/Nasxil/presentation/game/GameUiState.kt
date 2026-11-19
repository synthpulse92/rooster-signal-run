package com.Cabmeri3.Nasxil.presentation.game

import com.Cabmeri3.Nasxil.core.config.GameConfig
import com.Cabmeri3.Nasxil.domain.model.CrossOutcome
import com.Cabmeri3.Nasxil.domain.model.FailCause
import com.Cabmeri3.Nasxil.domain.model.RouteLayer
import com.Cabmeri3.Nasxil.domain.model.RunSummary

enum class RunPhase {
    RUNNING,
    FAILING
}

data class GameUiState(
    val layers: List<RouteLayer> = emptyList(),
    val layerIndex: Int = 0,
    val clockMs: Long = 0L,
    val elapsedMs: Long = 0L,
    val phase: RunPhase = RunPhase.RUNNING,
    val score: Int = 0,
    val stamps: Int = 0,
    val chain: Int = 0,
    val bestChain: Int = 0,
    val overdrive: Int = 0,
    val overdriveActive: Boolean = false,
    val overdriveLeftMs: Int = 0,
    val rainStep: Int = 0,
    val night: Boolean = false,
    val shortcutArmed: Boolean = false,
    val shortcutLeftMs: Int = 0,
    val ghostVisible: Boolean = false,
    val ghostLayer: Int = -1,
    val slideLeftMs: Int = 0,
    val vestCharges: Int = 0,
    val lampTimerOwned: Boolean = false,
    val ferryWindow: Float = GameConfig.FERRY_WINDOW,
    val scoreMultiplier: Float = 1f,
    val paused: Boolean = false,
    val failCause: FailCause? = null,
    val failLeftMs: Int = 0,
    val lastOutcome: CrossOutcome? = null,
    val seed: Int = 0
)

sealed interface GameEvent {
    data class Finished(val summary: RunSummary) : GameEvent
}
