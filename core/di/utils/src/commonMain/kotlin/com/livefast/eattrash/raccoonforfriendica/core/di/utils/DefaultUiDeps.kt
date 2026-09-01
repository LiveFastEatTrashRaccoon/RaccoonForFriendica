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
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelperFactory
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
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandlerFactory
import org.koin.core.annotation.Single

@Single
internal class DefaultUiDeps(
    override val appInfoRepository: AppInfoRepository,
    override val barColorProvider: BarColorProvider,
    override val blurHashRepository: BlurHashRepository,
    override val calendarHelper: CalendarHelper,
    override val colorSchemeProvider: ColorSchemeProvider,
    override val crashReportManager: CrashReportManager,
    override val drawerCoordinator: DrawerCoordinator,
    override val fabNestedScrollConnection: FabNestedScrollConnection,
    override val fileSystemManager: FileSystemManager,
    override val galleryHelper: GalleryHelper,
    override val imageLoaderProvider: ImageLoaderProvider,
    override val mainRouter: MainRouter,
    override val navigationCoordinator: NavigationCoordinator,
    override val networkStateObserver: NetworkStateObserver,
    override val resources: CoreResources,
    override val shareHelper: ShareHelper,
    override val strings: Strings,
    override val themeRepository: ThemeRepository,
    override val entryActionRepository: EntryActionRepository,
    private val customUriHandlerFactory: CustomUriHandlerFactory,
    private val clipboardHelperFactory: ClipboardHelperFactory,
) : UiDeps {

    override fun getCustomUriHandler(uriHandler: UriHandler): CustomUriHandler =
        customUriHandlerFactory.create(uriHandler)

    override fun getClipboardHelper(clipboard: Clipboard): ClipboardHelper =
        clipboardHelperFactory.create(clipboard)
}
