package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.AppIconManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.DefaultAppIconManager
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
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
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

actual fun getNetworkStateObserver(): NetworkStateObserver {
    val res by KoinJavaComponent.inject<NetworkStateObserver>(NetworkStateObserver::class.java)
    return res
}

internal actual val nativeFileSystemModule =
    module {
        single<FileSystemManager> {
            DefaultFileSystemManager(
                context = get(),
            )
        }
    }

internal actual val nativeGalleryModule =
    module {
        single<GalleryHelper> {
            DefaultGalleryHelper(
                context = get(),
            )
        }
    }

internal actual val nativeShareModule =
    module {
        single<ShareHelper> {
            DefaultShareHelper(
                context = get(),
            )
        }
    }

internal actual val nativeUrlModule =
    module {
        single<CustomTabsHelper> {
            DefaultCustomTabsHelper(
                context = get(),
            )
        }
    }

internal actual val nativeDebugModule =
    module {
        single<AppInfoRepository> {
            DefaultAppInfoRepository(
                context = get(),
            )
        }
    }

internal actual val nativeHapticFeedbackModule =
    module {
        single<HapticFeedback> {
            DefaultHapticFeedback(
                context = get(),
            )
        }
    }

internal actual val nativeCrashReportModule =
    module {
        single<CrashReportManager> {
            DefaultCrashReportManager(
                context = get(),
                keyStore = get(),
            )
        }
    }

internal actual val nativeCalendarModule =
    module {
        single<CalendarHelper> {
            DefaultCalendarHelper(
                context = get(),
            )
        }
    }

internal actual val nativeAppIconModule =
    module {
        single<AppIconManager> {
            DefaultAppIconManager(
                context = get(),
                keyStore = get(),
            )
        }
    }
