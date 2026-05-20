package com.darius.relay_vpn

import com.darius.relay_vpn.data.di.networkModule
import com.darius.relay_vpn.domain.di.domainModule
import com.darius.relay_vpn.ui.di.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            listOf(
                appComponentModule,
                appModule,
                networkModule,
                domainModule,
                viewModelModule,
            )
        )
    }
}
