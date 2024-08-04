package com.github.akesiseli.raccoonforfriendica.core.navigation.di

import com.github.akesiseli.raccoonforfriendica.core.navigation.DefaultNavigationCoordinator
import com.github.akesiseli.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.koin.dsl.module

val coreNavigationModule =
    module {
        single<NavigationCoordinator> {
            DefaultNavigationCoordinator()
        }
    }
