package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.Clipboard
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import org.koin.core.parameter.parametersOf

fun getImageLoaderProvider(): ImageLoaderProvider = getByInjection(ImageLoaderProvider::class)

@Composable
fun rememberImageLoaderProvider() = remember { getImageLoaderProvider() }

fun getGalleryHelper(): GalleryHelper = getByInjection(GalleryHelper::class)

@Composable
fun rememberGalleryHelper() = remember { getGalleryHelper() }

fun getShareHelper(): ShareHelper = getByInjection(ShareHelper::class)

@Composable
fun rememberShareHelper() = remember { getShareHelper() }

fun getAppInfoRepository(): AppInfoRepository = getByInjection(AppInfoRepository::class)

@Composable
fun rememberAppInfoRepository() = remember { getAppInfoRepository() }

fun getBlurHashRepository(): BlurHashRepository = getByInjection(BlurHashRepository::class)

@Composable
fun rememberBlurHashRepository() = remember { getBlurHashRepository() }

fun getCrashReportManager(): CrashReportManager = getByInjection(CrashReportManager::class)

fun getCalendarHelper(): CalendarHelper = getByInjection(CalendarHelper::class)

@Composable
fun rememberCalendarHelper() = remember { getCalendarHelper() }

fun getNetworkStateObserver(): NetworkStateObserver = getByInjection(NetworkStateObserver::class)

@Composable
fun rememberNetworkStateObserver() = remember { getNetworkStateObserver() }

fun getClipboardHelper(clipboard: Clipboard): ClipboardHelper =
    getByInjection(clazz = ClipboardHelper::class, parameters = { parametersOf(clipboard) })

@Composable
fun rememberClipboardHelper(clipboard: Clipboard) = remember { getClipboardHelper(clipboard) }
