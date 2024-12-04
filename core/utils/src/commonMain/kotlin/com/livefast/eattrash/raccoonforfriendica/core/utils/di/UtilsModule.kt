package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.imageload")
internal class ImageLoadModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.network")
internal class NetworkStateModule

val coreUtilsModule =
    module {
        includes(
            AppInfoModule().module,
            AppIconModule().module,
            CalendarModule().module,
            DebugModule().module,
            FileSystemModule().module,
            GalleryModule().module,
            ShareModule().module,
            UrlModule().module,
            VibrateModule().module,
            ImageLoadModule().module,
            NetworkStateModule().module,
        )
    }
