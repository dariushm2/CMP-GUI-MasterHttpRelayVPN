package com.darius.lionvpn.connectivity

interface NetworkHelper {
    fun registerListener(
        onNetworkAvailable: () -> Unit,
        onNetworkLost: () -> Unit,
    )
    fun unregisterListener()
}
