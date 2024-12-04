package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.imageload")
internal class ImageLoadModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.network")
internal class NetworkStateModule

@Module(
    includes = [
        AppInfoModule::class,
        AppIconModule::class,
        CalendarModule::class,
        DebugModule::class,
        FileSystemModule::class,
        GalleryModule::class,
        ShareModule::class,
        UrlModule::class,
        VibrateModule::class,
        ImageLoadModule::class,
        NetworkStateModule::class,
    ],
)
internal class UtilsModule

val coreUtilsModule = UtilsModule().module
