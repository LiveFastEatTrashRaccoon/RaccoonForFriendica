package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import coil3.PlatformContext
import org.koin.dsl.module

internal actual val nativeImageLoadModule = module {
    single<PlatformContext> {
        PlatformContext.INSTANCE
    }
}
