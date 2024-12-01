package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import org.koin.core.module.Module

expect fun getImageLoaderProvider(): ImageLoaderProvider

expect fun getGalleryHelper(): GalleryHelper

expect fun getShareHelper(): ShareHelper

expect fun getAppInfoRepository(): AppInfoRepository

expect fun getBlurHashRepository(): BlurHashRepository

expect fun getCrashReportManager(): CrashReportManager

expect fun getCalendarHelper(): CalendarHelper

expect fun getNetworkStateObserver(): NetworkStateObserver

internal expect val nativeFileSystemModule: Module

internal expect val nativeGalleryModule: Module

internal expect val nativeShareModule: Module

internal expect val nativeUrlModule: Module

internal expect val nativeDebugModule: Module

internal expect val nativeHapticFeedbackModule: Module

internal expect val nativeCrashReportModule: Module

internal expect val nativeCalendarModule: Module

internal expect val nativeAppIconModule: Module
