package com.darius.relay_vpn.ext

inline fun <reified T> Any?.asType() = try {
    this as? T
} catch (e: ClassCastException) {
    null
}
