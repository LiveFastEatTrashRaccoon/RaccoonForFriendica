package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.resources.SharedResources
import com.livefast.eattrash.raccoonforfriendica.resources.SharedStrings
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.singleton

internal val resourcesModule =
    DI.Module("SharedResourcesModule") {
        bind<CoreResources> { singleton { SharedResources() } }
        bind<Strings> {
            factory { _: String ->
                // the locale parameter is currently ignored
                SharedStrings()
            }
        }
    }
