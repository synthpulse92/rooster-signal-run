package com.Cabmeri3.Nasxil.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Cabmeri3.Nasxil.core.theme.NightSurfaceAlt
import com.Cabmeri3.Nasxil.core.theme.SignalAmber
import com.Cabmeri3.Nasxil.core.theme.TextMuted
import com.Cabmeri3.Nasxil.core.theme.TextPrimary

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    enabled: Boolean = true,
    accent: Color = SignalAmber,
    height: Dp = 50.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "secondaryScale"
    )
    val contentColor = if (enabled) TextPrimary else TextMuted
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(height).scale(scale),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (enabled) accent.copy(alpha = 0.30f) else TextMuted.copy(alpha = 0.45f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = NightSurfaceAlt.copy(alpha = 0.85f),
            contentColor = contentColor,
            disabledContainerColor = NightSurfaceAlt.copy(alpha = 0.55f),
            disabledContentColor = TextMuted
        ),
        interactionSource = interactionSource
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = if (enabled) accent else TextMuted,
                    modifier = Modifier.height(20.dp).width(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}
