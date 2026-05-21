package com.darius.lionvpn

import com.darius.lionvpn.connectivity.ConnectivityHandler
import com.darius.lionvpn.connectivity.NetworkHelper
import org.koin.dsl.module

actual val platformModule = module {
    single {
        ContextFactory()
    }
    single<NetworkHelper> {
        get<AndroidApplicationComponent>().networkHelper
    }
    single {
        ConnectivityHandler(get())
    }
}
