package com.darius.lionvpn

import androidx.activity.ComponentActivity

actual class ContextFactory {

    private lateinit var activity: ComponentActivity

    fun attach(activity: ComponentActivity) {
        this.activity = activity
    }
    actual fun getContext(): Any = activity
    actual fun getApplication(): Any = activity.application
    actual fun getActivity(): Any = activity
}
