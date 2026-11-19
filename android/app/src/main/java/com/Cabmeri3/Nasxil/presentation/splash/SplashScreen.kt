package com.Cabmeri3.Nasxil.presentation.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
import com.Cabmeri3.Nasxil.core.theme.GoGreen
import com.Cabmeri3.Nasxil.core.theme.NightBase
import com.Cabmeri3.Nasxil.core.theme.NightSurfaceAlt
import com.Cabmeri3.Nasxil.core.theme.RoadNavy
import com.Cabmeri3.Nasxil.core.theme.SignalAmber
import com.Cabmeri3.Nasxil.core.theme.TextMuted
import com.Cabmeri3.Nasxil.core.theme.TextPrimary
import com.Cabmeri3.Nasxil.core.theme.TextSecondary
import com.Cabmeri3.Nasxil.core.ui.ScreenBackground

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    LaunchedEffect(finished) {
        if (finished) onFinished()
    }
    SplashScreen()
}

@Composable
fun SplashScreen() {
    val transition = rememberInfiniteTransition(label = "splash")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing)),
        label = "sweep"
    )
    val lampStep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "lampStep"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.96f),
            RoadNavy.copy(alpha = 0.88f),
            NightBase.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LampDot(FeatherRed, lampStep.toInt() == 0)
                    LampDot(SignalAmber, lampStep.toInt() == 1)
                    LampDot(GoGreen, lampStep.toInt() >= 2)
                }
                Spacer(modifier = Modifier.width(22.dp))
                Box(
                    modifier = Modifier.size(196.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(196.dp).rotate(sweep)) {
                        val stroke = 3f
                        drawArc(
                            color = SignalAmber.copy(alpha = 0.85f),
                            startAngle = 0f,
                            sweepAngle = 72f,
                            useCenter = false,
                            topLeft = Offset(stroke, stroke),
                            size = Size(size.width - stroke * 2, size.height - stroke * 2),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke * 1.6f)
                        )
                    }
                    Canvas(modifier = Modifier.size(176.dp)) {
                        drawCircle(
                            color = SignalAmber.copy(alpha = 0.45f),
                            radius = size.minDimension / 2f,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.cd_logo),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(132.dp).alpha(pulse)
                    )
                }
            }

            Spacer(modifier = Modifier.height(38.dp))

            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.splash_title),
                    style = MaterialTheme.typography.displaySmall,
                    color = SignalAmber.copy(alpha = 0.35f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Text(
                    text = stringResource(R.string.splash_title),
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(44.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(188.dp)
                    .height(4.dp),
                color = SignalAmber,
                trackColor = NightSurfaceAlt
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.loading_route),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }

        Text(
            text = stringResource(R.string.legal_notice),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
                .fillMaxWidth()
                .padding(bottom = 18.dp, start = 20.dp, end = 20.dp)
        )
    }
}

@Composable
private fun LampDot(color: Color, lit: Boolean) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .background(
                if (lit) color else color.copy(alpha = 0.18f),
                RoundedCornerShape(9.dp)
            )
    )
}
