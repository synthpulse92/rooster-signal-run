package com.Cabmeri3.Nasxil.data.repository

import com.Cabmeri3.Nasxil.data.local.RunPreferences
import com.Cabmeri3.Nasxil.domain.model.LayerType
import com.Cabmeri3.Nasxil.domain.model.PlayerProgress
import com.Cabmeri3.Nasxil.domain.model.UpgradeId
import com.Cabmeri3.Nasxil.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val storage: RunPreferences) : ProgressRepository {

    override fun load(): PlayerProgress {
        val ownedRaw = storage.text(KEY_OWNED, "")
        val owned = ownedRaw.split(SEPARATOR)
            .filter { it.isNotBlank() }
            .mapNotNull { name -> UpgradeId.entries.firstOrNull { it.name == name } }
            .toSet()
        val mastery = LayerType.entries.associateWith { storage.int(KEY_MASTERY + it.name, 0) }
        return PlayerProgress(
            bestDistance = storage.int(KEY_BEST_DISTANCE, 0),
            bestChain = storage.int(KEY_BEST_CHAIN, 0),
            bestScore = storage.int(KEY_BEST_SCORE, 0),
            stamps = storage.int(KEY_STAMPS, 0),
            runCount = storage.int(KEY_RUNS, 0),
            owned = owned,
            mastery = mastery
        )
    }

    override fun save(progress: PlayerProgress) {
        storage.putInt(KEY_BEST_DISTANCE, progress.bestDistance)
        storage.putInt(KEY_BEST_CHAIN, progress.bestChain)
        storage.putInt(KEY_BEST_SCORE, progress.bestScore)
        storage.putInt(KEY_STAMPS, progress.stamps)
        storage.putInt(KEY_RUNS, progress.runCount)
        storage.putText(KEY_OWNED, progress.owned.joinToString(SEPARATOR) { it.name })
        LayerType.entries.forEach { type ->
            storage.putInt(KEY_MASTERY + type.name, progress.xpOf(type))
        }
    }

    override fun clear() {
        val keys = mutableListOf(
            KEY_BEST_DISTANCE,
            KEY_BEST_CHAIN,
            KEY_BEST_SCORE,
            KEY_STAMPS,
            KEY_RUNS,
            KEY_OWNED
        )
        LayerType.entries.forEach { keys.add(KEY_MASTERY + it.name) }
        storage.remove(keys)
    }

    private companion object {
        const val SEPARATOR = ","
        const val KEY_BEST_DISTANCE = "best_distance"
        const val KEY_BEST_CHAIN = "best_chain"
        const val KEY_BEST_SCORE = "best_score"
        const val KEY_STAMPS = "stamps"
        const val KEY_RUNS = "run_count"
        const val KEY_OWNED = "owned_upgrades"
        const val KEY_MASTERY = "mastery_"
    }
}
