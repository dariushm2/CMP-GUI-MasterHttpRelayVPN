package com.darius.lionvpn.ui.home.scripts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.surfaceContainerHigh
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.outlineVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.titleSm
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.setup_instructions_title
import lion_vpn.shared.generated.resources.setup_instructions_intro
import lion_vpn.shared.generated.resources.setup_instructions_step1
import lion_vpn.shared.generated.resources.setup_instructions_step2
import lion_vpn.shared.generated.resources.setup_instructions_step3
import lion_vpn.shared.generated.resources.setup_instructions_step4
import lion_vpn.shared.generated.resources.setup_instructions_step5
import lion_vpn.shared.generated.resources.setup_instructions_step6
import lion_vpn.shared.generated.resources.setup_instructions_step7

@Composable
fun SetupInstructionsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = modifier
                .width(520.dp)
                .padding(16.dp),
            shape = roundedLg,
            colors = CardDefaults.cardColors(containerColor = surfaceContainerHigh),
            border = borderStrokeGlass()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InstructionsHeader(onDismiss = onDismiss)
                Divider(color = outlineVariant)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                ) {
                    SelectionContainer {
                        Steps()
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionsHeader(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
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
                imageVector = Icons.Default.Info,
                contentDescription = "Instructions Icon",
                tint = secondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(Res.string.setup_instructions_title),
                style = titleSm.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = secondary
                )
            )
        }
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Dialog",
                tint = onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Steps(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(Res.string.setup_instructions_intro),
            style = bodySm.copy(color = onSurface, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
        )

        StepRowWithLink(
            instructionText = stringResource(Res.string.setup_instructions_step1),
            linkText = "https://github.com/masterking32/MasterHttpRelayVPN/blob/python_testing/apps_script/Code.gs"
        )

        StepRowWithLink(
            instructionText = stringResource(Res.string.setup_instructions_step2),
            linkText = "https://script.google.com"
        )

        Text(
            text = stringResource(Res.string.setup_instructions_step3),
            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
        )

        Text(
            text = stringResource(Res.string.setup_instructions_step4),
            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
        )

        Text(
            text = stringResource(Res.string.setup_instructions_step5),
            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
        )

        Text(
            text = stringResource(Res.string.setup_instructions_step6),
            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
        )

        Text(
            text = stringResource(Res.string.setup_instructions_step7),
            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
        )
    }
}

@Composable
private fun StepRowWithLink(
    instructionText: String,
    linkText: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = instructionText,
            style = bodySm.copy(color = onSurface, lineHeight = 20.sp)
        )
        Text(
            text = linkText,
            color = primary,
            style = bodySm.copy(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Medium),
            modifier = Modifier
                .clickable { uriHandler.openUri(linkText) }
                .padding(vertical = 2.dp)
        )
    }
}
