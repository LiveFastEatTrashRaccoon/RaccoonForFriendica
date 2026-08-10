package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import coil3.PlatformContext
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
internal actual class NativeUtilsModule {
    @Single
    fun platformContext(): PlatformContext = PlatformContext.INSTANCE
}
