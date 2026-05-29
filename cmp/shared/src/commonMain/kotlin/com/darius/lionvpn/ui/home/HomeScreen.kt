package com.darius.lionvpn.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.darius.lionvpn.ui.home.adaptive.*
import com.darius.lionvpn.ui.home.dashboard.DashboardTab
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.model.Lang.Companion.isEnglish
import com.darius.lionvpn.ui.theme.*

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
                    CompactTopBar(
                        language = state.language,
                        onLanguageToggle = {
                            val nextLang = if (state.language.isEnglish()) Lang.FA else Lang.EN
                            onClick(Event.ChangeLanguage(nextLang))
                        }
                    )
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
                            onTabSelect = { activeTab = it },
                            language = state.language,
                            onLanguageToggle = {
                                val nextLang = if (state.language == Lang.EN) Lang.FA else Lang.EN
                                onClick(Event.ChangeLanguage(nextLang))
                            }
                        )
                    }
                    isExpanded -> {
                        Sidebar(
                            activeTab = activeTab,
                            onTabSelect = { activeTab = it },
                            language = state.language,
                            onLanguageChange = { onClick(Event.ChangeLanguage(it)) }
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
                            HomeTab.EditConfig -> EditConfigTab(state, onClick)
                            HomeTab.Certificates -> CertificatesTab(onClick = onClick)
                            HomeTab.About -> AboutTab()
                        }
                    }
                }
            }
        }
    }
}
