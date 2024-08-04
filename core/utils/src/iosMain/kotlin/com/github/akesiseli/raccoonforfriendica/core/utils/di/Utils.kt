package com.github.akesiseli.raccoonforfriendica.core.utils.di

import androidx.compose.ui.platform.UriHandler
import com.github.akesiseli.raccoonforfriendica.core.utils.fs.DefaultFileSystemManager
import com.github.akesiseli.raccoonforfriendica.core.utils.fs.FileSystemManager
import com.github.akesiseli.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.github.akesiseli.raccoonforfriendica.core.utils.url.UrlManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

actual fun getImageLoaderProvider(): ImageLoaderProvider = ImageLoaderDiHelper.imageLoaderProvider

actual fun getUrlManager(default: UriHandler): UrlManager = ImageLoaderDiHelper.getUrlManager(default)

actual val coreUtilsFileSystemModule =
    module {
        single<FileSystemManager> {
            DefaultFileSystemManager()
        }
    }

internal object ImageLoaderDiHelper : KoinComponent {
    val imageLoaderProvider: ImageLoaderProvider by inject()

    fun getUrlManager(default: UriHandler): UrlManager {
        val res by inject<UrlManager>(
            parameters = {
                parametersOf(default)
            },
        )
        return res
    }
}
