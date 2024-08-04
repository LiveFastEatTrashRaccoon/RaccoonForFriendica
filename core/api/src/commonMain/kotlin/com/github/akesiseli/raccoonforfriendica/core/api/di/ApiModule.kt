package com.github.akesiseli.raccoonforfriendica.core.api.di

import com.github.akesiseli.raccoonforfriendica.core.api.provider.DefaultServiceProvider
import com.github.akesiseli.raccoonforfriendica.core.api.provider.ServiceProvider
import org.koin.dsl.module

val coreApiModule =
    module {
        single<ServiceProvider> {
            DefaultServiceProvider()
        }
    }
