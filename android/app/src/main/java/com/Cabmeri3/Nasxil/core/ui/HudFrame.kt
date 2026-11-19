package com.Cabmeri3.Nasxil.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Cabmeri3.Nasxil.core.theme.NightSurface
import com.Cabmeri3.Nasxil.core.theme.SignalAmber

@Composable
fun HudFrame(
    modifier: Modifier = Modifier,
    accent: Color = SignalAmber,
    fill: Color = NightSurface.copy(alpha = 0.90f),
    corner: Dp = 16.dp,
    bracket: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier = modifier
            .background(fill, shape)
            .border(1.dp, accent.copy(alpha = 0.22f), shape)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val arm = bracket.toPx()
            val inset = 6f
            val stroke = 2f
            val w = size.width
            val h = size.height
            drawLine(accent.copy(alpha = 0.45f), Offset(inset, inset + arm), Offset(inset, inset), stroke)
            drawLine(accent.copy(alpha = 0.45f), Offset(inset, inset), Offset(inset + arm, inset), stroke)
            drawLine(accent.copy(alpha = 0.45f), Offset(w - inset - arm, inset), Offset(w - inset, inset), stroke)
            drawLine(accent.copy(alpha = 0.45f), Offset(w - inset, inset), Offset(w - inset, inset + arm), stroke)
            drawLine(accent.copy(alpha = 0.45f), Offset(inset, h - inset - arm), Offset(inset, h - inset), stroke)
            drawLine(accent.copy(alpha = 0.45f), Offset(inset, h - inset), Offset(inset + arm, h - inset), stroke)
            drawLine(accent.copy(alpha = 0.45f), Offset(w - inset - arm, h - inset), Offset(w - inset, h - inset), stroke)
            drawLine(accent.copy(alpha = 0.45f), Offset(w - inset, h - inset), Offset(w - inset, h - inset - arm), stroke)
        }
        content()
    }
}
