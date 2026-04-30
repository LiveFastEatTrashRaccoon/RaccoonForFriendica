package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.resources.SharedResources
import com.livefast.eattrash.raccoonforfriendica.resources.SharedStrings
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindSingleton

internal val resourcesModule =
    DI.Module("SharedResourcesModule") {
        bindSingleton<CoreResources> { SharedResources() }
        bindFactory<String, Strings> { _ ->
            // the locale parameter is currently ignored
            SharedStrings()
        }
    }
