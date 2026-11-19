package com.Cabmeri3.Nasxil.presentation.depot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Cabmeri3.Nasxil.R
import com.Cabmeri3.Nasxil.core.di.ServiceLocator
import com.Cabmeri3.Nasxil.core.theme.NightBase
import com.Cabmeri3.Nasxil.core.theme.NightSurface
import com.Cabmeri3.Nasxil.core.theme.NightSurfaceAlt
import com.Cabmeri3.Nasxil.core.theme.NightWash
import com.Cabmeri3.Nasxil.core.theme.RoadNavy
import com.Cabmeri3.Nasxil.core.theme.SignalAmber
import com.Cabmeri3.Nasxil.core.theme.TextMuted
import com.Cabmeri3.Nasxil.core.theme.TextPrimary
import com.Cabmeri3.Nasxil.core.theme.TextSecondary
import com.Cabmeri3.Nasxil.core.ui.HudFrame
import com.Cabmeri3.Nasxil.core.ui.ScreenBackground
import com.Cabmeri3.Nasxil.core.ui.SecondaryButton
import com.Cabmeri3.Nasxil.core.ui.StampBadge
import com.Cabmeri3.Nasxil.domain.model.LayerType
import com.Cabmeri3.Nasxil.domain.model.UpgradeId

@Composable
fun DepotRoute(onMenu: () -> Unit) {
    val depotViewModel: DepotViewModel = viewModel(factory = ServiceLocator.factory)
    val state by depotViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        depotViewModel.refresh()
    }
    DepotScreen(
        state = state,
        onBuy = depotViewModel::buy,
        onMenu = onMenu
    )
}

@Composable
fun DepotScreen(
    state: DepotUiState,
    onBuy: (UpgradeId) -> Unit,
    onMenu: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            RoadNavy.copy(alpha = 0.92f),
            NightBase.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.depot),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                StampBadge(count = state.stamps)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.mastery_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                state.mastery.forEach { row ->
                    MasteryCard(row)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.upgrades_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (state.empty) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.depot_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    state.upgrades.forEach { upgrade ->
                        UpgradeCard(upgrade = upgrade, onBuy = onBuy)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MasteryCard(row: MasteryUi) {
    HudFrame(
        modifier = Modifier.fillMaxWidth(),
        fill = NightSurface.copy(alpha = 0.84f),
        corner = 14.dp,
        bracket = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(masteryLabel(row.type)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.tier_value, row.tier),
                    style = MaterialTheme.typography.labelMedium,
                    color = SignalAmber
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(NightSurfaceAlt, RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(row.progress.coerceIn(0.02f, 1f))
                        .fillMaxHeight()
                        .background(SignalAmber, RoundedCornerShape(3.dp))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tier_effect, row.scorePercent, row.windowMs),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun UpgradeCard(upgrade: UpgradeUi, onBuy: (UpgradeId) -> Unit) {
    val accent = if (upgrade.owned) NightWash else if (upgrade.affordable) SignalAmber else TextMuted
    val lockedLabel = stringResource(R.string.state_locked)
    val ownedLabel = stringResource(R.string.owned)
    HudFrame(
        modifier = Modifier.fillMaxWidth(),
        accent = accent,
        fill = NightSurface.copy(alpha = 0.84f),
        corner = 14.dp,
        bracket = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(upgradeName(upgrade.id)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(upgradeEffect(upgrade.id)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.cost_value, upgrade.cost),
                    style = MaterialTheme.typography.labelMedium,
                    color = accent
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (upgrade.owned) {
                Text(
                    text = ownedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = NightWash,
                    modifier = Modifier
                        .background(NightWash.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            } else {
                SecondaryButton(
                    text = stringResource(R.string.buy),
                    onClick = { onBuy(upgrade.id) },
                    enabled = upgrade.affordable,
                    accent = accent,
                    modifier = Modifier
                        .width(104.dp)
                        .semantics {
                            if (!upgrade.affordable) stateDescription = lockedLabel
                        }
                )
            }
        }
    }
}

private fun masteryLabel(type: LayerType): Int = when (type) {
    LayerType.ROAD -> R.string.type_road
    LayerType.RAIL -> R.string.type_rail
    LayerType.FERRY -> R.string.type_ferry
}

private fun upgradeName(id: UpgradeId): Int = when (id) {
    UpgradeId.REFLECTIVE_VEST -> R.string.upgrade_vest
    UpgradeId.LAMP_TIMER -> R.string.upgrade_lamp_timer
    UpgradeId.FERRY_CLEATS -> R.string.upgrade_cleats
}

private fun upgradeEffect(id: UpgradeId): Int = when (id) {
    UpgradeId.REFLECTIVE_VEST -> R.string.upgrade_vest_effect
    UpgradeId.LAMP_TIMER -> R.string.upgrade_lamp_timer_effect
    UpgradeId.FERRY_CLEATS -> R.string.upgrade_cleats_effect
}
