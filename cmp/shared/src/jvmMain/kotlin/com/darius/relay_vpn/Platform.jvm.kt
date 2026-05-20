package com.darius.relay_vpn

import com.darius.relay_vpn.connectivity.NetworkHelper


class JvmPlatform : Platform {
    override val type: Platform.Type = when {
        System.getProperty("os.name").contains("mac", ignoreCase = true)
            -> Platform.Type.MAC
        System.getProperty("os.name").contains("win", ignoreCase = true)
            -> Platform.Type.WIN
        else -> Platform.Type.LINUX
    }
    override val name: String = type.name
    override fun isMac(): Boolean = type == Platform.Type.MAC
    override fun isWin(): Boolean = type == Platform.Type.WIN
    override fun isLinux(): Boolean = type == Platform.Type.LINUX
}

actual fun getPlatform(): Platform = JvmPlatform()

class JvmApplicationComponent(
    val networkHelper: NetworkHelper,
)

actual fun isDebugBuild(): Boolean = true
