package com.darius.lionvpn.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.darius.lionvpn.getPlatform
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.Res
import lion_vpn.shared.generated.resources.back_button

@Composable
fun BackButton(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val backIcon = if (getPlatform().isIos()) Icons.AutoMirrored.Filled.ArrowBackIos
    else Icons.AutoMirrored.Filled.ArrowBack
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = modifier
    ) {
        Icon(
            imageVector = backIcon,
            contentDescription = stringResource(Res.string.back_button),
        )
    }
}
