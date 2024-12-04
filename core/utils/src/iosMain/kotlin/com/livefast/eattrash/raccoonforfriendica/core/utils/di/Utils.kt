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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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

actual fun getImageLoaderProvider(): ImageLoaderProvider = CoreUtilsDiHelper.imageLoaderProvider

actual fun getGalleryHelper(): GalleryHelper = CoreUtilsDiHelper.galleryHelper

actual fun getShareHelper(): ShareHelper = CoreUtilsDiHelper.shareHelper

actual fun getAppInfoRepository(): AppInfoRepository = CoreUtilsDiHelper.appInfoRepository

actual fun getBlurHashRepository(): BlurHashRepository = CoreUtilsDiHelper.blurHashRepository

actual fun getCrashReportManager(): CrashReportManager = CoreUtilsDiHelper.crashReportManager

actual fun getCalendarHelper(): CalendarHelper = CoreUtilsDiHelper.calendarHelper

actual fun getNetworkStateObserver(): NetworkStateObserver = CoreUtilsDiHelper.networkStateObserver

internal object CoreUtilsDiHelper : KoinComponent {
    val imageLoaderProvider: ImageLoaderProvider by inject()
    val galleryHelper: GalleryHelper by inject()
    val shareHelper: ShareHelper by inject()
    val appInfoRepository: AppInfoRepository by inject()
    val blurHashRepository: BlurHashRepository by inject()
    val crashReportManager by inject<CrashReportManager>()
    val calendarHelper by inject<CalendarHelper>()
    val networkStateObserver by inject<NetworkStateObserver>()
}
