package com.darius.lionvpn

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.darius.lionvpn.connectivity.NetworkHelper
import com.darius.lionvpn.ext.asType

class AndroidNetworkHelper(
    context: Context,
) : NetworkHelper {

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivityManager = context
        .getSystemService(Context.CONNECTIVITY_SERVICE)
        .asType<ConnectivityManager>()

    override fun registerListener(
        onNetworkAvailable: () -> Unit,
        onNetworkLost: () -> Unit,
    ) {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onNetworkAvailable()
            }
            override fun onUnavailable() {
                onNetworkLost()
            }
            override fun onLost(network: Network) {
                onNetworkLost()
            }
        }
        networkCallback?.let { connectivityManager?.registerDefaultNetworkCallback(it) }
    }

    override fun unregisterListener() {
        networkCallback?.let { connectivityManager?.unregisterNetworkCallback(it) }
    }
}
