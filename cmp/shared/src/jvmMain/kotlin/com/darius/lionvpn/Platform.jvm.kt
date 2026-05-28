package com.darius.lionvpn

import com.darius.lionvpn.connectivity.NetworkHelper


class JvmPlatform : Platform {

    enum class OS {
        MAC,
        WIN,
        LINUX,
    }
    val os: OS = when {
        System.getProperty("os.name").contains("mac", ignoreCase = true)
            -> OS.MAC
        System.getProperty("os.name").contains("win", ignoreCase = true)
            -> OS.WIN
        else -> OS.LINUX
    }
    override val type: Platform.Type = Platform.Type.JVM
    override val name: String = type.name
    override fun isMac(): Boolean = os == OS.MAC
    override fun isWin(): Boolean = os == OS.WIN
    override fun isLinux(): Boolean = os == OS.LINUX
}

actual fun getPlatform(): Platform = JvmPlatform()

class JvmApplicationComponent(
    val networkHelper: NetworkHelper,
)

actual fun isDebugBuild(): Boolean = true

actual fun getCurrentTimeString(): String {
    return java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
}
