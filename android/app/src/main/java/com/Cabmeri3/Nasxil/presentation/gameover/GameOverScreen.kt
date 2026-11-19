package com.Cabmeri3.Nasxil.presentation.gameover

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Cabmeri3.Nasxil.R
import com.Cabmeri3.Nasxil.core.di.ServiceLocator
import com.Cabmeri3.Nasxil.core.theme.FeatherRed
import com.Cabmeri3.Nasxil.core.theme.NightBase
import com.Cabmeri3.Nasxil.core.theme.NightSurface
import com.Cabmeri3.Nasxil.core.theme.NightWash
import com.Cabmeri3.Nasxil.core.theme.SignalAmber
import com.Cabmeri3.Nasxil.core.theme.TextSecondary
import com.Cabmeri3.Nasxil.core.ui.HudFrame
import com.Cabmeri3.Nasxil.core.ui.PrimaryButton
import com.Cabmeri3.Nasxil.core.ui.ScreenBackground
import com.Cabmeri3.Nasxil.core.ui.SecondaryButton
import com.Cabmeri3.Nasxil.core.ui.StatItem
import com.Cabmeri3.Nasxil.core.ui.StatRow
import com.Cabmeri3.Nasxil.domain.model.FailCause
import com.Cabmeri3.Nasxil.domain.model.RunSummary
import kotlin.math.abs

@Composable
fun GameOverRoute(
    summary: RunSummary,
    onPlayAgain: () -> Unit,
    onDepot: () -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = ServiceLocator.factory)
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(summary) {
        gameOverViewModel.bind(summary)
    }
    GameOverScreen(
        state = state,
        onPlayAgain = onPlayAgain,
        onDepot = onDepot,
        onMenu = onMenu
    )
}

@Composable
fun GameOverScreen(
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onDepot: () -> Unit,
    onMenu: () -> Unit
) {
    val summary = state.summary
    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.90f),
            FeatherRed.copy(alpha = 0.12f),
            NightBase.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HudFrame(
                modifier = Modifier.fillMaxWidth(),
                accent = FeatherRed,
                fill = NightSurface.copy(alpha = 0.88f),
                corner = 18.dp
            ) {
                Text(
                    text = stringResource(verdictOf(summary.failCause)),
                    style = MaterialTheme.typography.displaySmall,
                    color = FeatherRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 22.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
            }

            if (summary.newRecord) {
                Spacer(modifier = Modifier.height(14.dp))
                RecordRibbon()
            }

            Spacer(modifier = Modifier.height(18.dp))

            HudFrame(
                modifier = Modifier.fillMaxWidth(),
                fill = NightSurface.copy(alpha = 0.86f),
                corner = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.stat_score),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = summary.score.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        color = SignalAmber
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StatRow(
                        items = listOf(
                            StatItem(
                                label = stringResource(R.string.stat_distance),
                                value = summary.distance.toString(),
                                accent = SignalAmber,
                                visible = summary.distance > 0
                            ),
                            StatItem(
                                label = stringResource(R.string.stat_stamps),
                                value = summary.stamps.toString(),
                                accent = NightWash,
                                visible = summary.stamps > 0
                            ),
                            StatItem(
                                label = stringResource(R.string.stat_chain),
                                value = summary.bestChain.toString(),
                                accent = FeatherRed,
                                visible = summary.bestChain > 0
                            )
                        )
                    )
                    if (summary.masteryGain > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.mastery_gain, summary.masteryGain),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NightWash
                        )
                    }
                    val ghostDelta = summary.ghostDelta
                    if (ghostDelta != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (ghostDelta >= 0) {
                                stringResource(R.string.ghost_ahead, ghostDelta)
                            } else {
                                stringResource(R.string.ghost_behind, abs(ghostDelta))
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                leadingIcon = R.drawable.ic_play,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.depot),
                    onClick = onDepot,
                    leadingIcon = R.drawable.ic_stamp,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu,
                    leadingIcon = R.drawable.icon_home,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RecordRibbon() {
    val transition = rememberInfiniteTransition(label = "ribbon")
    val shimmer by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "shimmer"
    )
    Text(
        text = stringResource(R.string.new_record),
        style = MaterialTheme.typography.titleMedium,
        color = NightBase,
        modifier = Modifier
            .background(SignalAmber, RoundedCornerShape(12.dp))
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .alpha(shimmer)
    )
}

private fun verdictOf(cause: FailCause): Int = when (cause) {
    FailCause.RED_LIGHT -> R.string.verdict_red_light
    FailCause.GATE_DOWN -> R.string.verdict_gate_down
    FailCause.FERRY_MISS -> R.string.verdict_ferry_miss
    FailCause.LAMP_OUT -> R.string.verdict_lamp_out
}
