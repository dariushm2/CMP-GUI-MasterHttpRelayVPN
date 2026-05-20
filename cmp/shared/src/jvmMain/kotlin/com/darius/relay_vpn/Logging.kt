package com.darius.relay_vpn


actual fun debugLog(message: String?) {
    println(message)
}

actual fun errorLog(throwable: Throwable?) {
    println(throwable)
}
