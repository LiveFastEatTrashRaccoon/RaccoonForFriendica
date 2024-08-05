package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.DefaultUrlManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.UrlManager
import org.koin.dsl.module

val coreUtilsModule =
    module {
        single<ImageLoaderProvider> {
            DefaultImageLoaderProvider(
                context = get(),
                fileSystemManager = get(),
            )
        }

        single<ImagePreloadManager> {
            DefaultImagePreloadManager(
                context = get(),
                imageLoaderProvider = get(),
            )
        }
        single<UrlManager> { params ->
            DefaultUrlManager(
                defaultHandler = params[0],
            )
        }
    }
