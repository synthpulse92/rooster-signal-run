package com.Cabmeri3.Nasxil.domain.model

data class PlayerProgress(
    val bestDistance: Int = 0,
    val bestChain: Int = 0,
    val bestScore: Int = 0,
    val stamps: Int = 0,
    val runCount: Int = 0,
    val owned: Set<UpgradeId> = emptySet(),
    val mastery: Map<LayerType, Int> = emptyMap()
) {
    fun xpOf(type: LayerType): Int = mastery[type] ?: 0

    fun totalTiers(): Int = LayerType.entries.sumOf { MasteryTier.tierOf(xpOf(it)) }
}
