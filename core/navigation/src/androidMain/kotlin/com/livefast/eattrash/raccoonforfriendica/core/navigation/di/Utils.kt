package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.koin.java.KoinJavaComponent

actual fun getNavigationCoordinator(): NavigationCoordinator {
    val res by KoinJavaComponent.inject<NavigationCoordinator>(NavigationCoordinator::class.java)
    return res
}

actual fun getDetailOpener(): DetailOpener {
    val res by KoinJavaComponent.inject<DetailOpener>(DetailOpener::class.java)
    return res
}
