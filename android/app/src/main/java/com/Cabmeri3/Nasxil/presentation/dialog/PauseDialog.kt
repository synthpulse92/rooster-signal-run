package com.Cabmeri3.Nasxil.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.Cabmeri3.Nasxil.R
import com.Cabmeri3.Nasxil.core.theme.NightSurface
import com.Cabmeri3.Nasxil.core.theme.TextPrimary
import com.Cabmeri3.Nasxil.core.ui.HudFrame
import com.Cabmeri3.Nasxil.core.ui.PrimaryButton
import com.Cabmeri3.Nasxil.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        HudFrame(
            modifier = Modifier.fillMaxWidth(),
            fill = NightSurface.copy(alpha = 0.97f),
            corner = 18.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.paused),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(18.dp))
                PrimaryButton(
                    text = stringResource(R.string.resume),
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = stringResource(R.string.restart),
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu,
                    leadingIcon = R.drawable.icon_home,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
