package com.darius.lionvpn.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.graphics.vector.ImageVector
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.tab_about
import lion_vpn.shared.generated.resources.tab_dashboard
import lion_vpn.shared.generated.resources.tab_scripts
import lion_vpn.shared.generated.resources.tab_settings
import org.jetbrains.compose.resources.StringResource

enum class HomeTab(val title: StringResource, val icon: ImageVector) {
    Dashboard(Res.string.tab_dashboard, Icons.Default.Dashboard),
    Scripts(Res.string.tab_scripts, Icons.Default.Terminal),
    Settings(Res.string.tab_settings, Icons.Default.Settings),
    About(Res.string.tab_about, Icons.Default.Info);
}
