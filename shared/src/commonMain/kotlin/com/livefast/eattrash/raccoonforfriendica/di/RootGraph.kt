package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.di.utils.UiDeps
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

interface RootGraph : ViewModelGraph {
    val authManager: AuthManager
    val mainRouter: MainRouter
    val navigationCoordinator: NavigationCoordinator
    val uiDeps: UiDeps
}
