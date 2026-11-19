package com.Cabmeri3.Nasxil.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val TICK_MS = 16L

    const val LAYER_HEIGHT_DP = 92
    const val LAYERS_AHEAD = 5
    const val LAYERS_BEHIND = 1
    const val ROUTE_CHUNK = 40

    const val RED_MS = 1500
    const val AMBER_MS = 600
    const val GREEN_MS_BASE = 1100
    const val GREEN_MS_FLOOR = 420
    const val GREEN_MS_DECAY = 45
    const val GREEN_DECAY_EVERY = 3
    const val PERFECT_WINDOW_MS = 320

    const val GATE_CYCLE_MS = 3400
    const val GATE_UP_MS = 1900
    const val GATE_LOWER_MS = 700

    const val FERRY_PERIOD_MIN_MS = 2200
    const val FERRY_PERIOD_MAX_MS = 3600
    const val FERRY_WINDOW = 0.30f
    const val FERRY_CLEATS_BONUS = 1.25f

    const val DASH_MS = 180
    const val SLIDE_MS = 520
    const val FAIL_HOLD_MS = 1100

    const val CHAIN_FOR_SHORTCUT = 4
    const val SHORTCUT_WINDOW_MS = 2200
    const val SHORTCUT_SKIP = 3
    const val SHORTCUT_STAMPS = 3

    const val OVERDRIVE_GAIN = 25
    const val OVERDRIVE_MAX = 100
    const val OVERDRIVE_MS = 6000
    const val OVERDRIVE_TIME_SCALE = 0.5f

    const val NIGHT_FROM_LAYER = 12
    const val NIGHT_WINDOW_FACTOR = 0.85f
    const val NIGHT_STAMP_BONUS = 1

    const val RAIN_EVERY_LAYERS = 6
    const val RAIN_MAX_STEP = 3
    const val RAIN_WINDOW_PENALTY = 0.08f
    const val RAIN_DRIFT_GAIN = 0.15f

    const val ROOKIE_BONUS_MS = 180
    const val ROOKIE_SCORE_CAP = 1.5f

    const val MASTERY_WINDOW_MS = 20
    const val MASTERY_SCORE_STEP = 0.02f

    const val GENERATOR_ATTEMPTS = 20
    const val MIN_SAFE_WINDOW_MS = 300
    const val SEED_A = 7919
    const val SEED_B = 104729
}
