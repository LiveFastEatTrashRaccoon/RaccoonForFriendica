package com.github.akesiseli.raccoonforfriendica.core.navigation.di

import com.github.akesiseli.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getNavigationCoordinator(): NavigationCoordinator = CoreNavigationDiHelper.navigationCoordinator

internal object CoreNavigationDiHelper : KoinComponent {
    val navigationCoordinator: NavigationCoordinator by inject()
}
