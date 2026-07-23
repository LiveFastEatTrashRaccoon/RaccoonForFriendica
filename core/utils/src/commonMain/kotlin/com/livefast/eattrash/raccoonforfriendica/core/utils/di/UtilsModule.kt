package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashDecoder
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultBlurHashDecoder
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultBlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.DefaultImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.ConnectivityProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.DefaultNetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import org.koin.dsl.module

val utilsModule = module {
    includes(
        nativeAppIconModule,
        nativeAppInfoModule,
        nativeCalendarModule,
        nativeClipboardModule,
        nativeConnectivityModule,
        nativeDebugModule,
        nativeFileSystemModule,
        nativeGalleryModule,
        nativeImageLoadModule,
        nativeShareModule,
        nativeOpenUrlModule,
        nativeVibrateModule,
    )

    single<BlurHashDecoder> {
        DefaultBlurHashDecoder()
    }
    single<BlurHashRepository> {
        DefaultBlurHashRepository(
            decoder = get(),
        )
    }
    single<ImagePreloadManager> {
        DefaultImagePreloadManager(
            context = get(),
            imageLoaderProvider = get(),
        )
    }
    single<ImageLoaderProvider> {
        DefaultImageLoaderProvider(
            context = get(),
            fileSystemManager = get(),
        )
    }
    single<NetworkStateObserver> {
        val connectivityProvider = get<ConnectivityProvider>()
        DefaultNetworkStateObserver(connectivityProvider.provide())
    }
}
