package com.darius.lionvpn.ui.home.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.SharedBuildConfig
import com.darius.lionvpn.ui.home.HomeTab
import com.darius.lionvpn.ui.theme.surfaceContainerLowest
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.gutter
import com.darius.lionvpn.ui.theme.stackLg
import com.darius.lionvpn.ui.theme.headlineMd
import com.darius.lionvpn.ui.theme.monoCode
import com.darius.lionvpn.ui.theme.onSurfaceVariant
import com.darius.lionvpn.ui.theme.bodyMd
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.app_name
import lion_vpn.shared.generated.resources.tab_dashboard
import lion_vpn.shared.generated.resources.tab_scripts
import lion_vpn.shared.generated.resources.tab_settings
import lion_vpn.shared.generated.resources.tab_about

@Composable
fun Sidebar(
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
            .width(240.dp)
            .fillMaxHeight()
            .background(surfaceContainerLowest)
            .padding(vertical = stackLg),
        verticalArrangement = Arrangement.Top
    ) {
        // Logo / Branding Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = gutter)
                .padding(bottom = stackLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🦁",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(primary.copy(alpha = 0.15f), roundedDefault)
                    .border(1.dp, primary.copy(alpha = 0.3f), roundedDefault)
                    .offset(y = 1.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(Res.string.app_name),
                    style = headlineMd.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                )
                Text(
                    text = SharedBuildConfig.APP_VERSION,
                    style = monoCode.copy(
                        fontSize = 10.sp,
                        color = onSurfaceVariant.copy(alpha = 0.6f),
                        textDirection = TextDirection.Ltr,
                    ),
                )
            }
        }

        // Navigation Links
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SidebarNavItem(
                label = stringResource(Res.string.tab_dashboard),
                icon = Icons.Default.Dashboard,
                isActive = activeTab == HomeTab.Dashboard,
                onClick = { onTabSelect(HomeTab.Dashboard) }
            )
            SidebarNavItem(
                label = stringResource(Res.string.tab_scripts),
                icon = Icons.Default.Terminal,
                isActive = activeTab == HomeTab.Scripts,
                onClick = { onTabSelect(HomeTab.Scripts) }
            )
            SidebarNavItem(
                label = stringResource(Res.string.tab_settings),
                icon = Icons.Default.Settings,
                isActive = activeTab == HomeTab.Settings,
                onClick = { onTabSelect(HomeTab.Settings) }
            )
            SidebarNavItem(
                label = stringResource(Res.string.tab_about),
                icon = Icons.Default.Info,
                isActive = activeTab == HomeTab.About,
                onClick = { onTabSelect(HomeTab.About) }
            )
        }
    }
}

@Composable
private fun SidebarNavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) secondary.copy(alpha = 0.08f) else Color.Transparent
    val contentColor = if (isActive) secondary else onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = gutter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$label Icon",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = bodyMd.copy(
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor
                )
            )
        }

        // Right indicator line if active
        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(secondary)
            )
        }
    }
}
