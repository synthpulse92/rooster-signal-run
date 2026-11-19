package com.Cabmeri3.Nasxil.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = SignalAmber,
    onPrimary = NightBase,
    primaryContainer = AmberDeep,
    onPrimaryContainer = NightBase,
    secondary = FeatherRed,
    onSecondary = TextPrimary,
    tertiary = WaterCyan,
    onTertiary = NightBase,
    background = NightBase,
    onBackground = TextPrimary,
    surface = NightSurface,
    onSurface = TextPrimary,
    surfaceVariant = NightSurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = NightSurfaceAlt,
    error = FeatherRed,
    onError = TextPrimary,
    scrim = NightBase
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
