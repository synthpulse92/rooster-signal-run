package com.Cabmeri3.Nasxil.presentation.gameover

import androidx.lifecycle.ViewModel
import com.Cabmeri3.Nasxil.domain.model.RunSummary
import com.Cabmeri3.Nasxil.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GameOverUiState(
    val summary: RunSummary = RunSummary(),
    val totalStamps: Int = 0,
    val bestDistance: Int = 0
)

class GameOverViewModel(
    private val progressStore: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    fun bind(summary: RunSummary) {
        val progress = progressStore.load()
        _state.update {
            it.copy(
                summary = summary,
                totalStamps = progress.stamps,
                bestDistance = progress.bestDistance
            )
        }
    }
}
