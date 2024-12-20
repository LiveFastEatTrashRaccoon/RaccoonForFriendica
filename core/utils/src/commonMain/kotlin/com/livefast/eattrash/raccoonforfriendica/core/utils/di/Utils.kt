package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
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

fun getGalleryHelper(): GalleryHelper {
    val res by RootDI.di.instance<GalleryHelper>()
    return res
}

fun getShareHelper(): ShareHelper {
    val res by RootDI.di.instance<ShareHelper>()
    return res
}

fun getAppInfoRepository(): AppInfoRepository {
    val res by RootDI.di.instance<AppInfoRepository>()
    return res
}

fun getBlurHashRepository(): BlurHashRepository {
    val res by RootDI.di.instance<BlurHashRepository>()
    return res
}

fun getCrashReportManager(): CrashReportManager {
    val res by RootDI.di.instance<CrashReportManager>()
    return res
}

fun getCalendarHelper(): CalendarHelper {
    val res by RootDI.di.instance<CalendarHelper>()
    return res
}

fun getNetworkStateObserver(): NetworkStateObserver {
    val res by RootDI.di.instance<NetworkStateObserver>()
    return res
}
