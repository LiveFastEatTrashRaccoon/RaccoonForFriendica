package com.livefast.eattrash.raccoonforfriendica.core.di.utils

import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.calendar.CalendarHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.FabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler

interface UiDeps {
    // core
    val appInfoRepository: AppInfoRepository
    val barColorProvider: BarColorProvider
    val blurHashRepository: BlurHashRepository
    val calendarHelper: CalendarHelper
    val colorSchemeProvider: ColorSchemeProvider
    val crashReportManager: CrashReportManager
    val drawerCoordinator: DrawerCoordinator
    val fabNestedScrollConnection: FabNestedScrollConnection
    val fileSystemManager: FileSystemManager
    val galleryHelper: GalleryHelper
    val imageLoaderProvider: ImageLoaderProvider
    val mainRouter: MainRouter
    val navigationCoordinator: NavigationCoordinator
    val networkStateObserver: NetworkStateObserver
    val resources: CoreResources
    val shareHelper: ShareHelper
    val strings: Strings
    val themeRepository: ThemeRepository

    // domain
    val entryActionRepository: EntryActionRepository

    fun getCustomUriHandler(uriHandler: UriHandler): CustomUriHandler

    fun getClipboardHelper(clipboard: Clipboard): ClipboardHelper
}
