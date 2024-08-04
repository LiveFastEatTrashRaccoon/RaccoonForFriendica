package com.github.akesiseli.raccoonforfriendica.di

import com.github.akesiseli.raccoonforfriendica.core.resources.CoreResources
import com.github.akesiseli.raccoonforfriendica.resources.SharedResources
import org.koin.dsl.module

internal val coreResourceModule =
    module {
        single<CoreResources> {
            SharedResources()
        }
    }
