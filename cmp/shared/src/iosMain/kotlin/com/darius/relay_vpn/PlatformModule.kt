package com.darius.relay_vpn

import com.darius.relay_vpn.connectivity.ConnectivityHandler
import com.darius.relay_vpn.connectivity.NetworkHelper
import org.koin.dsl.module

actual val platformModule = module {
    single<NetworkHelper> {
        get<IosApplicationComponent>().networkHelper
    }
    single {
        ConnectivityHandler(get())
    }
}
