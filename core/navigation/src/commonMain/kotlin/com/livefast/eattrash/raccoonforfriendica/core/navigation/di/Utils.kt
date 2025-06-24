package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.kodein.di.instance

fun getNavigationCoordinator(): NavigationCoordinator {
    val res by RootDI.di.instance<NavigationCoordinator>()
    return res
}

fun getDrawerCoordinator(): DrawerCoordinator {
    val res by RootDI.di.instance<DrawerCoordinator>()
    return res
}

fun getMainRouter(): MainRouter {
    val res by RootDI.di.instance<MainRouter>()
    return res
}
