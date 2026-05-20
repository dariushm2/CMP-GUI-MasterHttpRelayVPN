package com.darius.relay_vpn

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import org.koin.android.ext.android.getKoin
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getKoin().get<ContextFactory>().attach(this)

        setContent {
            val data = this.intent.data
            data?.toString()?.let {
                DeepLinkHandler.setDeepLink(it)
            }

            // This will make sure the icons in status bar adapt to the theme
            val view = LocalView.current
            val darkTheme = isSystemInDarkTheme()
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            App(
                connectivityHandler = koinInject(),
            )
        }
    }
}
