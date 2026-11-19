package com.Cabmeri3.Nasxil.presentation.menu

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.Cabmeri3.Nasxil.core.theme.RoadNavy
import com.Cabmeri3.Nasxil.core.theme.SignalAmber
import com.Cabmeri3.Nasxil.core.theme.TextPrimary
import com.Cabmeri3.Nasxil.core.theme.TextSecondary
import com.Cabmeri3.Nasxil.core.ui.PrimaryButton
import com.Cabmeri3.Nasxil.core.ui.ScreenBackground
import com.Cabmeri3.Nasxil.core.ui.SecondaryButton
import com.Cabmeri3.Nasxil.core.ui.StatItem
import com.Cabmeri3.Nasxil.core.ui.StatRow
import com.Cabmeri3.Nasxil.core.ui.StampBadge

@Composable
fun MenuRoute(
    onPlay: () -> Unit,
    onDepot: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }
    MenuScreen(
        state = state,
        onPlay = onPlay,
        onDepot = onDepot,
        onSettings = onSettings
    )
}

@Composable
fun MenuScreen(
    state: MenuUiState,
    onPlay: () -> Unit,
    onDepot: () -> Unit,
    onSettings: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menu")
    val bob by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "bob"
    )

    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.35f),
            RoadNavy.copy(alpha = 0.55f),
            NightBase.copy(alpha = 0.92f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.brand),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                StampBadge(count = state.stamps)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_rooster),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(132.dp)
                        .offset(y = bob.dp)
                        .padding(bottom = 6.dp)
                )
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(132.dp)
                        .offset(y = 58.dp)
                        .graphicsLayer(scaleY = -0.45f, alpha = 0.18f)
                )
            }

            MenuSheet(
                state = state,
                onPlay = onPlay,
                onDepot = onDepot,
                onSettings = onSettings
            )
        }
    }
}

@Composable
private fun MenuSheet(
    state: MenuUiState,
    onPlay: () -> Unit,
    onDepot: () -> Unit,
    onSettings: () -> Unit
) {
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NightSurface.copy(alpha = 0.92f), shape)
            .border(1.dp, SignalAmber.copy(alpha = 0.22f), shape)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.splash_title),
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.tagline),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        StatRow(
            items = listOf(
                StatItem(
                    label = stringResource(R.string.stat_best),
                    value = state.bestDistance.toString(),
                    accent = SignalAmber,
                    visible = state.bestDistance > 0
                ),
                StatItem(
                    label = stringResource(R.string.stat_chain),
                    value = state.bestChain.toString(),
                    accent = FeatherRed,
                    visible = state.bestChain > 0
                ),
                StatItem(
                    label = stringResource(R.string.stat_mastery),
                    value = state.masteryTiers.toString(),
                    accent = NightWash,
                    visible = state.masteryTiers > 0
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = stringResource(R.string.play),
            onClick = onPlay,
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
                text = stringResource(R.string.settings),
                onClick = onSettings,
                leadingIcon = R.drawable.ic_gear,
                modifier = Modifier.weight(1f)
            )
        }
        if (state.nightAlways) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.night_badge),
                style = MaterialTheme.typography.labelSmall,
                color = NightWash,
                modifier = Modifier
                    .background(NightWash.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}
