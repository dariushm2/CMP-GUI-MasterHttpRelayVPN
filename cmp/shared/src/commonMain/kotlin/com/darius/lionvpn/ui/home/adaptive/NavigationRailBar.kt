package com.darius.lionvpn.ui.home.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.home.HomeTab
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationRailBar(
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
    language: Lang,
    onLanguageToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = surfaceContainerLowest,
        header = {
            // Compact Logo
            Text(
                text = "🦁",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(primary.copy(alpha = 0.15f), roundedDefault)
                    .border(1.dp, primary.copy(alpha = 0.3f), roundedDefault)
            )
        },
        modifier = modifier
            .fillMaxHeight()
            .width(72.dp)

    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Item(HomeTab.Dashboard, activeTab, onTabSelect)
            Spacer(modifier = Modifier.height(8.dp))
            Item(HomeTab.Scripts, activeTab, onTabSelect)
            Spacer(modifier = Modifier.height(8.dp))
            Item(HomeTab.Certificates, activeTab, onTabSelect)
            Spacer(modifier = Modifier.height(8.dp))
            Item(HomeTab.EditConfig, activeTab, onTabSelect)
            Spacer(modifier = Modifier.height(8.dp))
            Item(HomeTab.About, activeTab, onTabSelect)
        }

        // Language Switcher at the bottom
        IconButton(
            onClick = onLanguageToggle,
            modifier = Modifier
                .size(40.dp)
                .background(surfaceContainerLow, roundedDefault)
                .border(1.dp, outlineVariant.copy(alpha = 0.5f), roundedDefault)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Switch Language",
                    tint = secondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (language == Lang.EN) "FA" else "EN",
                    style = labelCaps.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = secondary)
                )
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

