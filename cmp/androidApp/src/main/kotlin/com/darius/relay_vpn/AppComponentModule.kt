package com.darius.relay_vpn

import com.darius.relay_vpn.connectivity.ConnectivityHandler
import com.darius.relay_vpn.connectivity.NetworkHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appComponentModule = module {
    single {
        ContextFactory()
    }
    single<NetworkHelper> {
        AndroidNetworkHelper(androidContext())
    }
    single {
        ConnectivityHandler(get())
    }
}
