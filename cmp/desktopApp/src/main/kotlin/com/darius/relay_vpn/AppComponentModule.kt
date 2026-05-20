package com.darius.relay_vpn

import com.darius.relay_vpn.connectivity.ConnectivityHandler
import com.darius.relay_vpn.connectivity.NetworkHelper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appComponentModule = module {
    single {
        ContextFactory()
    }
    single<NetworkHelper> {
        JvmNetworkHelper()
    }
    single {
        ConnectivityHandler(get())
    }
    viewModel {
        AppViewModel()
    }
}
