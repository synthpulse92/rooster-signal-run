package com.Cabmeri3.Nasxil.domain.repository

import com.Cabmeri3.Nasxil.domain.model.GameSettings

interface SettingsRepository {
    fun load(): GameSettings
    fun save(settings: GameSettings)
}
