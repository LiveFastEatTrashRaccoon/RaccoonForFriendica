package com.github.akesiseli.raccoonforfriendica.core.navigation.di

import com.github.akesiseli.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.koin.java.KoinJavaComponent

actual fun getNavigationCoordinator(): NavigationCoordinator {
    val res by KoinJavaComponent.inject<NavigationCoordinator>(NavigationCoordinator::class.java)
    return res
}
