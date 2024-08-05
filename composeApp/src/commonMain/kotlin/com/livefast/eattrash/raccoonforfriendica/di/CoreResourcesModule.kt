package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.resources.SharedResources
import org.koin.dsl.module

internal val coreResourceModule =
    module {
        single<CoreResources> {
            SharedResources()
        }
    }
