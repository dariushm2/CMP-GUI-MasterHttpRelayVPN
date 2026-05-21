package com.darius.lionvpn.ext

inline fun <reified T> Any?.asType() = try {
    this as? T
} catch (e: ClassCastException) {
    null
}
