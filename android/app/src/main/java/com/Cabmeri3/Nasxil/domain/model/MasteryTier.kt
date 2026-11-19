package com.Cabmeri3.Nasxil.domain.model

import com.Cabmeri3.Nasxil.core.config.GameConfig

object MasteryTier {

    val thresholds = listOf(20, 60, 140, 300)

    fun tierOf(xp: Int): Int = thresholds.count { xp >= it }

    fun nextThreshold(xp: Int): Int = thresholds.firstOrNull { xp < it } ?: thresholds.last()

    fun progressToNext(xp: Int): Float {
        val tier = tierOf(xp)
        if (tier >= thresholds.size) return 1f
        val floor = if (tier == 0) 0 else thresholds[tier - 1]
        val ceiling = thresholds[tier]
        val span = (ceiling - floor).coerceAtLeast(1)
        return ((xp - floor).toFloat() / span).coerceIn(0f, 1f)
    }

    fun windowBonusMs(xp: Int): Int = tierOf(xp) * GameConfig.MASTERY_WINDOW_MS

    fun scorePercent(xp: Int): Int = tierOf(xp) * 2
}

data class MasteryRow(
    val type: LayerType,
    val xp: Int
)
