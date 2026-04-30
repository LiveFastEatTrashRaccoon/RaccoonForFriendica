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
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.singleton

val utilsModule =
    DI.Module("UtilsModule") {
        importAll(
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

        bindSingleton<BlurHashDecoder> {
            DefaultBlurHashDecoder()
        }
        bindSingleton<BlurHashRepository> {
            DefaultBlurHashRepository(
                decoder = instance(),
            )
        }
        bindSingleton<ImagePreloadManager> {
            DefaultImagePreloadManager(
                context = instance(),
                imageLoaderProvider = instance(),
            )
        }
        bindSingleton<ImageLoaderProvider> {
            DefaultImageLoaderProvider(
                context = instance(),
                fileSystemManager = instance(),
            )
        }
        bindSingleton<NetworkStateObserver> {
            val connectivityProvider = instance<ConnectivityProvider>()
            DefaultNetworkStateObserver(connectivityProvider.provide())
        }
    }
