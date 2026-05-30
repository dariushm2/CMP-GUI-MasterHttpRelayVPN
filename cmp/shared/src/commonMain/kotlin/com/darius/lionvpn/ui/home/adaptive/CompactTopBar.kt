package com.darius.lionvpn.ui.home.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🦁",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .background(primary.copy(alpha = 0.15f), roundedDefault)
                            .border(1.dp, primary.copy(alpha = 0.3f), roundedDefault)
                            .offset(y = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = headlineMd.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = surfaceContainerLowest
        ),
        modifier = modifier
    )
}
