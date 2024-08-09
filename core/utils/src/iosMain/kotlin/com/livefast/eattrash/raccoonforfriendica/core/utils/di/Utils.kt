package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.DefaultFileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

actual fun getImageLoaderProvider(): ImageLoaderProvider = CoreUtilsDiHelper.imageLoaderProvider

actual val coreUtilsFileSystemModule =
    module {
        single<FileSystemManager> {
            DefaultFileSystemManager()
        }
    }

internal object CoreUtilsDiHelper : KoinComponent {
    val imageLoaderProvider: ImageLoaderProvider by inject()
}
