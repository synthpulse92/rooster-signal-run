package com.Cabmeri3.Nasxil.domain.model

enum class LayerType {
    ROAD,
    RAIL,
    FERRY
}

enum class LampPhase {
    RED,
    AMBER,
    GREEN,
    DARK
}

enum class GateState {
    UP,
    LOWERING,
    DOWN
}

data class RouteLayer(
    val index: Int,
    val type: LayerType,
    val greenMs: Int,
    val phaseOffsetMs: Int,
    val brokenLamp: Boolean = false,
    val vanCount: Int = 0,
    val vanToRight: Boolean = true,
    val ferryPeriodMs: Int = 0,
    val ferryPhase: Float = 0f,
    val hasShortcut: Boolean = false
)
