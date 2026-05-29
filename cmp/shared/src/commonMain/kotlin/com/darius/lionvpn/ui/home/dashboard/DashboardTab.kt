package com.darius.lionvpn.ui.home.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darius.lionvpn.ui.home.Event
import com.darius.lionvpn.ui.home.HomeState
import com.darius.lionvpn.ui.model.SavedConfig
import com.darius.lionvpn.ui.theme.containerPadding
import com.darius.lionvpn.ui.theme.gutter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun PortraitDashboard(
    state: HomeState,
    isConnectEnabled: Boolean,
    address: String,
    executionCount: Int,
    activeConfig: SavedConfig?,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(containerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gutter)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .heightIn(max = 280.dp),
            horizontalArrangement = Arrangement.spacedBy(gutter)
        ) {
            ConnectionHeroCard(
                vpnState = state.connectionState,
                isConnectEnabled = isConnectEnabled,
                address = address,
                onConnectToggle = { if (isConnectEnabled) onClick(Event.Connect) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            StatsPanelGrid(
                activeConfig = activeConfig,
                executionCount = executionCount,
                totalScriptsCount = state.savedConfigs.size,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        TerminalLogConsole(
            logs = state.log,
            onClearClick = { onClick(Event.ClearLogs) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
fun LandscapeDashboard(
    state: HomeState,
    isConnectEnabled: Boolean,
    address: String,
    executionCount: Int,
    activeConfig: SavedConfig?,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(containerPadding),
        horizontalArrangement = Arrangement.spacedBy(gutter),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .heightIn(max = 320.dp),
            verticalArrangement = Arrangement.spacedBy(gutter)
        ) {
            ConnectionHeroCard(
                vpnState = state.connectionState,
                isConnectEnabled = isConnectEnabled,
                address = address,
                onConnectToggle = { if (isConnectEnabled) onClick(Event.Connect) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            StatsPanelGrid(
                activeConfig = activeConfig,
                executionCount = executionCount,
                totalScriptsCount = state.savedConfigs.size,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }

        TerminalLogConsole(
            logs = state.log,
            onClearClick = { onClick(Event.ClearLogs) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
fun DashboardTab(
    state: HomeState,
    onClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeConfig = if (state.selectedConfigIndex in state.savedConfigs.indices) {
        state.savedConfigs[state.selectedConfigIndex]
    } else {
        null
    }
    val isConnectEnabled = activeConfig != null && activeConfig.id.isNotEmpty() && activeConfig.key.isNotEmpty()
    val address = remember(state.rawConfigJson) {
        try {
            val obj = Json.parseToJsonElement(state.rawConfigJson).jsonObject
            val host = obj["listen_host"]?.jsonPrimitive?.content ?: "127.0.0.1"
            val port = obj["http_port"]?.jsonPrimitive?.intOrNull ?: 8085
            "$host:$port"
        } catch (e: Exception) {
            "127.0.0.1:8085"
        }
    }
    val executionCount = remember(state.log) {
        try {
            state.log.asReversed().firstOrNull {
                it.contains("Apps Script executions used so far:", ignoreCase = true)
            }?.substringAfter("Apps Script executions used so far:")
                ?.trim()
                ?.substringBefore(" ")
                ?.trim()
                ?.toIntOrNull() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val showSideBySide = maxHeight < 480.dp
        if (!showSideBySide) {
            PortraitDashboard(
                state = state,
                isConnectEnabled = isConnectEnabled,
                address = address,
                executionCount = executionCount,
                activeConfig = activeConfig,
                onClick = onClick
            )
        } else {
            LandscapeDashboard(
                state = state,
                isConnectEnabled = isConnectEnabled,
                address = address,
                executionCount = executionCount,
                activeConfig = activeConfig,
                onClick = onClick
            )
        }
    }
}
