package com.Cabmeri3.Nasxil.domain.repository

import com.Cabmeri3.Nasxil.domain.model.PlayerProgress

interface ProgressRepository {
    fun load(): PlayerProgress
    fun save(progress: PlayerProgress)
    fun clear()
}
