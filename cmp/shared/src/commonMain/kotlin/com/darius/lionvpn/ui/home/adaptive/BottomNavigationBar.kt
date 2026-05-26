package com.darius.lionvpn.ui.home.adaptive

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.home.HomeTab
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomNavigationBar(
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = surfaceContainerLowest,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        Item(HomeTab.Dashboard, activeTab, onTabSelect)
        Item(HomeTab.Scripts, activeTab, onTabSelect)
        Item(HomeTab.Certificates, activeTab, onTabSelect)
        Item(HomeTab.EditConfig, activeTab, onTabSelect)
        Item(HomeTab.About, activeTab, onTabSelect)
    }
}

@Composable
private fun RowScope.Item(
    tab: HomeTab,
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
) {
    NavigationBarItem(
        selected = activeTab == tab,
        onClick = { onTabSelect(tab) },
        icon = { Icon(tab.icon, contentDescription = null) },
        label = { Text(stringResource(tab.title), fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = secondary,
            selectedTextColor = secondary,
            indicatorColor = secondary.copy(alpha = 0.1f)
        )
    )
}
