package com.darius.lionvpn.ui.home.adaptive

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTopBar(
    language: Lang,
    onLanguageToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🦁", fontSize = 24.sp)
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
        actions = {
            TextButton(
                onClick = onLanguageToggle,
                colors = ButtonDefaults.textButtonColors(contentColor = secondary)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Switch Language",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (language == Lang.EN) "فارسی" else "English",
                    style = labelCaps.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = surfaceContainerLowest
        ),
        modifier = modifier
    )
}
