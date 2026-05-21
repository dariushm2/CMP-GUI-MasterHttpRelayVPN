package com.darius.lionvpn.connectivity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

class ConnectivityHandler(private val helper: NetworkHelper) {

    val isConnected: Flow<Boolean> = callbackFlow {
        helper.registerListener(
            onNetworkAvailable = {
                trySend(true)
            },
            onNetworkLost = {
                trySend(false)
            }
        )

        awaitClose {
            helper.unregisterListener()
        }
    }.distinctUntilChanged().flowOn(Dispatchers.IO)
}
