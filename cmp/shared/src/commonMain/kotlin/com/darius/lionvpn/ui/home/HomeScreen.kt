package com.darius.lionvpn.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.*

enum class HomeTab {
    Dashboard,
    Scripts,
    Certificates
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeTab by remember { mutableStateOf(HomeTab.Dashboard) }

    Surface(
        color = background,
        modifier = modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar Navigation (Fixed width 240dp)
            Sidebar(
                activeTab = activeTab,
                onTabSelect = { activeTab = it },
            )

            // Right Pane: Header + Content + Footer
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Main Content Area with elegant fade transitions
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(background)
                ) {
                    // Soft background glow gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        primary.copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Tab contents
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = {
                            fadeIn() with fadeOut()
                        }
                    ) { targetTab ->
                        when (targetTab) {
                            HomeTab.Dashboard -> DashboardTab(
                                state = state,
                                onClick = onClick
                            )
                            HomeTab.Scripts -> ScriptsTab(
                                state = state,
                                onClick = onClick
                            )
                            HomeTab.Certificates -> CertificatesTab(onClick = onClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Sidebar(
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = outlineVariant,
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(vertical = stackLg),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Logo / Branding Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = gutter)
                    .padding(bottom = stackLg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(primary.copy(alpha = 0.15f), roundedDefault)
                        .border(1.dp, primary.copy(alpha = 0.3f), roundedDefault),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🦁", style = displayLg)
                }
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
                        text = stringResource(Res.string.app_version),
                        style = monoCode.copy(
                            fontSize = 10.sp,
                            color = onSurfaceVariant.copy(alpha = 0.6f)
                        )
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
                    label = stringResource(Res.string.tab_certificates),
                    icon = Icons.Default.VerifiedUser,
                    isActive = activeTab == HomeTab.Certificates,
                    onClick = { onTabSelect(HomeTab.Certificates) }
                )
            }
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
