package com.Cabmeri3.Nasxil.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Cabmeri3.Nasxil.R
import com.Cabmeri3.Nasxil.core.di.ServiceLocator
import com.Cabmeri3.Nasxil.core.theme.FeatherRed
import com.Cabmeri3.Nasxil.core.theme.NightBase
import com.Cabmeri3.Nasxil.core.theme.NightSurface
import com.Cabmeri3.Nasxil.core.theme.NightSurfaceAlt
import com.Cabmeri3.Nasxil.core.theme.RoadNavy
import com.Cabmeri3.Nasxil.core.theme.SignalAmber
import com.Cabmeri3.Nasxil.core.theme.TextMuted
import com.Cabmeri3.Nasxil.core.theme.TextPrimary
import com.Cabmeri3.Nasxil.core.theme.TextSecondary
import com.Cabmeri3.Nasxil.core.ui.HudFrame
import com.Cabmeri3.Nasxil.core.ui.ScreenBackground
import com.Cabmeri3.Nasxil.core.ui.SecondaryButton
import com.Cabmeri3.Nasxil.domain.model.NightMode
import com.Cabmeri3.Nasxil.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsRoute(onMenu: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        settingsViewModel.refresh()
    }
    SettingsScreen(
        state = state,
        onNightMode = settingsViewModel::setNightMode,
        onGhost = settingsViewModel::setGhost,
        onRookie = settingsViewModel::setRookieAssist,
        onAskReset = settingsViewModel::askReset,
        onDismissReset = settingsViewModel::dismissReset,
        onConfirmReset = settingsViewModel::confirmReset,
        onMenu = onMenu
    )
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onNightMode: (NightMode) -> Unit,
    onGhost: (Boolean) -> Unit,
    onRookie: (Boolean) -> Unit,
    onAskReset: () -> Unit,
    onDismissReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onMenu: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.88f),
            RoadNavy.copy(alpha = 0.80f),
            NightBase.copy(alpha = 0.94f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                OptionCard(
                    title = stringResource(R.string.night_shift),
                    explanation = stringResource(R.string.night_shift_explain)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NightModeChip(
                            label = stringResource(R.string.night_auto),
                            selected = state.settings.nightMode == NightMode.AUTO,
                            onClick = { onNightMode(NightMode.AUTO) },
                            modifier = Modifier.weight(1f)
                        )
                        NightModeChip(
                            label = stringResource(R.string.night_always),
                            selected = state.settings.nightMode == NightMode.ALWAYS_ON,
                            onClick = { onNightMode(NightMode.ALWAYS_ON) },
                            modifier = Modifier.weight(1.2f)
                        )
                        NightModeChip(
                            label = stringResource(R.string.night_off),
                            selected = state.settings.nightMode == NightMode.OFF,
                            onClick = { onNightMode(NightMode.OFF) },
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OptionCard(
                    title = stringResource(R.string.replay_ghost),
                    explanation = stringResource(R.string.replay_ghost_explain)
                ) {
                    SwitchRow(
                        checked = state.settings.ghostEnabled,
                        onCheckedChange = onGhost
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OptionCard(
                    title = stringResource(R.string.rookie_assist),
                    explanation = stringResource(R.string.rookie_assist_explain)
                ) {
                    SwitchRow(
                        checked = state.settings.rookieAssist,
                        onCheckedChange = onRookie
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                SecondaryButton(
                    text = stringResource(R.string.reset_progress),
                    onClick = onAskReset,
                    accent = FeatherRed,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.confirmVisible) {
            ConfirmResetDialog(
                onConfirm = onConfirmReset,
                onDismiss = onDismissReset
            )
        }
    }
}

@Composable
private fun OptionCard(
    title: String,
    explanation: String,
    control: @Composable () -> Unit
) {
    HudFrame(
        modifier = Modifier.fillMaxWidth(),
        fill = NightSurface.copy(alpha = 0.84f),
        corner = 14.dp,
        bracket = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            control()
        }
    }
}

@Composable
private fun NightModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLabel = stringResource(R.string.state_selected)
    val idleLabel = stringResource(R.string.state_idle)
    SecondaryButton(
        text = label,
        onClick = onClick,
        accent = if (selected) SignalAmber else TextMuted,
        modifier = modifier.semantics {
            stateDescription = if (selected) selectedLabel else idleLabel
        }
    )
}

@Composable
private fun SwitchRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (checked) onLabel else offLabel,
            style = MaterialTheme.typography.labelMedium,
            color = if (checked) SignalAmber else TextMuted
        )
        Spacer(modifier = Modifier.width(12.dp))
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NightBase,
                checkedTrackColor = SignalAmber,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = NightSurfaceAlt
            ),
            modifier = Modifier.semantics {
                stateDescription = if (checked) onLabel else offLabel
            }
        )
    }
}
