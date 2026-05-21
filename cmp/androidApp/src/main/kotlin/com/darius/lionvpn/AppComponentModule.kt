package com.darius.lionvpn

import com.darius.lionvpn.connectivity.ConnectivityHandler
import com.darius.lionvpn.connectivity.NetworkHelper
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
