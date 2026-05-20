package com.darius.relay_vpn

import platform.Foundation.NSLog
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun debugLog(message: String?) {
    message?.let {  NSLog("DEBUG: %@", it) }
}

@OptIn(ExperimentalForeignApi::class)
actual fun errorLog(throwable: Throwable?) {
    throwable?.let {  NSLog("ERROR: %@", it.message) }
}
