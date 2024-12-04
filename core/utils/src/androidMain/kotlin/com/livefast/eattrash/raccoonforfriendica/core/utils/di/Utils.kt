package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.java.KoinJavaComponent

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.appicon")
internal actual class AppIconModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo")
internal actual class AppInfoModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.calendar")
internal actual class CalendarModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.debug")
internal actual class DebugModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.fs")
internal actual class FileSystemModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.gallery")
internal actual class GalleryModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.share")
internal actual class ShareModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.url")
internal actual class UrlModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate")
internal actual class VibrateModule

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
