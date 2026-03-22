package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.Clipboard
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import org.kodein.di.instance

fun getImageLoaderProvider(): ImageLoaderProvider {
    val res by RootDI.di.instance<ImageLoaderProvider>()
    return res
}

@Composable
fun rememberImageLoaderProvider() = remember { getImageLoaderProvider() }

fun getGalleryHelper(): GalleryHelper {
    val res by RootDI.di.instance<GalleryHelper>()
    return res
}

@Composable
fun rememberGalleryHelper() = remember { getGalleryHelper() }

fun getShareHelper(): ShareHelper {
    val res by RootDI.di.instance<ShareHelper>()
    return res
}

@Composable
fun rememberShareHelper() = remember { getShareHelper() }

fun getAppInfoRepository(): AppInfoRepository {
    val res by RootDI.di.instance<AppInfoRepository>()
    return res
}

@Composable
fun rememberAppInfoRepository() = remember { getAppInfoRepository() }

fun getBlurHashRepository(): BlurHashRepository {
    val res by RootDI.di.instance<BlurHashRepository>()
    return res
}

@Composable
fun rememberBlurHashRepository() = remember { getBlurHashRepository() }

fun getCrashReportManager(): CrashReportManager {
    val res by RootDI.di.instance<CrashReportManager>()
    return res
}

fun getCalendarHelper(): CalendarHelper {
    val res by RootDI.di.instance<CalendarHelper>()
    return res
}

@Composable
fun rememberCalendarHelper() = remember { getCalendarHelper() }

fun getNetworkStateObserver(): NetworkStateObserver {
    val res by RootDI.di.instance<NetworkStateObserver>()
    return res
}

@Composable
fun rememberNetworkStateObserver() = remember { getNetworkStateObserver() }

fun getClipboardHelper(clipboard: Clipboard): ClipboardHelper {
    val res by RootDI.di.instance<Clipboard, ClipboardHelper>(arg = clipboard)
    return res
}

@Composable
fun rememberClipboardHelper(clipboard: Clipboard) = remember { getClipboardHelper(clipboard) }
