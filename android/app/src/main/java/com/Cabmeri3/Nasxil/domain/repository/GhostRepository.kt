package com.Cabmeri3.Nasxil.domain.repository

import com.Cabmeri3.Nasxil.domain.model.GhostFrame

interface GhostRepository {
    fun load(): List<GhostFrame>
    fun save(frames: List<GhostFrame>)
    fun clear()
}
