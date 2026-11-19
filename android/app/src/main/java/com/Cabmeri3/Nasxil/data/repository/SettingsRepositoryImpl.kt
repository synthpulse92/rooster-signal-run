package com.Cabmeri3.Nasxil.data.repository

import com.Cabmeri3.Nasxil.data.local.RunPreferences
import com.Cabmeri3.Nasxil.domain.model.GameSettings
import com.Cabmeri3.Nasxil.domain.model.NightMode
import com.Cabmeri3.Nasxil.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val storage: RunPreferences) : SettingsRepository {

    override fun load(): GameSettings {
        val modeName = storage.text(KEY_NIGHT, NightMode.AUTO.name)
        val mode = NightMode.entries.firstOrNull { it.name == modeName } ?: NightMode.AUTO
        return GameSettings(
            nightMode = mode,
            ghostEnabled = storage.bool(KEY_GHOST, true),
            rookieAssist = storage.bool(KEY_ROOKIE, false)
        )
    }

    override fun save(settings: GameSettings) {
        storage.putText(KEY_NIGHT, settings.nightMode.name)
        storage.putBool(KEY_GHOST, settings.ghostEnabled)
        storage.putBool(KEY_ROOKIE, settings.rookieAssist)
    }

    private companion object {
        const val KEY_NIGHT = "night_mode"
        const val KEY_GHOST = "ghost_enabled"
        const val KEY_ROOKIE = "rookie_assist"
    }
}
