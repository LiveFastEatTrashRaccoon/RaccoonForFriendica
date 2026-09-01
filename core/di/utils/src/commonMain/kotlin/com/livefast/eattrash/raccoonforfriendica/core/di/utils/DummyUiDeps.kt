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

abstract class DummyUiDeps : UiDeps {
    override val appInfoRepository: AppInfoRepository get() = TODO("Stub!")
    override val barColorProvider: BarColorProvider get() = TODO("Stub!")
    override val blurHashRepository: BlurHashRepository get() = TODO("Stub!")
    override val calendarHelper: CalendarHelper get() = TODO("Stub!")
    override val colorSchemeProvider: ColorSchemeProvider get() = TODO("Stub!")
    override val crashReportManager: CrashReportManager get() = TODO("Stub!")
    override val drawerCoordinator: DrawerCoordinator get() = TODO("Stub!")
    override val fabNestedScrollConnection: FabNestedScrollConnection get() = TODO("Stub!")
    override val fileSystemManager: FileSystemManager get() = TODO("Stub!")
    override val galleryHelper: GalleryHelper get() = TODO("Stub!")
    override val imageLoaderProvider: ImageLoaderProvider get() = TODO("Stub!")
    override val mainRouter: MainRouter get() = TODO("Stub!")
    override val navigationCoordinator: NavigationCoordinator get() = TODO("Stub!")
    override val networkStateObserver: NetworkStateObserver get() = TODO("Stub!")
    override val resources: CoreResources get() = TODO("Stub!")
    override val shareHelper: ShareHelper get() = TODO("Stub!")
    override val strings: Strings get() = TODO("Stub!")
    override val themeRepository: ThemeRepository get() = TODO("Stub!")
    override val entryActionRepository: EntryActionRepository get() = TODO("Stub!")

    override fun getCustomUriHandler(uriHandler: UriHandler): CustomUriHandler  = TODO("Stub!")

    override fun getClipboardHelper(clipboard: Clipboard): ClipboardHelper = TODO("Stub!")
}
