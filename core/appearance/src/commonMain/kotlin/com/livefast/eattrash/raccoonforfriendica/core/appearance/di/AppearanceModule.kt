package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module
import org.koin.core.module.Module as KoinModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.appearance.repository")
internal class AppearanceRepositoryModule

val coreAppearanceModule: KoinModule =
    module {
        includes(
            nativeAppearanceModule,
            AppearanceRepositoryModule().module,
        )
    }
