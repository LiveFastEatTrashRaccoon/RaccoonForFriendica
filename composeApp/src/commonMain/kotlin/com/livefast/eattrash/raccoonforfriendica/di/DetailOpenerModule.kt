package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.navigation.DefaultDetailOpener
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal val detailOpenerModule =
    DI.Module("DetailOpenerModule") {
        bind<DetailOpener> {
            singleton {
                DefaultDetailOpener(
                    navigationCoordinator = instance(),
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    userCache = instance(),
                    entryCache = instance(),
                    eventCache = instance(),
                    circleCache = instance(),
                )
            }
        }
    }
