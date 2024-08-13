package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.DefaultFileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.DefaultGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent

actual fun getImageLoaderProvider(): ImageLoaderProvider {
    val inject = KoinJavaComponent.inject<ImageLoaderProvider>(ImageLoaderProvider::class.java)
    val res by inject
    return res
}

actual fun getGalleryHelper(): GalleryHelper {
    val res: GalleryHelper by KoinJavaComponent.inject(GalleryHelper::class.java)
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

actual val coreUtilsGalleryModule =
    module {
        single<GalleryHelper> {
            DefaultGalleryHelper(
                context = get(),
            )
        }
    }
