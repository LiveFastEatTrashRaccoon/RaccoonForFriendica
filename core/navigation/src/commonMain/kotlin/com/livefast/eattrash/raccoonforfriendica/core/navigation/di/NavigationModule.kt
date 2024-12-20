package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val navigationModule =
    DI.Module("NavigationModule") {
        bind<NavigationCoordinator> { singleton { DefaultNavigationCoordinator() } }
        bind<DrawerCoordinator> { singleton { DefaultDrawerCoordinator() } }
    }
