package com.darius.lionvpn.ui.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {
    single<CoroutineDispatcher> { Dispatchers.Main }
}
