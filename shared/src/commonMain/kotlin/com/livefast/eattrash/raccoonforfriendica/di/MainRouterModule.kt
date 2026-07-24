package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.navigation.DefaultMainRouter
import org.koin.dsl.module

internal val mainRouterModule = module {
    single<MainRouter> {
        DefaultMainRouter(
            navigationCoordinator = get(),
            identityRepository = get(),
            settingsRepository = get(),
            userCache = get(),
            entryCache = get(),
            eventCache = get(),
            circleCache = get(),
            attachmentCache = get(),
        )
    }
}
