package com.darius.lionvpn.ui.home.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.bodyMd
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.surfaceContainerHighest
import com.darius.lionvpn.ui.theme.titleSm
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.settings_section_connection
import lion_vpn.shared.generated.resources.settings_item_config_title
import lion_vpn.shared.generated.resources.settings_item_config_desc
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConnectionSettingsSection(
    onOpenConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.settings_section_connection),
            style = titleSm.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primary)
        )
        ConfigSettingsCard(onOpenConfig = onOpenConfig)
    }
}

@Composable
private fun ConfigSettingsCard(
    onOpenConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = roundedLg,
        colors = CardDefaults.cardColors(containerColor = surfaceContainerHighest),
        border = borderStrokeGlass(),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenConfig() }
                .padding(gutter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(Res.string.settings_item_config_title),
                    style = bodyMd.copy(fontWeight = FontWeight.Bold, color = onSurface)
                )
                Text(
                    text = stringResource(Res.string.settings_item_config_desc),
                    style = bodySm.copy(fontSize = 12.sp, color = onSurfaceVariant)
                )
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = secondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
