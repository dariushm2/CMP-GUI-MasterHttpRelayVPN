package com.darius.relay_vpn

import androidx.compose.ui.awt.ComposeWindow

actual class ContextFactory {

    // For resources (similar to NSBundle)
    actual fun getContext(): Any = ContextFactory::class.java.classLoader
        ?: Thread.currentThread().contextClassLoader
        ?: ClassLoader.getSystemClassLoader()

    // On Desktop there's no direct "Application" equivalent like UIApplication.
    // You can return the main class, system properties, or a custom object.
    actual fun getApplication(): Any = ApplicationInfo

    // For "current screen/activity" – on Desktop we usually use Compose Window
    // You can pass the window from your main() or use a holder.
    actual fun getActivity(): Any = currentWindowHolder.window
        ?: "No active window"
}

// Simple holder for the current window (set it once in your main())
object currentWindowHolder {
    var window: ComposeWindow? = null
}

// Optional: helper object
object ApplicationInfo {
    val name: String = "YourAppName" // or read from manifest / build config
    val version: String = System.getProperty("app.version") ?: "unknown"
    val os: String = System.getProperty("os.name") ?: "unknown"
}
