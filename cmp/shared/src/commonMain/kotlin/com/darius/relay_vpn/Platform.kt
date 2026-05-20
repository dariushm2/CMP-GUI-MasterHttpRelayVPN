package com.darius.relay_vpn

interface Platform {
    val name: String
    val type: Type

    fun isAndroid(): Boolean = false
    fun isIos(): Boolean = false
    fun isMac(): Boolean = false
    fun isWin(): Boolean = false
    fun isLinux(): Boolean = false

    enum class Type {
        ANDROID,
        IOS,
        MAC,
        WIN,
        LINUX,
    }
}

expect fun getPlatform(): Platform

expect fun isDebugBuild(): Boolean
