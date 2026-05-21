package com.darius.lionvpn

import timber.log.Timber

actual fun debugLog(message: String?) {
    Timber.d(message)
}

actual fun errorLog(throwable: Throwable?) {
    Timber.e(throwable)
}
