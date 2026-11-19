package com.Cabmeri3.Nasxil.domain.model

enum class CrossAction {
    DASH,
    SLIDE
}

enum class CrossOutcome {
    PERFECT,
    CLEAN,
    EDGY,
    FAIL
}

enum class FailCause {
    RED_LIGHT,
    GATE_DOWN,
    FERRY_MISS,
    LAMP_OUT
}

data class CrossResult(
    val outcome: CrossOutcome,
    val failCause: FailCause? = null,
    val forgiven: Boolean = false
)
