package com.darius.lionvpn.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darius.lionvpn.isDebugBuild
import com.darius.lionvpn.ui.navigation.Screen
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.debug

@Composable
fun DebugButton(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    if (isDebugBuild()) {
        Text(
            text = stringResource(Res.string.debug),
            style = MaterialTheme.typography.labelSmall,
            modifier = modifier
                .clickable {
                    navController.navigate(Screen.Debug.route) {
                        launchSingleTop = true
                    }
                }
                .padding(start = 8.dp)
        )
    }
}
