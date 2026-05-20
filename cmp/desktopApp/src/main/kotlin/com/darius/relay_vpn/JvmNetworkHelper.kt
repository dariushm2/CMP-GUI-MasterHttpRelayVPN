package com.darius.relay_vpn

import com.darius.relay_vpn.connectivity.NetworkHelper

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
