package com.darius.lionvpn.ui.home.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darius.lionvpn.BuildConfig
import com.darius.lionvpn.ui.home.HomeTab
import com.darius.lionvpn.ui.model.Lang
import com.darius.lionvpn.ui.theme.*
import org.jetbrains.compose.resources.stringResource
import lion_vpn.shared.generated.resources.*

@Composable
fun Sidebar(
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
    language: Lang,
    onLanguageChange: (Lang) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = outlineVariant,
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(vertical = stackLg),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Logo / Branding Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = gutter)
                    .padding(bottom = stackLg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🦁",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(primary.copy(alpha = 0.15f), roundedDefault)
                        .border(1.dp, primary.copy(alpha = 0.3f), roundedDefault)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = headlineMd.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                    )
                    Text(
                        text = BuildConfig.APP_VERSION,
                        style = monoCode.copy(
                            fontSize = 10.sp,
                            color = onSurfaceVariant.copy(alpha = 0.6f),
                            textDirection = TextDirection.Ltr,
                        ),
                    )
                }
            }

            // Navigation Links
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SidebarNavItem(
                    label = stringResource(Res.string.tab_dashboard),
                    icon = Icons.Default.Dashboard,
                    isActive = activeTab == HomeTab.Dashboard,
                    onClick = { onTabSelect(HomeTab.Dashboard) }
                )
                SidebarNavItem(
                    label = stringResource(Res.string.tab_scripts),
                    icon = Icons.Default.Terminal,
                    isActive = activeTab == HomeTab.Scripts,
                    onClick = { onTabSelect(HomeTab.Scripts) }
                )
                SidebarNavItem(
                    label = stringResource(Res.string.tab_certificates),
                    icon = Icons.Default.VerifiedUser,
                    isActive = activeTab == HomeTab.Certificates,
                    onClick = { onTabSelect(HomeTab.Certificates) }
                )
                SidebarNavItem(
                    label = stringResource(Res.string.tab_edit_config),
                    icon = Icons.Default.Settings,
                    isActive = activeTab == HomeTab.EditConfig,
                    onClick = { onTabSelect(HomeTab.EditConfig) }
                )
                SidebarNavItem(
                    label = stringResource(Res.string.tab_about),
                    icon = Icons.Default.Info,
                    isActive = activeTab == HomeTab.About,
                    onClick = { onTabSelect(HomeTab.About) }
                )
            }
        }

        // Language Toggle Switch at the bottom of the sidebar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = gutter),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (language == Lang.EN) "Language" else "زبان برنامه",
                style = bodySm.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(surfaceContainerLow, roundedDefault)
                    .border(1.dp, outlineVariant.copy(alpha = 0.5f), roundedDefault)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Persian Option
                val isFa = language == Lang.FA
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = if (isFa) secondary.copy(alpha = 0.15f) else Color.Transparent,
                            shape = roundedDefault
                        )
                        .border(
                            width = if (isFa) 1.dp else 0.dp,
                            color = if (isFa) secondary.copy(alpha = 0.4f) else Color.Transparent,
                            shape = roundedDefault
                        )
                        .clickable { onLanguageChange(Lang.FA) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "فارسی",
                        style = bodySm.copy(
                            fontWeight = if (isFa) FontWeight.Bold else FontWeight.Normal,
                            color = if (isFa) secondary else onSurfaceVariant
                        )
                    )
                }

                // English Option
                val isEn = language == Lang.EN
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = if (isEn) secondary.copy(alpha = 0.15f) else Color.Transparent,
                            shape = roundedDefault
                        )
                        .border(
                            width = if (isEn) 1.dp else 0.dp,
                            color = if (isEn) secondary.copy(alpha = 0.4f) else Color.Transparent,
                            shape = roundedDefault
                        )
                        .clickable { onLanguageChange(Lang.EN) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "English",
                        style = bodySm.copy(
                            fontWeight = if (isEn) FontWeight.Bold else FontWeight.Normal,
                            color = if (isEn) secondary else onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarNavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) secondary.copy(alpha = 0.08f) else Color.Transparent
    val contentColor = if (isActive) secondary else onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = gutter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$label Icon",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = bodyMd.copy(
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor
                )
            )
        }

        // Right indicator line if active
        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(secondary)
            )
        }
    }
}
