package com.darius.lionvpn.ui.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.theme.bodyMd
import com.darius.lionvpn.ui.theme.bodySm
import com.darius.lionvpn.ui.theme.borderStrokeGlass
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.onSurface
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.outlineVariant
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.roundedLg
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.surfaceContainerLow
import com.darius.lionvpn.ui.theme.surfaceContainerHighest
import com.darius.lionvpn.ui.theme.titleSm
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.settings_section_general
import lion_vpn.shared.generated.resources.settings_item_language_title
import lion_vpn.shared.generated.resources.settings_item_language_desc
import org.jetbrains.compose.resources.stringResource

@Composable
fun GeneralSettingsSection(
    language: Lang,
    onLanguageChange: (Lang) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.settings_section_general),
            style = titleSm.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primary)
        )
        LanguageSettingsCard(
            language = language,
            onLanguageChange = onLanguageChange
        )
    }
}

@Composable
private fun LanguageSettingsCard(
    language: Lang,
    onLanguageChange: (Lang) -> Unit,
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
                .padding(gutter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(Res.string.settings_item_language_title),
                    style = bodyMd.copy(fontWeight = FontWeight.Bold, color = onSurface)
                )
                Text(
                    text = stringResource(Res.string.settings_item_language_desc),
                    style = bodySm.copy(fontSize = 12.sp, color = onSurfaceVariant)
                )
            }
            
            LanguageToggleRow(
                language = language,
                onLanguageChange = onLanguageChange
            )
        }
    }
}

@Composable
private fun LanguageToggleRow(
    language: Lang,
    onLanguageChange: (Lang) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(160.dp)
            .height(36.dp)
            .background(surfaceContainerLow, roundedDefault)
            .border(1.dp, outlineVariant.copy(alpha = 0.5f), roundedDefault)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        LanguageToggleButton(
            label = "فارسی",
            isSelected = language == Lang.FA,
            onClick = { onLanguageChange(Lang.FA) },
            modifier = Modifier.weight(1f)
        )
        LanguageToggleButton(
            label = "English",
            isSelected = language == Lang.EN,
            onClick = { onLanguageChange(Lang.EN) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LanguageToggleButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) secondary.copy(alpha = 0.15f) else Color.Transparent
    val borderCol = if (isSelected) secondary.copy(alpha = 0.4f) else Color.Transparent
    val textCol = if (isSelected) secondary else onSurfaceVariant
    val textWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(bg, shape = roundedDefault)
            .border(if (isSelected) 1.dp else 0.dp, borderCol, shape = roundedDefault)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = bodySm.copy(fontWeight = textWeight, color = textCol)
        )
    }
}
