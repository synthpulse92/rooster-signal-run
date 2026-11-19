package com.Cabmeri3.Nasxil.presentation.menu

import androidx.lifecycle.ViewModel
import com.Cabmeri3.Nasxil.domain.model.NightMode
import com.Cabmeri3.Nasxil.domain.repository.ProgressRepository
import com.Cabmeri3.Nasxil.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel(
    private val progressStore: ProgressRepository,
    private val settingsStore: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressStore.load()
        val settings = settingsStore.load()
        _state.update {
            it.copy(
                bestDistance = progress.bestDistance,
                bestChain = progress.bestChain,
                masteryTiers = progress.totalTiers(),
                stamps = progress.stamps,
                nightAlways = settings.nightMode == NightMode.ALWAYS_ON
            )
        }
    }
}
