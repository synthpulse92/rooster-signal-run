package com.Cabmeri3.Nasxil.domain.usecase

import com.Cabmeri3.Nasxil.core.config.GameConfig
import com.Cabmeri3.Nasxil.domain.model.LayerType
import com.Cabmeri3.Nasxil.domain.model.RouteLayer
import kotlin.math.roundToInt
import kotlin.random.Random

data class RouteParams(
    val seed: Int,
    val alwaysNight: Boolean,
    val rookieAssist: Boolean,
    val masteryWindowMs: Map<LayerType, Int>
)

class GenerateRouteUseCase {

    operator fun invoke(params: RouteParams, fromIndex: Int, count: Int): List<RouteLayer> {
        val result = ArrayList<RouteLayer>(count)
        for (offset in 0 until count) {
            val index = fromIndex + offset
            val random = Random(params.seed xor (index * GameConfig.SEED_B))
            var layer: RouteLayer? = null
            var attempt = 0
            while (attempt < GameConfig.GENERATOR_ATTEMPTS && layer == null) {
                val candidate = build(random, index, params)
                if (crossable(candidate)) layer = candidate
                attempt++
            }
            result.add(layer ?: fallback(index))
        }
        return result
    }

    fun rainStep(index: Int): Int =
        (index / GameConfig.RAIN_EVERY_LAYERS).coerceAtMost(GameConfig.RAIN_MAX_STEP)

    fun nightAt(index: Int, alwaysNight: Boolean, nightDisabled: Boolean): Boolean = when {
        nightDisabled -> false
        alwaysNight -> true
        else -> index >= GameConfig.NIGHT_FROM_LAYER
    }

    private fun build(random: Random, index: Int, params: RouteParams): RouteLayer {
        val type = typeFor(index, random)
        val green = greenWindow(index, type, params)
        return when (type) {
            LayerType.ROAD -> RouteLayer(
                index = index,
                type = type,
                greenMs = green,
                phaseOffsetMs = random.nextInt(0, 2600),
                brokenLamp = index > 5 && random.nextInt(100) < 16,
                vanCount = random.nextInt(1, 4),
                vanToRight = random.nextBoolean(),
                hasShortcut = random.nextInt(100) < 40
            )
            LayerType.RAIL -> RouteLayer(
                index = index,
                type = type,
                greenMs = green,
                phaseOffsetMs = random.nextInt(0, GameConfig.GATE_CYCLE_MS),
                vanCount = 0,
                hasShortcut = random.nextInt(100) < 25
            )
            LayerType.FERRY -> RouteLayer(
                index = index,
                type = type,
                greenMs = green,
                phaseOffsetMs = random.nextInt(0, 2000),
                ferryPeriodMs = ferryPeriod(index, random),
                ferryPhase = random.nextFloat(),
                hasShortcut = random.nextInt(100) < 20
            )
        }
    }

    private fun typeFor(index: Int, random: Random): LayerType {
        if (index < 2) return LayerType.ROAD
        val slot = index % 9
        return when {
            slot == 4 || slot == 5 -> LayerType.RAIL
            slot == 8 -> LayerType.FERRY
            slot == 7 && random.nextInt(100) < 45 -> LayerType.FERRY
            slot == 6 && random.nextInt(100) < 30 -> LayerType.RAIL
            else -> LayerType.ROAD
        }
    }

    private fun greenWindow(index: Int, type: LayerType, params: RouteParams): Int {
        val decay = GameConfig.GREEN_MS_DECAY * (index / GameConfig.GREEN_DECAY_EVERY)
        var window = (GameConfig.GREEN_MS_BASE - decay).coerceAtLeast(GameConfig.GREEN_MS_FLOOR).toFloat()
        if (params.alwaysNight || index >= GameConfig.NIGHT_FROM_LAYER) {
            window *= GameConfig.NIGHT_WINDOW_FACTOR
        }
        window *= (1f - GameConfig.RAIN_WINDOW_PENALTY * rainStep(index))
        var total = window.roundToInt() + (params.masteryWindowMs[type] ?: 0)
        if (params.rookieAssist) total += GameConfig.ROOKIE_BONUS_MS
        return total.coerceAtLeast(GameConfig.MIN_SAFE_WINDOW_MS)
    }

    private fun ferryPeriod(index: Int, random: Random): Int {
        val base = random.nextInt(GameConfig.FERRY_PERIOD_MIN_MS, GameConfig.FERRY_PERIOD_MAX_MS)
        val faster = base * (1f - GameConfig.RAIN_DRIFT_GAIN * rainStep(index))
        return faster.roundToInt().coerceAtLeast(1400)
    }

    private fun crossable(layer: RouteLayer): Boolean = when (layer.type) {
        LayerType.ROAD -> layer.brokenLamp || layer.greenMs >= GameConfig.MIN_SAFE_WINDOW_MS
        LayerType.RAIL -> GameConfig.GATE_UP_MS >= GameConfig.MIN_SAFE_WINDOW_MS
        LayerType.FERRY -> layer.ferryPeriodMs >= 1400
    }

    private fun fallback(index: Int): RouteLayer = RouteLayer(
        index = index,
        type = LayerType.ROAD,
        greenMs = GameConfig.GREEN_MS_BASE,
        phaseOffsetMs = 0,
        brokenLamp = false,
        vanCount = 1,
        vanToRight = true,
        hasShortcut = false
    )
}
