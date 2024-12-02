package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.imageload")
class ImageLoadModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.network")
class NetworkStateModule

val coreUtilsModule =
    module {
        includes(
            nativeUtilsModule,
            ImageLoadModule().module,
            NetworkStateModule().module,
        )
    }
