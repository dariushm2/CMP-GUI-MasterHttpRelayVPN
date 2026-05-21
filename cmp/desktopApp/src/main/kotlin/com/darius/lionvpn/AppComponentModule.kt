package com.darius.lionvpn

import com.darius.lionvpn.connectivity.ConnectivityHandler
import com.darius.lionvpn.connectivity.NetworkHelper
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
