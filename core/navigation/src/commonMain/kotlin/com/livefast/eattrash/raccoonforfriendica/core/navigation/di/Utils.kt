package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerCoordinator
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

fun getDetailOpener(): DetailOpener {
    val res by RootDI.di.instance<DetailOpener>()
    return res
}
