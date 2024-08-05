package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.DefaultFileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.UrlManager
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent

actual fun getImageLoaderProvider(): ImageLoaderProvider {
    val inject = KoinJavaComponent.inject<ImageLoaderProvider>(ImageLoaderProvider::class.java)
    val res by inject
    return res
}

actual fun getUrlManager(default: UriHandler): UrlManager {
    val inject =
        KoinJavaComponent.inject<UrlManager>(
            UrlManager::class.java,
            parameters = {
                parametersOf(default)
            },
        )
    val res by inject
    return res
}

actual val coreUtilsFileSystemModule =
    module {
        single<FileSystemManager> {
            DefaultFileSystemManager(
                context = get(),
            )
        }
    }
