package com.Cabmeri3.Nasxil.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.Cabmeri3.Nasxil.core.theme.FeatherRed
import com.Cabmeri3.Nasxil.core.theme.GoGreen
import com.Cabmeri3.Nasxil.core.theme.NightSurfaceAlt
import com.Cabmeri3.Nasxil.core.theme.SignalAmber
import com.Cabmeri3.Nasxil.core.theme.TextMuted
import com.Cabmeri3.Nasxil.domain.model.LampPhase

fun lampColor(phase: LampPhase): Color = when (phase) {
    LampPhase.RED -> FeatherRed
    LampPhase.AMBER -> SignalAmber
    LampPhase.GREEN -> GoGreen
    LampPhase.DARK -> TextMuted
}

@Composable
fun SignalLamp(
    phase: LampPhase,
    diameter: Dp,
    modifier: Modifier = Modifier,
    glow: Boolean = true,
    intensity: Float = 1f
) {
    val color = lampColor(phase)
    val dark = phase == LampPhase.DARK
    Canvas(modifier = modifier.size(diameter)) {
        val radius = size.minDimension / 2f
        if (glow && !dark) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.40f * intensity), Color.Transparent),
                    radius = radius * 2.4f
                ),
                radius = radius * 2.4f
            )
        }
        drawCircle(color = NightSurfaceAlt, radius = radius)
        drawCircle(color = color.copy(alpha = if (dark) 0.35f else 1f), radius = radius * 0.72f)
    }
}
