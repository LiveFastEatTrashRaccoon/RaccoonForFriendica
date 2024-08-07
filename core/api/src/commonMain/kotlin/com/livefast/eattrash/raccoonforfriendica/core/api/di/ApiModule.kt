package com.livefast.eattrash.raccoonforfriendica.core.api.di

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.DefaultServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreApiModule =
    module {
        single<ServiceProvider>(named("default")) {
            DefaultServiceProvider()
        }
        factory<ServiceProvider>(named("other")) {
            DefaultServiceProvider()
        }
    }
