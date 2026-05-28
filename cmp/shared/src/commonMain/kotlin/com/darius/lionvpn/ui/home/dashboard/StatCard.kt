package com.darius.lionvpn.ui.home.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.model.SavedConfig
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.labelCaps
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedMd
import com.darius.lionvpn.ui.theme.tertiary
import com.darius.lionvpn.ui.theme.titleSm
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.no_config
import lion_vpn.shared.generated.resources.stat_active_script
import lion_vpn.shared.generated.resources.stat_executions
import lion_vpn.shared.generated.resources.stat_total_scripts
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = roundedMd,
        colors = CardDefaults.cardColors(containerColor = Color(0x1F1E293B)),
        border = BorderStroke(1.dp, Color(0x33DAE2FD))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    style = titleSm.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurface,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = title,
                style = labelCaps.copy(
                    color = onSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EmptyStatCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = roundedMd,
        colors = CardDefaults.cardColors(containerColor = Color(0x0A1E293B)),
        border = BorderStroke(1.dp, Color(0x1BFFFFFF))
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun StatsPanelGrid(
    activeConfig: SavedConfig?,
    executionCount: Int,
    totalScriptsCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(gutter)
    ) {
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gutter)
        ) {
            StatCard(
                title = stringResource(Res.string.stat_active_script),
                value = activeConfig?.name ?: stringResource(Res.string.no_config),
                icon = Icons.Default.Code,
                iconColor = primary,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            StatCard(
                title = stringResource(Res.string.stat_executions),
                value = executionCount.toString(),
                icon = Icons.Default.Bolt,
                iconColor = tertiary,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gutter)
        ) {
            StatCard(
                title = stringResource(Res.string.stat_total_scripts),
                value = totalScriptsCount.toString(),
                icon = Icons.Default.Dns,
                iconColor = primary,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            EmptyStatCard(
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}
