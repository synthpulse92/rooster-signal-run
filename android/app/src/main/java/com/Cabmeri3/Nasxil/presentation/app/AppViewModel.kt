package com.Cabmeri3.Nasxil.presentation.app

import androidx.lifecycle.ViewModel
import com.Cabmeri3.Nasxil.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val backStack = mutableListOf<Screen>()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) backStack.add(current)
        _screen.value = target
    }

    fun replace(target: Screen) {
        _screen.value = target
    }

    fun reset(target: Screen) {
        backStack.clear()
        _screen.value = target
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
    }
}
