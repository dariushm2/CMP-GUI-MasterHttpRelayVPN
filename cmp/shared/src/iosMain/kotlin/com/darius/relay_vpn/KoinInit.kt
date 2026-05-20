package com.darius.relay_vpn

import com.darius.relay_vpn.ui.di.appModule
import com.darius.relay_vpn.data.di.networkModule
import com.darius.relay_vpn.domain.di.domainModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(
    appComponent: IosApplicationComponent,
) {
    startKoin {
        modules(
            listOf(
                module { single { appComponent } },
                platformModule,
                appModule,
                networkModule,
                domainModule,
                viewModelModule,
            )
        )
    }
}
