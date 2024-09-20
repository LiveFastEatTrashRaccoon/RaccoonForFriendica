package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashDecoder
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultBlurHashDecoder
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultBlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
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
        single<BlurHashDecoder> {
            DefaultBlurHashDecoder()
        }
        single<BlurHashRepository> {
            DefaultBlurHashRepository(
                decoder = get(),
            )
        }
    }
