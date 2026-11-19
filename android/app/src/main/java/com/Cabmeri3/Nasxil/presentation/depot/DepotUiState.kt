package com.Cabmeri3.Nasxil.presentation.depot

import com.Cabmeri3.Nasxil.domain.model.LayerType
import com.Cabmeri3.Nasxil.domain.model.UpgradeId

data class MasteryUi(
    val type: LayerType,
    val tier: Int,
    val progress: Float,
    val scorePercent: Int,
    val windowMs: Int
)

data class UpgradeUi(
    val id: UpgradeId,
    val cost: Int,
    val owned: Boolean,
    val affordable: Boolean
)

data class DepotUiState(
    val stamps: Int = 0,
    val mastery: List<MasteryUi> = emptyList(),
    val upgrades: List<UpgradeUi> = emptyList()
) {
    val empty: Boolean
        get() = stamps == 0 && upgrades.none { it.owned }
}
