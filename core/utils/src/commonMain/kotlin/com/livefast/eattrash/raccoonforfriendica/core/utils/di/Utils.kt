package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import org.koin.core.annotation.Module

@Module
internal expect class AppIconModule()

@Module
internal expect class AppInfoModule()

@Module
internal expect class CalendarModule()

@Module
internal expect class DebugModule()

@Module
internal expect class FileSystemModule()

@Module
internal expect class GalleryModule()

@Module
internal expect class ShareModule()

@Module
internal expect class UrlModule()

@Module
internal expect class VibrateModule()

expect fun getImageLoaderProvider(): ImageLoaderProvider

expect fun getGalleryHelper(): GalleryHelper

expect fun getShareHelper(): ShareHelper

expect fun getAppInfoRepository(): AppInfoRepository

expect fun getBlurHashRepository(): BlurHashRepository

expect fun getCrashReportManager(): CrashReportManager

expect fun getCalendarHelper(): CalendarHelper

expect fun getNetworkStateObserver(): NetworkStateObserver
