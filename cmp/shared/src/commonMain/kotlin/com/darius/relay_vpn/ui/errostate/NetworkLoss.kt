package com.darius.relay_vpn.ui.errostate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.stringResource
import relay_vpn.shared.generated.resources.Res
import relay_vpn.shared.generated.resources.connection_lost

@OptIn(InternalResourceApi::class)
@Composable
fun NetworkLoss(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.Yellow)
    ) {
        Text(
            text = stringResource(Res.string.connection_lost),
            color = Color.Black,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}
