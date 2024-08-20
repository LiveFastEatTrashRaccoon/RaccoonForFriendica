package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.DefaultAppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.DefaultFileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.DefaultGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.DefaultShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.DefaultCustomTabsHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

actual fun getImageLoaderProvider(): ImageLoaderProvider = CoreUtilsDiHelper.imageLoaderProvider

actual fun getGalleryHelper(): GalleryHelper = CoreUtilsDiHelper.galleryHelper

actual fun getShareHelper(): ShareHelper = CoreUtilsDiHelper.shareHelper

actual fun getAppInfoRepository(): AppInfoRepository = CoreUtilsDiHelper.appInfoRepository

actual val coreUtilsFileSystemModule =
    module {
        single<FileSystemManager> {
            DefaultFileSystemManager()
        }
    }

actual val coreUtilsGalleryModule =
    module {
        single<GalleryHelper> {
            DefaultGalleryHelper()
        }
    }

actual val coreUtilsShareModule =
    module {
        single<ShareHelper> {
            DefaultShareHelper()
        }
    }

actual val coreUtilsUrlModule =
    module {
        single<CustomTabsHelper> {
            DefaultCustomTabsHelper()
        }
    }

actual val coreUtilsDebugModule =
    module {
        single<AppInfoRepository> {
            DefaultAppInfoRepository()
        }
    }

internal object CoreUtilsDiHelper : KoinComponent {
    val imageLoaderProvider: ImageLoaderProvider by inject()
    val galleryHelper: GalleryHelper by inject()
    val shareHelper: ShareHelper by inject()
    val appInfoRepository: AppInfoRepository by inject()
}
