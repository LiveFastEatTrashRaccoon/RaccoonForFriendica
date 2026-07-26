package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.Clipboard
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
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

@OptIn(DelicateDiApi::class)
@Composable
fun rememberImageLoaderProvider() = remember { getByInjection(ImageLoaderProvider::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberGalleryHelper() = remember { getByInjection(GalleryHelper::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberShareHelper() = remember { getByInjection(ShareHelper::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberAppInfoRepository() = remember { getByInjection(AppInfoRepository::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberBlurHashRepository() = remember { getByInjection(BlurHashRepository::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberCrashReportManager() = remember { getByInjection(CrashReportManager::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberCalendarHelper() = remember { getByInjection(CalendarHelper::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberNetworkStateObserver() = remember { getByInjection(NetworkStateObserver::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberClipboardHelper(clipboard: Clipboard) = remember {
    getByInjection(clazz = ClipboardHelper::class, parameters = { parametersOf(clipboard) })
}
