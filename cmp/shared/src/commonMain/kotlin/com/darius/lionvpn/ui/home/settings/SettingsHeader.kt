package com.darius.lionvpn.ui.home.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.headlineMd
import com.darius.lionvpn.ui.theme.onSurface
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.tab_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(Res.string.tab_settings),
            style = headlineMd.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
        )
    }
}
