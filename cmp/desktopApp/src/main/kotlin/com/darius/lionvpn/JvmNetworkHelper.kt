package com.darius.lionvpn

import com.darius.lionvpn.connectivity.NetworkHelper

class JvmNetworkHelper : NetworkHelper {
    override fun registerListener(
        onNetworkAvailable: () -> Unit,
        onNetworkLost: () -> Unit
    ) {
        // TODO
    }

    override fun unregisterListener() {
        // TODO
    }
}
