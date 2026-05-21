package com.darius.lionvpn


actual fun debugLog(message: String?) {
    println(message)
}

actual fun errorLog(throwable: Throwable?) {
    println(throwable)
}
