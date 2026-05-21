package com.darius.lionvpn

import com.darius.lionvpn.ui.di.appModule
import com.darius.lionvpn.data.di.networkModule
import com.darius.lionvpn.domain.di.domainModule
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
