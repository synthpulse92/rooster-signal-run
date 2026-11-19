package com.Cabmeri3.Nasxil.presentation.depot

import androidx.lifecycle.ViewModel
import com.Cabmeri3.Nasxil.data.sample.SampleData
import com.Cabmeri3.Nasxil.domain.model.MasteryTier
import com.Cabmeri3.Nasxil.domain.model.PlayerProgress
import com.Cabmeri3.Nasxil.domain.model.UpgradeId
import com.Cabmeri3.Nasxil.domain.repository.ProgressRepository
import com.Cabmeri3.Nasxil.domain.usecase.PurchaseUpgradeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DepotViewModel(
    private val progressStore: ProgressRepository,
    private val purchase: PurchaseUpgradeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DepotUiState())
    val state: StateFlow<DepotUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.value = map(progressStore.load())
    }

    fun buy(id: UpgradeId) {
        val progress = progressStore.load()
        val updated = purchase(progress, SampleData.upgradeOf(id)) ?: return
        progressStore.save(updated)
        _state.value = map(updated)
    }

    private fun map(progress: PlayerProgress): DepotUiState = DepotUiState(
        stamps = progress.stamps,
        mastery = SampleData.masteryOrder.map { type ->
            val xp = progress.xpOf(type)
            MasteryUi(
                type = type,
                tier = MasteryTier.tierOf(xp),
                progress = MasteryTier.progressToNext(xp),
                scorePercent = MasteryTier.scorePercent(xp),
                windowMs = MasteryTier.windowBonusMs(xp)
            )
        },
        upgrades = SampleData.upgradeCatalogue.map { upgrade ->
            UpgradeUi(
                id = upgrade.id,
                cost = upgrade.cost,
                owned = progress.owned.contains(upgrade.id),
                affordable = progress.stamps >= upgrade.cost
            )
        }
    )
}
