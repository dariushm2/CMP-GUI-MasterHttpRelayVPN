package com.darius.relay_vpn.domain.di

import com.darius.relay_vpn.domain.KtorRecipeRepo
import com.darius.relay_vpn.domain.RecipesRepo
import org.koin.dsl.module

val domainModule = module {

    single<RecipesRepo> {
        KtorRecipeRepo(get())
    }
}
