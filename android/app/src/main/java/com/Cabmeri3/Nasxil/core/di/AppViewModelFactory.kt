package com.Cabmeri3.Nasxil.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Cabmeri3.Nasxil.domain.repository.GhostRepository
import com.Cabmeri3.Nasxil.domain.repository.ProgressRepository
import com.Cabmeri3.Nasxil.domain.repository.SettingsRepository
import com.Cabmeri3.Nasxil.domain.usecase.GenerateRouteUseCase
import com.Cabmeri3.Nasxil.domain.usecase.PurchaseUpgradeUseCase
import com.Cabmeri3.Nasxil.domain.usecase.ResolveCrossingUseCase
import com.Cabmeri3.Nasxil.presentation.app.AppViewModel
import com.Cabmeri3.Nasxil.presentation.depot.DepotViewModel
import com.Cabmeri3.Nasxil.presentation.game.GameViewModel
import com.Cabmeri3.Nasxil.presentation.gameover.GameOverViewModel
import com.Cabmeri3.Nasxil.presentation.menu.MenuViewModel
import com.Cabmeri3.Nasxil.presentation.settings.SettingsViewModel
import com.Cabmeri3.Nasxil.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val ghost: GhostRepository,
    private val generateRoute: GenerateRouteUseCase,
    private val resolveCrossing: ResolveCrossingUseCase,
    private val purchaseUpgrade: PurchaseUpgradeUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(progress, settings)
            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(progress, settings, ghost, generateRoute, resolveCrossing)
            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(progress)
            modelClass.isAssignableFrom(DepotViewModel::class.java) ->
                DepotViewModel(progress, purchaseUpgrade)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settings, progress, ghost)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
