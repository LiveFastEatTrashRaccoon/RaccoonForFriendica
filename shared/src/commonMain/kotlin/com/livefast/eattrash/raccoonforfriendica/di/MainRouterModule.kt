package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.navigation.DefaultMainRouter
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal val mainRouterModule =
    DI.Module("MainRouterModule") {
        bindSingleton<MainRouter> {
            DefaultMainRouter(
                navigationCoordinator = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                userCache = instance(),
                entryCache = instance(),
                eventCache = instance(),
                circleCache = instance(),
                attachmentCache = instance(),
            )
        }
    }
