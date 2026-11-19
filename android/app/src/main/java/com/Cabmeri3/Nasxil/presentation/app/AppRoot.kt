package com.Cabmeri3.Nasxil.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Cabmeri3.Nasxil.core.di.ServiceLocator
import com.Cabmeri3.Nasxil.core.navigation.Screen
import com.Cabmeri3.Nasxil.core.theme.NightBase
import com.Cabmeri3.Nasxil.presentation.depot.DepotRoute
import com.Cabmeri3.Nasxil.presentation.game.GameRoute
import com.Cabmeri3.Nasxil.presentation.gameover.GameOverRoute
import com.Cabmeri3.Nasxil.presentation.menu.MenuRoute
import com.Cabmeri3.Nasxil.presentation.settings.SettingsRoute
import com.Cabmeri3.Nasxil.presentation.splash.SplashRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()

    BackHandler(enabled = screen !is Screen.Splash && screen !is Screen.Menu) {
        appViewModel.back()
    }

    Box(modifier = Modifier.fillMaxSize().background(NightBase)) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = { fadeIn(tween(420)) togetherWith fadeOut(tween(300)) },
            label = "screenHost"
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashRoute(
                    onFinished = { appViewModel.reset(Screen.Menu) }
                )
                is Screen.Menu -> MenuRoute(
                    onPlay = { appViewModel.navigate(Screen.Game) },
                    onDepot = { appViewModel.navigate(Screen.Depot) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )
                is Screen.Game -> GameRoute(
                    onGameOver = { summary -> appViewModel.replace(Screen.GameOver(summary)) },
                    onMenu = { appViewModel.reset(Screen.Menu) }
                )
                is Screen.GameOver -> GameOverRoute(
                    summary = target.summary,
                    onPlayAgain = { appViewModel.replace(Screen.Game) },
                    onDepot = { appViewModel.navigate(Screen.Depot) },
                    onMenu = { appViewModel.reset(Screen.Menu) }
                )
                is Screen.Depot -> DepotRoute(
                    onMenu = { appViewModel.back() }
                )
                is Screen.Settings -> SettingsRoute(
                    onMenu = { appViewModel.back() }
                )
            }
        }
    }
}
