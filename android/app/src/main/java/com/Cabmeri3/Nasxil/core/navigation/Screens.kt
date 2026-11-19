package com.Cabmeri3.Nasxil.core.navigation

import com.Cabmeri3.Nasxil.domain.model.RunSummary

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Game : Screen
    data class GameOver(val summary: RunSummary) : Screen
    data object Depot : Screen
    data object Settings : Screen
}
