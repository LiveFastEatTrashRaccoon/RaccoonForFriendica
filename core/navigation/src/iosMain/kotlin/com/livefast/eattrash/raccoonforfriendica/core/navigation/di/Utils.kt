package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getNavigationCoordinator(): NavigationCoordinator = CoreNavigationDiHelper.navigationCoordinator

actual fun getDetailOpener(): DetailOpener = CoreNavigationDiHelper.detailOpener

internal object CoreNavigationDiHelper : KoinComponent {
    val navigationCoordinator: NavigationCoordinator by inject()
    val detailOpener: DetailOpener by inject()
}
