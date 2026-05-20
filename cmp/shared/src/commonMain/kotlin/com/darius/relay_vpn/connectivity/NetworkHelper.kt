package com.darius.relay_vpn.connectivity

interface NetworkHelper {
    fun registerListener(
        onNetworkAvailable: () -> Unit,
        onNetworkLost: () -> Unit,
    )
    fun unregisterListener()
}
