package com.darius.lionvpn.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.darius.lionvpn.ui.home.adaptive.BottomNavigationBar
import com.darius.lionvpn.ui.home.adaptive.CompactTopBar
import com.darius.lionvpn.ui.home.adaptive.NavigationRailBar
import com.darius.lionvpn.ui.home.adaptive.Sidebar
import com.darius.lionvpn.ui.home.adaptive.WindowWidthSizeClass
import com.darius.lionvpn.ui.home.adaptive.calculateWindowWidthSizeClass
import com.darius.lionvpn.ui.home.dashboard.DashboardTab
import com.darius.lionvpn.ui.home.settings.SettingsTab
import com.darius.lionvpn.ui.theme.background
import com.darius.lionvpn.ui.theme.primary

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeTab by remember { mutableStateOf(HomeTab.Dashboard) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val windowSizeClass = calculateWindowWidthSizeClass(maxWidth)
        val isCompact = windowSizeClass == WindowWidthSizeClass.Compact
        val isMedium = windowSizeClass == WindowWidthSizeClass.Medium
        val isExpanded = windowSizeClass == WindowWidthSizeClass.Expanded

        Scaffold(
            topBar = {
                if (isCompact) {
                    CompactTopBar()
                }
            },
            bottomBar = {
                if (isCompact) {
                    BottomNavigationBar(
                        activeTab = activeTab,
                        onTabSelect = { activeTab = it }
                    )
                }
            },
            containerColor = background
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Side Navigation: Medium (Rail) vs. Expanded (Sidebar)
                when {
                    isMedium -> {
                        NavigationRailBar(
                            activeTab = activeTab,
                            onTabSelect = { activeTab = it }
                        )
                    }
                    isExpanded -> {
                        Sidebar(
                            activeTab = activeTab,
                            onTabSelect = { activeTab = it }
                        )
                    }
                }

                // Main Content Area with elegant fade transitions
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(background)
                        .padding(innerPadding)
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
                            HomeTab.Dashboard -> DashboardTab(state, onClick)
                            HomeTab.Scripts -> ScriptsTab(state, onClick)
                            HomeTab.Settings -> SettingsTab(state, onClick)
                            HomeTab.About -> AboutTab()
                        }
                    }
                }
            }
        }
    }
}
