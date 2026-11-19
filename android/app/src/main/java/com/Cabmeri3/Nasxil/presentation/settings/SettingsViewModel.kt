package com.Cabmeri3.Nasxil.presentation.settings

import androidx.lifecycle.ViewModel
import com.Cabmeri3.Nasxil.domain.model.GameSettings
import com.Cabmeri3.Nasxil.domain.model.NightMode
import com.Cabmeri3.Nasxil.domain.repository.GhostRepository
import com.Cabmeri3.Nasxil.domain.repository.ProgressRepository
import com.Cabmeri3.Nasxil.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val settings: GameSettings = GameSettings(),
    val confirmVisible: Boolean = false
)

class SettingsViewModel(
    private val settingsStore: SettingsRepository,
    private val progressStore: ProgressRepository,
    private val ghostStore: GhostRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(settings = settingsStore.load(), confirmVisible = false) }
    }

    fun setNightMode(mode: NightMode) {
        apply(_state.value.settings.copy(nightMode = mode))
    }

    fun setGhost(enabled: Boolean) {
        apply(_state.value.settings.copy(ghostEnabled = enabled))
    }

    fun setRookieAssist(enabled: Boolean) {
        apply(_state.value.settings.copy(rookieAssist = enabled))
    }

    fun askReset() {
        _state.update { it.copy(confirmVisible = true) }
    }

    fun dismissReset() {
        _state.update { it.copy(confirmVisible = false) }
    }

    fun confirmReset() {
        progressStore.clear()
        ghostStore.clear()
        _state.update { it.copy(confirmVisible = false) }
    }

    private fun apply(settings: GameSettings) {
        settingsStore.save(settings)
        _state.update { it.copy(settings = settings) }
    }
}
