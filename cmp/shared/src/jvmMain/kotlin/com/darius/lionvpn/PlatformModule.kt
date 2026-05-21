package com.darius.lionvpn

import com.darius.lionvpn.connectivity.ConnectivityHandler
import com.darius.lionvpn.connectivity.NetworkHelper
import org.koin.dsl.module

actual val platformModule = module {
    single<NetworkHelper> {
        get<JvmApplicationComponent>().networkHelper
    }
    single {
        ConnectivityHandler(get())
    }
}
