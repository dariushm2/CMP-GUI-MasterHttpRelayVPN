package com.darius.lionvpn.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darius.lionvpn.ProxyService
import com.darius.lionvpn.R
import com.darius.lionvpn.ui.theme.bodyMd
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.surfaceContainerLow
import com.darius.lionvpn.ui.theme.titleSm

@Composable
fun CertInstructionsDialog(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val errMessage = stringResource(R.string.cert_dialog_err_open_settings)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = roundedLg,
            color = surfaceContainerLow,
            border = borderStrokeGlass(),
            modifier = Modifier
                .fillMaxWidth(DIALOG_WIDTH_FRACTION)
                .padding(16.dp)
                .safeDrawingPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gutter),
                verticalArrangement = Arrangement.spacedBy(gutter)
            ) {
                CertInstructionsHeader(onDismiss = onDismiss)

                HorizontalDivider(color = Color(DIVIDER_COLOR_HEX))

                CertInstructionsContent()

                HorizontalDivider(color = Color(DIVIDER_COLOR_HEX))

                CertInstructionsActionButtons(
                    onDismiss = onDismiss,
                    context = context,
                    errMessage = errMessage
                )
            }
        }
    }
}

@Composable
private fun CertInstructionsHeader(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(R.string.cert_dialog_title),
                tint = primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(R.string.cert_dialog_title),
                style = titleSm.copy(
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
            )
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CertInstructionsContent(
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.cert_dialog_desc),
            style = bodyMd.copy(
                color = onSurfaceVariant,
                lineHeight = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.cert_dialog_steps_intro),
            style = bodySm.copy(
                color = onSurface,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
        )

        val steps = listOf(
            stringResource(R.string.cert_dialog_step1),
            stringResource(R.string.cert_dialog_step2),
            stringResource(R.string.cert_dialog_step3),
            stringResource(R.string.cert_dialog_step4),
            stringResource(R.string.cert_dialog_step5),
            stringResource(R.string.cert_dialog_step6)
        )

        steps.forEach { step ->
            Text(
                text = step,
                style = bodySm.copy(
                    color = onSurfaceVariant,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun CertInstructionsActionButtons(
    onDismiss: () -> Unit,
    context: Context,
    errMessage: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = {
                onDismiss()
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    try {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS))
                    } catch (ex: Exception) {
                        ProxyService.addLogLine("Error opening Settings: ${ex.message}")
                        Toast.makeText(context, errMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        ) {
            Text(
                text = stringResource(R.string.cert_dialog_btn_open_settings),
                color = primary,
                style = titleSm.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

private const val DIALOG_WIDTH_FRACTION = 0.9f
private const val DIVIDER_COLOR_HEX = 0x33DAE2FDL
