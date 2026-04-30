package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.kodein.di.DI
import org.kodein.di.bindSingleton

val navigationModule =
    DI.Module("NavigationModule") {
        bindSingleton<NavigationCoordinator> {
            DefaultNavigationCoordinator()
        }
        bindSingleton<DrawerCoordinator> {
            DefaultDrawerCoordinator()
        }
    }
