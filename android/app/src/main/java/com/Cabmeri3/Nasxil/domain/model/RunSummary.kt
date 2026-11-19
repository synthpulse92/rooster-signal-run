package com.Cabmeri3.Nasxil.domain.model

data class RunSummary(
    val distance: Int = 0,
    val stamps: Int = 0,
    val score: Int = 0,
    val bestChain: Int = 0,
    val masteryGain: Int = 0,
    val newRecord: Boolean = false,
    val ghostDelta: Int? = null,
    val failCause: FailCause = FailCause.RED_LIGHT
)
