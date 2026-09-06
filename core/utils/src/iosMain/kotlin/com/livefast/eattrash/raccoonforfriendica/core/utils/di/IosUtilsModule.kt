package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import coil3.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
class IosUtilsModule {

    @Provides
    @SingleIn(AppScope::class)
    fun providePlatformContext(): PlatformContext = PlatformContext.INSTANCE
}
