package com.github.akesiseli.raccoonforfriendica.core.utils.di

import com.github.akesiseli.raccoonforfriendica.core.utils.imageload.DefaultImageLoaderProvider
import com.github.akesiseli.raccoonforfriendica.core.utils.imageload.DefaultImagePreloadManager
import com.github.akesiseli.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.github.akesiseli.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.github.akesiseli.raccoonforfriendica.core.utils.url.DefaultUrlManager
import com.github.akesiseli.raccoonforfriendica.core.utils.url.UrlManager
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
