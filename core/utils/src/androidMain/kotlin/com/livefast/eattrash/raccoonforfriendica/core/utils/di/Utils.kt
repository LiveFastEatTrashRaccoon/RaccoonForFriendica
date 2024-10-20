package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.DefaultCalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.DefaultAppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.DefaultCrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.DefaultFileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.DefaultGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.DefaultShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.DefaultCustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.DefaultHapticFeedback
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
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

actual fun getShareHelper(): ShareHelper {
    val res: ShareHelper by KoinJavaComponent.inject(ShareHelper::class.java)
    return res
}

actual fun getAppInfoRepository(): AppInfoRepository {
    val res: AppInfoRepository by KoinJavaComponent.inject(AppInfoRepository::class.java)
    return res
}

actual fun getBlurHashRepository(): BlurHashRepository {
    val res: BlurHashRepository by KoinJavaComponent.inject(BlurHashRepository::class.java)
    return res
}

actual fun getCrashReportManager(): CrashReportManager {
    val res by KoinJavaComponent.inject<CrashReportManager>(CrashReportManager::class.java)
    return res
}

actual fun getCalendarHelper(): CalendarHelper {
    val res by KoinJavaComponent.inject<CalendarHelper>(CalendarHelper::class.java)
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

actual val coreUtilsShareModule =
    module {
        single<ShareHelper> {
            DefaultShareHelper(
                context = get(),
            )
        }
    }

actual val coreUtilsUrlModule =
    module {
        single<CustomTabsHelper> {
            DefaultCustomTabsHelper(
                context = get(),
            )
        }
    }

actual val coreUtilsDebugModule =
    module {
        single<AppInfoRepository> {
            DefaultAppInfoRepository(
                context = get(),
            )
        }
    }

actual val coreHapticFeedbackModule =
    module {
        single<HapticFeedback> {
            DefaultHapticFeedback(
                context = get(),
            )
        }
    }

actual val coreCrashReportModule =
    module {
        single<CrashReportManager> {
            DefaultCrashReportManager(
                context = get(),
            )
        }
    }

actual val coreUtilsCalendarModule =
    module {
        single<CalendarHelper> {
            DefaultCalendarHelper(
                context = get(),
            )
        }
    }
