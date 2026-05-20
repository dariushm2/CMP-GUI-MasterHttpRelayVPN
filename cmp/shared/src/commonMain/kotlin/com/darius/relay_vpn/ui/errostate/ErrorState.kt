package com.darius.relay_vpn.ui.errostate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import relay_vpn.shared.generated.resources.Res
import relay_vpn.shared.generated.resources.retry
import relay_vpn.shared.generated.resources.something_went_wrong

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(text = stringResource(Res.string.something_went_wrong))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onRetry) {
            Text(text = stringResource(Res.string.retry))
        }
    }
}
