package com.Cabmeri3.Nasxil.data.repository

import com.Cabmeri3.Nasxil.data.local.RunPreferences
import com.Cabmeri3.Nasxil.domain.model.GhostFrame
import com.Cabmeri3.Nasxil.domain.repository.GhostRepository

class GhostRepositoryImpl(private val storage: RunPreferences) : GhostRepository {

    override fun load(): List<GhostFrame> {
        val raw = storage.text(KEY_GHOST, "")
        if (raw.isBlank()) return emptyList()
        return raw.split(FRAME_SEPARATOR).mapNotNull { chunk ->
            val parts = chunk.split(FIELD_SEPARATOR)
            if (parts.size != 2) return@mapNotNull null
            val layer = parts[0].toIntOrNull() ?: return@mapNotNull null
            val elapsed = parts[1].toLongOrNull() ?: return@mapNotNull null
            GhostFrame(layer, elapsed)
        }
    }

    override fun save(frames: List<GhostFrame>) {
        val raw = frames.joinToString(FRAME_SEPARATOR) { it.layerIndex.toString() + FIELD_SEPARATOR + it.elapsedMs }
        storage.putText(KEY_GHOST, raw)
    }

    override fun clear() {
        storage.remove(listOf(KEY_GHOST))
    }

    private companion object {
        const val KEY_GHOST = "ghost_track"
        const val FRAME_SEPARATOR = ";"
        const val FIELD_SEPARATOR = ":"
    }
}
