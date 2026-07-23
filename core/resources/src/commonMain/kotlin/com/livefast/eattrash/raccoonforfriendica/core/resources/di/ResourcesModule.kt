package com.livefast.eattrash.raccoonforfriendica.core.resources.di

import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.DefaultCoreResources
import org.koin.dsl.module

val resourcesModule = module {
    single<CoreResources> {
        DefaultCoreResources()
    }
}
