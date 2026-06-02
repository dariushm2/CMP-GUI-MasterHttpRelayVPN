package com.darius.lionvpn.ui.home.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.getPlatform
import com.darius.lionvpn.ui.home.settings.PlatformDialog
import com.darius.lionvpn.ui.theme.bodyMd
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.labelCaps
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.roundedMd
import com.darius.lionvpn.ui.theme.surfaceContainerLow
import com.darius.lionvpn.ui.theme.tertiary
import com.darius.lionvpn.ui.theme.titleSm
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.stat_limitations_title
import lion_vpn.shared.generated.resources.stat_limitations_value
import lion_vpn.shared.generated.resources.limitations_dialog_title
import lion_vpn.shared.generated.resources.limitations_dialog_intro
import lion_vpn.shared.generated.resources.limitations_bullet1_title
import lion_vpn.shared.generated.resources.limitations_bullet1_desc
import lion_vpn.shared.generated.resources.limitations_bullet2_title
import lion_vpn.shared.generated.resources.limitations_bullet2_desc
import lion_vpn.shared.generated.resources.limitations_bullet3_title
import lion_vpn.shared.generated.resources.limitations_bullet3_desc
import lion_vpn.shared.generated.resources.limitations_bullet4_title
import lion_vpn.shared.generated.resources.limitations_bullet4_desc
import lion_vpn.shared.generated.resources.desktop_limitations_dialog_intro
import lion_vpn.shared.generated.resources.desktop_limitations_bullet1_title
import lion_vpn.shared.generated.resources.desktop_limitations_bullet1_desc
import lion_vpn.shared.generated.resources.desktop_limitations_bullet2_title
import lion_vpn.shared.generated.resources.desktop_limitations_bullet2_desc
import lion_vpn.shared.generated.resources.desktop_limitations_bullet3_title
import lion_vpn.shared.generated.resources.desktop_limitations_bullet3_desc
import lion_vpn.shared.generated.resources.limitations_dialog_got_it
import org.jetbrains.compose.resources.stringResource

@Composable
fun LimitationsStatCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
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
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(Res.string.stat_limitations_title),
                tint = tertiary,
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.stat_limitations_value),
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
                text = stringResource(Res.string.stat_limitations_title),
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
private fun BulletPoint(
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = bodyMd.copy(
                fontWeight = FontWeight.Bold,
                color = tertiary,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(top = 1.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = titleSm.copy(
                    fontWeight = FontWeight.Bold,
                    color = onSurface,
                    fontSize = 14.sp
                )
            )
            Text(
                text = desc,
                style = bodySm.copy(
                    color = onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
private fun LimitationsDialogHeader(
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
                contentDescription = stringResource(Res.string.limitations_dialog_title),
                tint = tertiary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(Res.string.limitations_dialog_title),
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
private fun AndroidLimitationsContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.limitations_dialog_intro),
            style = bodyMd.copy(
                color = onSurfaceVariant,
                lineHeight = 20.sp
            )
        )

        BulletPoint(
            title = stringResource(Res.string.limitations_bullet1_title),
            desc = stringResource(Res.string.limitations_bullet1_desc)
        )
        
        BulletPoint(
            title = stringResource(Res.string.limitations_bullet2_title),
            desc = stringResource(Res.string.limitations_bullet2_desc)
        )
        
        BulletPoint(
            title = stringResource(Res.string.limitations_bullet3_title),
            desc = stringResource(Res.string.limitations_bullet3_desc)
        )

        BulletPoint(
            title = stringResource(Res.string.limitations_bullet4_title),
            desc = stringResource(Res.string.limitations_bullet4_desc)
        )
    }
}

@Composable
private fun DesktopLimitationsContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.desktop_limitations_dialog_intro),
            style = bodyMd.copy(
                color = onSurfaceVariant,
                lineHeight = 20.sp
            )
        )

        BulletPoint(
            title = stringResource(Res.string.desktop_limitations_bullet1_title),
            desc = stringResource(Res.string.desktop_limitations_bullet1_desc)
        )
        
        BulletPoint(
            title = stringResource(Res.string.desktop_limitations_bullet2_title),
            desc = stringResource(Res.string.desktop_limitations_bullet2_desc)
        )
        
        BulletPoint(
            title = stringResource(Res.string.desktop_limitations_bullet3_title),
            desc = stringResource(Res.string.desktop_limitations_bullet3_desc)
        )
    }
}

@Composable
private fun LimitationsDialogGotItButton(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onDismiss) {
            Text(
                text = stringResource(Res.string.limitations_dialog_got_it),
                color = primary,
                fontWeight = FontWeight.Bold,
                style = titleSm
            )
        }
    }
}

@Composable
fun LimitationsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAndroid = getPlatform().isAndroid()
    PlatformDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .safeDrawingPadding(),
            shape = roundedLg,
            color = surfaceContainerLow,
            border = borderStrokeGlass()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gutter),
                verticalArrangement = Arrangement.spacedBy(gutter)
            ) {
                LimitationsDialogHeader(onDismiss = onDismiss)

                HorizontalDivider(color = Color(0x33DAE2FD))

                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (isAndroid) {
                        AndroidLimitationsContent()
                    } else {
                        DesktopLimitationsContent()
                    }
                }

                LimitationsDialogGotItButton(onDismiss = onDismiss)
            }
        }
    }
}
