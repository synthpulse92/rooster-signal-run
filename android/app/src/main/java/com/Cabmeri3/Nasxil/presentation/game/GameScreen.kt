package com.Cabmeri3.Nasxil.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Cabmeri3.Nasxil.R
import com.Cabmeri3.Nasxil.core.config.GameConfig
import com.Cabmeri3.Nasxil.core.di.ServiceLocator
import com.Cabmeri3.Nasxil.core.theme.FeatherRed
import com.Cabmeri3.Nasxil.core.theme.FerryWater
import com.Cabmeri3.Nasxil.core.theme.GoGreen
import com.Cabmeri3.Nasxil.core.theme.NightBase
import com.Cabmeri3.Nasxil.core.theme.NightSurface
import com.Cabmeri3.Nasxil.core.theme.NightSurfaceAlt
import com.Cabmeri3.Nasxil.core.theme.NightWash
import com.Cabmeri3.Nasxil.core.theme.RoadNavy
import com.Cabmeri3.Nasxil.core.theme.SignalAmber
import com.Cabmeri3.Nasxil.core.theme.TextMuted
import com.Cabmeri3.Nasxil.core.theme.TextSecondary
import com.Cabmeri3.Nasxil.core.theme.WaterCyan
import com.Cabmeri3.Nasxil.core.ui.HudFrame
import com.Cabmeri3.Nasxil.core.ui.PrimaryButton
import com.Cabmeri3.Nasxil.core.ui.ScreenBackground
import com.Cabmeri3.Nasxil.core.ui.SignalLamp
import com.Cabmeri3.Nasxil.domain.model.GateState
import com.Cabmeri3.Nasxil.domain.model.LampPhase
import com.Cabmeri3.Nasxil.domain.model.LayerType
import com.Cabmeri3.Nasxil.domain.model.RouteLayer
import com.Cabmeri3.Nasxil.domain.model.RunSummary
import com.Cabmeri3.Nasxil.domain.model.SignalClock
import com.Cabmeri3.Nasxil.presentation.dialog.PauseDialog
import kotlin.math.abs
import kotlin.random.Random

private val BASE_FROM_BOTTOM = 226.dp

@Composable
fun GameRoute(
    onGameOver: (RunSummary) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = ServiceLocator.factory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        gameViewModel.beginRun()
        onDispose { gameViewModel.pause() }
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onGameOver(event.summary)
            }
        }
    }

    GameScreen(
        state = state,
        onTap = gameViewModel::tap,
        onSlide = gameViewModel::slide,
        onOverdrive = gameViewModel::activateOverdrive,
        onPause = gameViewModel::pauseByPlayer,
        onResume = gameViewModel::resume,
        onRestart = gameViewModel::restart,
        onMenu = onMenu
    )
}

@Composable
fun GameScreen(
    state: GameUiState,
    onTap: () -> Unit,
    onSlide: () -> Unit,
    onOverdrive: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    val overlay = if (state.night) {
        listOf(
            NightBase.copy(alpha = 0.90f),
            RoadNavy.copy(alpha = 0.74f),
            NightBase.copy(alpha = 0.94f)
        )
    } else {
        listOf(
            NightBase.copy(alpha = 0.78f),
            RoadNavy.copy(alpha = 0.62f),
            NightBase.copy(alpha = 0.88f)
        )
    }

    ScreenBackground(
        overlay = overlay,
        wash = if (state.night) NightWash.copy(alpha = 0.06f) else Color.Transparent
    ) {
        RouteStack(state = state, modifier = Modifier.fillMaxSize())
        RainCurtain(step = state.rainStep, modifier = Modifier.fillMaxSize())
        OverdriveVignette(active = state.overdriveActive, modifier = Modifier.fillMaxSize())
        FailFlash(state = state, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { onTap() }
                }
                .pointerInput(Unit) {
                    var fired = false
                    detectVerticalDragGestures(
                        onDragStart = { fired = false },
                        onDragEnd = { fired = false },
                        onDragCancel = { fired = false }
                    ) { _, drag ->
                        if (!fired && drag > 6f) {
                            fired = true
                            onSlide()
                        }
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            HudBar(state = state, onPause = onPause)
            Spacer(modifier = Modifier.weight(1f))
            ControlHint()
            Spacer(modifier = Modifier.height(10.dp))
            ControlBar(state = state, onTap = onTap, onOverdrive = onOverdrive)
        }

        if (state.paused) {
            PauseDialog(
                onResume = onResume,
                onRestart = onRestart,
                onMenu = onMenu
            )
        }
    }
}

@Composable
private fun RouteStack(state: GameUiState, modifier: Modifier) {
    if (state.layers.isEmpty()) return
    val camera by animateFloatAsState(
        targetValue = state.layerIndex.toFloat(),
        animationSpec = tween(GameConfig.DASH_MS, easing = FastOutSlowInEasing),
        label = "camera"
    )
    val bandHeight = GameConfig.LAYER_HEIGHT_DP.dp
    val first = (state.layerIndex - GameConfig.LAYERS_BEHIND).coerceAtLeast(0)
    val last = (state.layerIndex + GameConfig.LAYERS_AHEAD).coerceAtMost(state.layers.lastIndex)
    val sliding = state.slideLeftMs > 0
    val hopping = abs(camera - state.layerIndex) > 0.02f

    Box(modifier = modifier.clipToBounds()) {
        for (position in first..last) {
            val layer = state.layers[position]
            LayerBand(
                layer = layer,
                clockMs = state.clockMs,
                lampTimer = state.lampTimerOwned,
                shortcut = state.shortcutArmed && layer.index == state.layerIndex + 1 && layer.hasShortcut,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(bandHeight)
                    .offset(y = -(BASE_FROM_BOTTOM + bandHeight * (layer.index - camera)))
            )
        }

        if (state.ghostVisible && state.ghostLayer >= 0) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(FeatherRed, BlendMode.SrcAtop),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(
                        x = 52.dp,
                        y = -(BASE_FROM_BOTTOM + 14.dp + bandHeight * (state.ghostLayer - camera))
                    )
                    .size(48.dp)
                    .alpha(0.32f)
            )
        }

        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_rooster),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -(BASE_FROM_BOTTOM + 14.dp))
                .size(58.dp)
                .graphicsLayer(
                    scaleY = if (sliding) 0.70f else if (hopping) 1.08f else 1f,
                    scaleX = if (sliding) 1.18f else 1f
                )
        )
    }
}

@Composable
private fun LayerBand(
    layer: RouteLayer,
    clockMs: Long,
    lampTimer: Boolean,
    shortcut: Boolean,
    modifier: Modifier
) {
    Box(modifier = modifier.clipToBounds()) {
        when (layer.type) {
            LayerType.ROAD -> RoadBand(layer, clockMs, lampTimer)
            LayerType.RAIL -> RailBand(layer, clockMs)
            LayerType.FERRY -> FerryBand(layer, clockMs)
        }
        if (shortcut) {
            ShortcutGate(modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}

@Composable
private fun RoadBand(layer: RouteLayer, clockMs: Long, lampTimer: Boolean) {
    val phase = SignalClock.lampPhase(layer, clockMs)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoadNavy.copy(alpha = 0.92f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dash = PathEffect.dashPathEffect(floatArrayOf(18f, 18f))
            drawLine(
                color = TextSecondary.copy(alpha = 0.25f),
                start = Offset(0f, size.height * 0.34f),
                end = Offset(size.width, size.height * 0.34f),
                strokeWidth = 2f,
                pathEffect = dash
            )
            drawLine(
                color = TextSecondary.copy(alpha = 0.25f),
                start = Offset(0f, size.height * 0.68f),
                end = Offset(size.width, size.height * 0.68f),
                strokeWidth = 2f,
                pathEffect = dash
            )
        }

        if (SignalClock.vansVisible(layer, clockMs)) {
            for (slot in 0 until layer.vanCount) {
                val shift = SignalClock.vanOffset(layer, slot, clockMs)
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (shift * 150f).dp, y = ((slot % 2) * 18 - 9).dp)
                        .size(58.dp)
                        .graphicsLayer(scaleX = if (layer.vanToRight) 1f else -1f)
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (lampTimer && phase == LampPhase.GREEN) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(SignalAmber.copy(alpha = 0.75f), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            SignalLamp(
                phase = phase,
                diameter = 18.dp,
                intensity = if (layer.brokenLamp) 0f else 1f
            )
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                painter = painterResource(R.drawable.sprite_2),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(52.dp)
                    .alpha(if (layer.brokenLamp) 0.45f else 1f)
            )
        }
    }
}

@Composable
private fun RailBand(layer: RouteLayer, clockMs: Long) {
    val angle = SignalClock.gateAngle(layer, clockMs)
    val state = SignalClock.gateState(layer, clockMs)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightSurfaceAlt.copy(alpha = 0.94f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = TextMuted.copy(alpha = 0.55f),
                start = Offset(0f, size.height * 0.40f),
                end = Offset(size.width, size.height * 0.40f),
                strokeWidth = 4f
            )
            drawLine(
                color = TextMuted.copy(alpha = 0.55f),
                start = Offset(0f, size.height * 0.62f),
                end = Offset(size.width, size.height * 0.62f),
                strokeWidth = 4f
            )
        }
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
                .size(72.dp)
                .rotate(angle)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp)
                .size(14.dp)
                .background(
                    if (state == GateState.UP) GoGreen else FeatherRed,
                    CircleShape
                )
        )
    }
}

@Composable
private fun FerryBand(layer: RouteLayer, clockMs: Long) {
    val shift = SignalClock.ferryOffset(layer, clockMs)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FerryWater.copy(alpha = 0.94f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = WaterCyan.copy(alpha = 0.22f),
                start = Offset(0f, size.height * 0.30f),
                end = Offset(size.width, size.height * 0.30f),
                strokeWidth = 3f
            )
            drawLine(
                color = WaterCyan.copy(alpha = 0.16f),
                start = Offset(0f, size.height * 0.72f),
                end = Offset(size.width, size.height * 0.72f),
                strokeWidth = 3f
            )
        }
        Image(
            painter = painterResource(R.drawable.sprite_5),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (shift * 120f).dp)
                .size(96.dp)
        )
    }
}

@Composable
private fun ShortcutGate(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "shortcut")
    val glow by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse),
        label = "shortcutGlow"
    )
    Box(
        modifier = modifier
            .padding(end = 6.dp)
            .height(64.dp)
            .width(96.dp)
            .border(2.dp, SignalAmber.copy(alpha = glow), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.shortcut),
            style = MaterialTheme.typography.labelSmall,
            color = SignalAmber
        )
    }
}

@Composable
private fun RainCurtain(step: Int, modifier: Modifier) {
    if (step <= 0) return
    val transition = rememberInfiniteTransition(label = "rain")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "drift"
    )
    val drops = when (step) {
        1 -> 40
        2 -> 80
        else -> 130
    }
    Canvas(modifier = modifier) {
        val random = Random(7919)
        val alpha = 0.08f + 0.035f * step
        for (index in 0 until drops) {
            val x = random.nextFloat() * size.width
            val base = random.nextFloat()
            val y = ((base + drift) % 1f) * size.height
            drawLine(
                color = TextSecondary.copy(alpha = alpha),
                start = Offset(x, y),
                end = Offset(x - 9f, y + 26f),
                strokeWidth = 1.4f
            )
        }
    }
}

@Composable
private fun OverdriveVignette(active: Boolean, modifier: Modifier) {
    if (!active) return
    val transition = rememberInfiniteTransition(label = "overdrive")
    val pulse by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(tween(830, easing = LinearEasing), RepeatMode.Reverse),
        label = "overdrivePulse"
    )
    Box(
        modifier = modifier.background(
            Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    SignalAmber.copy(alpha = pulse)
                )
            )
        )
    )
}

@Composable
private fun FailFlash(state: GameUiState, modifier: Modifier) {
    if (state.phase != RunPhase.FAILING) return
    val progress = 1f - (state.failLeftMs.toFloat() / GameConfig.FAIL_HOLD_MS)
    val flash = (0.55f * (1f - progress * 3f)).coerceAtLeast(0f)
    Box(
        modifier = modifier.background(FeatherRed.copy(alpha = flash)),
        contentAlignment = Alignment.Center
    ) {
        for (index in 0 until 3) {
            val spread = (index - 1) * 48f * progress
            Image(
                painter = painterResource(R.drawable.ic_feather),
                contentDescription = null,
                colorFilter = ColorFilter.tint(FeatherRed),
                modifier = Modifier
                    .offset(x = spread.dp, y = (-70f * progress).dp)
                    .size(30.dp)
                    .alpha((1f - progress).coerceIn(0f, 1f))
                    .rotate(spread * 1.6f)
            )
        }
    }
}

@Composable
private fun HudBar(state: GameUiState, onPause: () -> Unit) {
    HudFrame(
        modifier = Modifier.fillMaxWidth(),
        fill = NightSurface.copy(alpha = 0.78f),
        corner = 14.dp,
        bracket = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPause,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = stringResource(R.string.cd_pause),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (index in 0 until GameConfig.CHAIN_FOR_SHORTCUT) {
                        val filled = index < state.chain
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .rotate(45f)
                                .background(
                                    if (filled) SignalAmber else SignalAmber.copy(alpha = 0.18f)
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.layer_label,
                        state.layerIndex,
                        stringResource(layerTypeLabel(state))
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = state.layerIndex.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SignalAmber,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                Text(
                    text = stringResource(R.string.run_stamps, state.stamps),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

private fun layerTypeLabel(state: GameUiState): Int {
    val layer = state.layers.getOrNull(state.layerIndex)
    return when (layer?.type) {
        LayerType.RAIL -> R.string.type_rail
        LayerType.FERRY -> R.string.type_ferry
        else -> R.string.type_road
    }
}

@Composable
private fun ControlHint() {
    Text(
        text = stringResource(R.string.control_hint),
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(NightSurface.copy(alpha = 0.70f), RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp)
    )
}

@Composable
private fun ControlBar(
    state: GameUiState,
    onTap: () -> Unit,
    onOverdrive: () -> Unit
) {
    val ready = state.overdrive >= GameConfig.OVERDRIVE_MAX && !state.overdriveActive
    val meter = (state.overdrive.toFloat() / GameConfig.OVERDRIVE_MAX).coerceIn(0.02f, 1f)
    val readyLabel = stringResource(R.string.state_ready)
    val chargingLabel = stringResource(R.string.state_charging)
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(NightSurfaceAlt, RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(meter)
                    .fillMaxHeight()
                    .background(SignalAmber, RoundedCornerShape(3.dp))
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryButton(
                text = stringResource(R.string.tap),
                onClick = onTap,
                height = 60.dp,
                modifier = Modifier.weight(1.4f)
            )
            PrimaryButton(
                text = stringResource(R.string.overdrive),
                onClick = onOverdrive,
                enabled = ready,
                height = 60.dp,
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        stateDescription = if (ready) readyLabel else chargingLabel
                    }
            )
        }
    }
}
