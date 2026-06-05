package com.darius.lionvpn.ui.home.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.home.HomeTab
import com.darius.lionvpn.ui.theme.primary
import com.darius.lionvpn.ui.theme.roundedDefault
import com.darius.lionvpn.ui.theme.surfaceContainerLowest
import com.darius.lionvpn.ui.theme.secondary
import com.darius.lionvpn.ui.theme.stackLg
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationRailBar(
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
            .width(72.dp)
    ) {
        val allItemsVisible = maxHeight >= 520.dp

        NavigationRail(
            containerColor = surfaceContainerLowest,
            windowInsets = NavigationRailDefaults.windowInsets.only(WindowInsetsSides.Vertical),
            header = {
                // Compact Logo - padding collapses to minimal when space is constrained
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
            },
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceContainerLowest)
                .padding(vertical = if (allItemsVisible) stackLg else 0.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                Item(HomeTab.Dashboard, activeTab, onTabSelect)
                Spacer(modifier = Modifier.height(8.dp))
                Item(HomeTab.Scripts, activeTab, onTabSelect)
                Spacer(modifier = Modifier.height(8.dp))
                Item(HomeTab.Settings, activeTab, onTabSelect)
                Spacer(modifier = Modifier.height(8.dp))
                Item(HomeTab.About, activeTab, onTabSelect)
            }
        }
    }
}

@Composable
private fun Item(
    tab: HomeTab,
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
) {
    NavigationRailItem(
        selected = activeTab == tab,
        onClick = { onTabSelect(tab) },
        icon = { Icon(tab.icon, contentDescription = null) },
        label = { Text(stringResource(tab.title), fontSize = 10.sp) },
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = secondary,
            selectedTextColor = secondary,
            indicatorColor = secondary.copy(alpha = 0.1f),
        )
    )
}
