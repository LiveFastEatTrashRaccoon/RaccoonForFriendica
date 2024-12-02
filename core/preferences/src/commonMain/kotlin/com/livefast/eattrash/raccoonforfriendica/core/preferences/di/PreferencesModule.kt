package com.livefast.eattrash.raccoonforfriendica.core.preferences.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.preferences")
class PreferencesModule

val corePreferencesModule =
    module {
        includes(
            nativePreferencesModule,
            PreferencesModule().module,
        )
    }
