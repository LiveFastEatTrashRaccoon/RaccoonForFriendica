package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.koin.dsl.module

val coreNavigationModule =
    module {
        single<NavigationCoordinator> {
            DefaultNavigationCoordinator()
        }
        single<DrawerCoordinator> {
            DefaultDrawerCoordinator()
        }
    }
