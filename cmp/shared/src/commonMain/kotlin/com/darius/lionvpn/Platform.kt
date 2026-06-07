package com.darius.lionvpn

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
       JVM,
    }
}

expect fun getPlatform(): Platform

expect fun isDebugBuild(): Boolean

expect fun getCurrentTimeString(): String

expect fun getCurrentTimeMillis(): Long
