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
import androidx.navigation.NavHostController

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
        modifier = modifier.fillMaxSize(),
        color = Theme2.background
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
                        .background(Theme2.background)
                ) {
                    // Soft background glow gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Theme2.primary.copy(alpha = 0.05f),
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
                            HomeTab.Certificates -> CertificatesTab(
                                state = state,
                                onClick = onClick
                            )
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
            .background(Theme2.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = Theme2.outlineVariant,
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(vertical = Theme2.stackLg),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Logo / Branding Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme2.gutter)
                    .padding(bottom = Theme2.stackLg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Theme2.primary.copy(alpha = 0.15f), Theme2.roundedDefault)
                        .border(1.dp, Theme2.primary.copy(alpha = 0.3f), Theme2.roundedDefault),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield Logo",
                        tint = Theme2.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Lion VPN",
                        style = Theme2.headlineMd.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Theme2.primary
                        )
                    )
                    Text(
                        text = "1.0.0",
                        style = Theme2.monoCode.copy(
                            fontSize = 10.sp,
                            color = Theme2.onSurfaceVariant.copy(alpha = 0.6f)
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
                    label = "Dashboard",
                    icon = Icons.Default.Dashboard,
                    isActive = activeTab == HomeTab.Dashboard,
                    onClick = { onTabSelect(HomeTab.Dashboard) }
                )
                SidebarNavItem(
                    label = "Scripts",
                    icon = Icons.Default.Terminal,
                    isActive = activeTab == HomeTab.Scripts,
                    onClick = { onTabSelect(HomeTab.Scripts) }
                )
                SidebarNavItem(
                    label = "Certificates",
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
    val backgroundColor = if (isActive) Theme2.secondary.copy(alpha = 0.08f) else Color.Transparent
    val contentColor = if (isActive) Theme2.secondary else Theme2.onSurfaceVariant

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
                .padding(horizontal = Theme2.gutter),
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
                style = Theme2.bodyMd.copy(
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
                    .background(Theme2.secondary)
            )
        }
    }
}
