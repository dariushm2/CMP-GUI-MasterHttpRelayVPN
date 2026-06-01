package com.darius.lionvpn.ui.home.scripts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.onSecondary
import com.darius.lionvpn.ui.theme.outlineVariant
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.roundedSm
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.monoCode
import com.darius.lionvpn.ui.theme.labelCaps
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.surfaceContainerHighest
import com.darius.lionvpn.ui.theme.error
import com.darius.lionvpn.ui.theme.titleSm
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.selected_check_desc
import lion_vpn.shared.generated.resources.deployment_id
import lion_vpn.shared.generated.resources.active
import lion_vpn.shared.generated.resources.standby
import lion_vpn.shared.generated.resources.copy_deployment_id_desc
import lion_vpn.shared.generated.resources.delete_profile_icon_desc

@Composable
fun ScriptRow(
    name: String,
    id: String,
    isActive: Boolean,
    isMock: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    onCopied: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isActive) {
        Color(0x1F4EDEA3) // 10% secondary emerald green opacity
    } else {
        Color(0x1F1E293B) // Standard glass-card dark blue/slate
    }

    val cardBorder = if (isActive) {
        BorderStroke(1.dp, secondary.copy(alpha = 0.6f))
    } else {
        BorderStroke(1.dp, outlineVariant.copy(alpha = 0.4f))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = roundedDefault,
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = gutter, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RadioButtonIndicator(isActive = isActive)
                Spacer(modifier = Modifier.width(16.dp))
                ScriptDetails(name = name, id = id, isActive = isActive)
            }

            ScriptStatusAndActions(
                isActive = isActive,
                id = id,
                isMock = isMock,
                onDelete = onDelete,
                onCopied = onCopied
            )
        }
    }
}

@Composable
private fun RadioButtonIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .background(
                if (isActive) secondary else Color.Transparent,
                CircleShape
            )
            .border(
                2.dp,
                if (isActive) secondary else outlineVariant,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.selected_check_desc),
                tint = onSecondary,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun ScriptDetails(
    name: String,
    id: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = titleSm.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) secondary else onSurface
            )
        )
        val maskedId = if (id.length > 24) {
            "${id.take(8)}...${id.takeLast(8)}"
        } else {
            id
        }
        SelectionContainer {
            Text(
                text = stringResource(Res.string.deployment_id, maskedId),
                style = monoCode.copy(
                    fontSize = 11.sp,
                    color = onSurfaceVariant.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun ScriptStatusAndActions(
    isActive: Boolean,
    id: String,
    isMock: Boolean,
    onDelete: () -> Unit,
    onCopied: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        // Active/Standby status badge
        Box(
            modifier = Modifier
                .background(
                    if (isActive) secondary.copy(alpha = 0.15f) else surfaceContainerHighest.copy(alpha = 0.4f),
                    roundedSm
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isActive) stringResource(Res.string.active) else stringResource(Res.string.standby),
                style = labelCaps.copy(
                    fontSize = 10.sp,
                    color = if (isActive) secondary else onSurfaceVariant
                )
            )
        }

        // Copy Deployment ID button
        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(id))
                onCopied()
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = stringResource(Res.string.copy_deployment_id_desc),
                tint = onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
        }

        // Delete profile button (only shown for non-mocks)
        if (!isMock) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.delete_profile_icon_desc),
                    tint = error.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
