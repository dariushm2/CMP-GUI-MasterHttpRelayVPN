package com.darius.lionvpn

import com.darius.lionvpn.ui.ScreenViewModel
import com.darius.lionvpn.ui.navigation.Screen
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module
import kotlin.reflect.KClass

val viewModelModule = module {

    //viewModelOf(::RecipesViewModel)
    viewModel { (screen: Screen) ->
        listViewModel(screen.toViewModelClass())
    }
}

fun Scope.listViewModel(viewModelClass: KClass<out ScreenViewModel<out Screen>>):
        ScreenViewModel<out Screen> = get(viewModelClass)

fun Screen.toViewModelClass(): KClass<out ScreenViewModel<out Screen>> = when (this) {
    //is Screen.Recipe -> RecipeViewModel::class
    is Screen.Debug -> throw UnsupportedOperationException("Debug screen does not use a ViewModel")
}
