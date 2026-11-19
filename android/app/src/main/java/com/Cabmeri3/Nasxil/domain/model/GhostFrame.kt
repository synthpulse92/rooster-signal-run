package com.Cabmeri3.Nasxil.domain.model

data class GhostFrame(
    val layerIndex: Int,
    val elapsedMs: Long
)

object GhostTrack {
    fun layerAt(frames: List<GhostFrame>, elapsedMs: Long): Int {
        if (frames.isEmpty()) return -1
        var result = 0
        for (frame in frames) {
            if (frame.elapsedMs > elapsedMs) break
            result = frame.layerIndex
        }
        return result
    }
}
