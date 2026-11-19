package com.Cabmeri3.Nasxil.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Cabmeri3.Nasxil.core.config.GameConfig
import com.Cabmeri3.Nasxil.domain.model.CrossAction
import com.Cabmeri3.Nasxil.domain.model.CrossOutcome
import com.Cabmeri3.Nasxil.domain.model.FailCause
import com.Cabmeri3.Nasxil.domain.model.GameSettings
import com.Cabmeri3.Nasxil.domain.model.GhostFrame
import com.Cabmeri3.Nasxil.domain.model.GhostTrack
import com.Cabmeri3.Nasxil.domain.model.LayerType
import com.Cabmeri3.Nasxil.domain.model.MasteryTier
import com.Cabmeri3.Nasxil.domain.model.NightMode
import com.Cabmeri3.Nasxil.domain.model.PlayerProgress
import com.Cabmeri3.Nasxil.domain.model.RunSummary
import com.Cabmeri3.Nasxil.domain.model.UpgradeId
import com.Cabmeri3.Nasxil.domain.repository.GhostRepository
import com.Cabmeri3.Nasxil.domain.repository.ProgressRepository
import com.Cabmeri3.Nasxil.domain.repository.SettingsRepository
import com.Cabmeri3.Nasxil.domain.usecase.GenerateRouteUseCase
import com.Cabmeri3.Nasxil.domain.usecase.ResolveCrossingUseCase
import com.Cabmeri3.Nasxil.domain.usecase.RouteParams
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

class GameViewModel(
    private val progressStore: ProgressRepository,
    private val settingsStore: SettingsRepository,
    private val ghostStore: GhostRepository,
    private val generateRoute: GenerateRouteUseCase,
    private val resolveCrossing: ResolveCrossingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var tickJob: Job? = null
    private var active = false
    private var settings = GameSettings()
    private var progress = PlayerProgress()
    private var ghostTrack: List<GhostFrame> = emptyList()
    private val recorded = mutableListOf<GhostFrame>()
    private val masteryGain = mutableMapOf<LayerType, Int>()
    private var params = RouteParams(0, false, false, emptyMap())

    fun beginRun() {
        if (!active) {
            startRun()
        } else if (!_state.value.paused) {
            startLoop()
        }
    }

    fun pause() {
        stopLoop()
    }

    fun pauseByPlayer() {
        if (!active) return
        stopLoop()
        _state.update { it.copy(paused = true) }
    }

    fun resume() {
        if (!active) return
        _state.update { it.copy(paused = false) }
        startLoop()
    }

    fun restart() {
        stopLoop()
        active = false
        startRun()
    }

    fun tap() {
        cross(CrossAction.DASH)
    }

    fun slide() {
        cross(CrossAction.SLIDE)
    }

    fun activateOverdrive() {
        val current = _state.value
        if (!active || current.paused) return
        if (current.overdrive < GameConfig.OVERDRIVE_MAX || current.overdriveActive) return
        _state.update {
            it.copy(
                overdrive = 0,
                overdriveActive = true,
                overdriveLeftMs = GameConfig.OVERDRIVE_MS
            )
        }
    }

    override fun onCleared() {
        stopLoop()
        super.onCleared()
    }

    private fun startRun() {
        settings = settingsStore.load()
        progress = progressStore.load()
        ghostTrack = ghostStore.load()
        recorded.clear()
        masteryGain.clear()
        val seed = ((progress.runCount + 1) * GameConfig.SEED_A) xor (progress.bestDistance + 1) * GameConfig.SEED_B
        val alwaysNight = settings.nightMode == NightMode.ALWAYS_ON
        params = RouteParams(
            seed = seed,
            alwaysNight = alwaysNight,
            rookieAssist = settings.rookieAssist,
            masteryWindowMs = LayerType.entries.associateWith { MasteryTier.windowBonusMs(progress.xpOf(it)) }
        )
        var multiplier = 1f + GameConfig.MASTERY_SCORE_STEP * progress.totalTiers()
        if (settings.rookieAssist) multiplier = multiplier.coerceAtMost(GameConfig.ROOKIE_SCORE_CAP)
        active = true
        _state.value = GameUiState(
            layers = generateRoute(params, 0, GameConfig.ROUTE_CHUNK),
            layerIndex = 0,
            night = alwaysNight,
            ghostVisible = settings.ghostEnabled && ghostTrack.isNotEmpty(),
            vestCharges = if (progress.owned.contains(UpgradeId.REFLECTIVE_VEST)) 1 else 0,
            lampTimerOwned = progress.owned.contains(UpgradeId.LAMP_TIMER),
            ferryWindow = if (progress.owned.contains(UpgradeId.FERRY_CLEATS)) {
                GameConfig.FERRY_WINDOW * GameConfig.FERRY_CLEATS_BONUS
            } else {
                GameConfig.FERRY_WINDOW
            },
            scoreMultiplier = multiplier,
            seed = seed
        )
        startLoop()
    }

    private fun startLoop() {
        if (tickJob?.isActive == true) return
        tickJob = viewModelScope.launch {
            var previous = System.nanoTime()
            while (isActive) {
                delay(GameConfig.TICK_MS)
                val now = System.nanoTime()
                val deltaMs = ((now - previous) / 1_000_000L).coerceIn(1L, 64L)
                previous = now
                advance(deltaMs)
            }
        }
    }

    private fun stopLoop() {
        tickJob?.cancel()
        tickJob = null
    }

    private fun advance(deltaMs: Long) {
        val current = _state.value
        if (current.paused) return
        if (current.phase == RunPhase.FAILING) {
            val left = current.failLeftMs - deltaMs.toInt()
            if (left <= 0) {
                stopLoop()
                eventChannel.trySend(GameEvent.Finished(buildSummary(current)))
            } else {
                _state.update { it.copy(failLeftMs = left) }
            }
            return
        }
        val scale = if (current.overdriveActive) GameConfig.OVERDRIVE_TIME_SCALE else 1f
        val overdriveLeft = (current.overdriveLeftMs - deltaMs.toInt()).coerceAtLeast(0)
        val shortcutLeft = (current.shortcutLeftMs - deltaMs.toInt()).coerceAtLeast(0)
        val elapsed = current.elapsedMs + deltaMs
        _state.update {
            it.copy(
                clockMs = it.clockMs + (deltaMs * scale).toLong(),
                elapsedMs = elapsed,
                overdriveActive = it.overdriveActive && overdriveLeft > 0,
                overdriveLeftMs = overdriveLeft,
                shortcutArmed = it.shortcutArmed && shortcutLeft > 0,
                shortcutLeftMs = shortcutLeft,
                slideLeftMs = (it.slideLeftMs - deltaMs.toInt()).coerceAtLeast(0),
                ghostLayer = if (it.ghostVisible) GhostTrack.layerAt(ghostTrack, elapsed) else -1
            )
        }
    }

    private fun cross(action: CrossAction) {
        val current = _state.value
        if (!active || current.paused || current.phase == RunPhase.FAILING) return
        val target = current.layers.getOrNull(current.layerIndex + 1) ?: return
        val result = resolveCrossing(
            layer = target,
            clockMs = current.clockMs,
            action = action,
            ferryWindow = current.ferryWindow,
            vestAvailable = current.vestCharges > 0
        )
        if (result.outcome == CrossOutcome.FAIL) {
            fail(result.failCause ?: FailCause.RED_LIGHT, current)
            return
        }
        val takesShortcut = current.shortcutArmed && target.hasShortcut && action == CrossAction.DASH
        val step = if (takesShortcut) GameConfig.SHORTCUT_SKIP else 1
        val nextIndex = current.layerIndex + step
        val chain = if (result.outcome == CrossOutcome.PERFECT) {
            current.chain + 1
        } else if (takesShortcut) {
            current.chain
        } else {
            0
        }
        val baseScore = when (result.outcome) {
            CrossOutcome.PERFECT -> 3
            CrossOutcome.CLEAN -> 2
            else -> 1
        }
        var gainedStamps = when (result.outcome) {
            CrossOutcome.PERFECT -> 2
            CrossOutcome.CLEAN -> 1
            else -> 0
        }
        if (current.night && gainedStamps > 0) gainedStamps += GameConfig.NIGHT_STAMP_BONUS
        if (takesShortcut) gainedStamps += GameConfig.SHORTCUT_STAMPS
        val xp = if (result.outcome == CrossOutcome.PERFECT) 2 else 1
        masteryGain[target.type] = (masteryGain[target.type] ?: 0) + xp
        recorded.add(GhostFrame(nextIndex, current.elapsedMs))
        val overdrive = if (result.outcome == CrossOutcome.PERFECT) {
            (current.overdrive + GameConfig.OVERDRIVE_GAIN).coerceAtMost(GameConfig.OVERDRIVE_MAX)
        } else {
            current.overdrive
        }
        val armShortcut = chain >= GameConfig.CHAIN_FOR_SHORTCUT &&
            current.layers.getOrNull(nextIndex + 1)?.hasShortcut == true
        val layers = extendIfNeeded(current.layers, nextIndex)
        val nightOn = when (settings.nightMode) {
            NightMode.OFF -> false
            NightMode.ALWAYS_ON -> true
            NightMode.AUTO -> nextIndex >= GameConfig.NIGHT_FROM_LAYER
        }
        _state.update {
            it.copy(
                layers = layers,
                layerIndex = nextIndex,
                score = it.score + (baseScore * it.scoreMultiplier).roundToInt(),
                stamps = it.stamps + gainedStamps,
                chain = chain,
                bestChain = max(it.bestChain, chain),
                overdrive = overdrive,
                rainStep = generateRoute.rainStep(nextIndex),
                night = nightOn,
                shortcutArmed = armShortcut,
                shortcutLeftMs = if (armShortcut) GameConfig.SHORTCUT_WINDOW_MS else 0,
                slideLeftMs = if (action == CrossAction.SLIDE) GameConfig.SLIDE_MS else 0,
                vestCharges = if (result.forgiven) it.vestCharges - 1 else it.vestCharges,
                lastOutcome = result.outcome
            )
        }
    }

    private fun extendIfNeeded(layers: List<com.Cabmeri3.Nasxil.domain.model.RouteLayer>, index: Int) =
        if (index + GameConfig.LAYERS_AHEAD + 2 >= layers.size) {
            layers + generateRoute(params, layers.size, GameConfig.ROUTE_CHUNK)
        } else {
            layers
        }

    private fun fail(cause: FailCause, current: GameUiState) {
        active = false
        persist(current)
        _state.update {
            it.copy(
                phase = RunPhase.FAILING,
                failCause = cause,
                failLeftMs = GameConfig.FAIL_HOLD_MS,
                lastOutcome = CrossOutcome.FAIL,
                shortcutArmed = false,
                overdriveActive = false
            )
        }
    }

    private fun persist(current: GameUiState) {
        val distance = current.layerIndex
        val newRecord = distance > progress.bestDistance
        val mergedMastery = LayerType.entries.associateWith {
            progress.xpOf(it) + (masteryGain[it] ?: 0)
        }
        val updated = progress.copy(
            bestDistance = max(progress.bestDistance, distance),
            bestChain = max(progress.bestChain, current.bestChain),
            bestScore = max(progress.bestScore, current.score),
            stamps = progress.stamps + current.stamps,
            runCount = progress.runCount + 1,
            mastery = mergedMastery
        )
        progressStore.save(updated)
        if (newRecord && recorded.isNotEmpty()) {
            ghostStore.save(recorded.toList())
        }
    }

    private fun buildSummary(current: GameUiState): RunSummary {
        val distance = current.layerIndex
        val tierGain = LayerType.entries.sumOf {
            MasteryTier.tierOf(progress.xpOf(it) + (masteryGain[it] ?: 0)) -
                MasteryTier.tierOf(progress.xpOf(it))
        }
        val ghostDelta = if (ghostTrack.isNotEmpty() && current.ghostVisible) {
            distance - GhostTrack.layerAt(ghostTrack, current.elapsedMs)
        } else {
            null
        }
        return RunSummary(
            distance = distance,
            stamps = current.stamps,
            score = current.score,
            bestChain = current.bestChain,
            masteryGain = tierGain,
            newRecord = distance > progress.bestDistance,
            ghostDelta = ghostDelta,
            failCause = current.failCause ?: FailCause.RED_LIGHT
        )
    }
}
