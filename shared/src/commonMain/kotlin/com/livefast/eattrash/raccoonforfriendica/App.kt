package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.rememberThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.AppTheme
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Locales
import com.livefast.eattrash.raccoonforfriendica.core.l10n.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.rememberL10nManager
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultNavigationAdapter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerEvent
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.resources.ProvideResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getCrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.rememberNetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EntryListType
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ProvideCustomFontScale
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.rememberSettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.rememberActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.rememberSetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.ProvideCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di.getCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openInternally
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerContent
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.PermanentDrawerContent
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.di.EntryListViewModelArgs
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedViewModel
import com.livefast.eattrash.raccoonforfriendica.navigation.buildNavigationGraph
import com.livefast.eattrash.raccoonforfriendica.navigation.buildNavigationGraphExpanded
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.kodein.di.compose.withDI

@OptIn(FlowPreview::class, ExperimentalComposeUiApi::class)
@Composable
fun App(onLoadingFinished: (() -> Unit)? = null) = withDI(RootDI.di) {
    // initialize crash reporting as soon as possible
    val crashReportManager = remember { getCrashReportManager() }
    LaunchedEffect(crashReportManager) {
        crashReportManager.initialize()
    }

    val navigationCoordinator = rememberNavigationCoordinator()
    val l10nManager = rememberL10nManager()
    val themeRepository = rememberThemeRepository()
    val settingsRepository = rememberSettingsRepository()
    val activeAccountMonitor = rememberActiveAccountMonitor()
    val setupAccountUseCase = rememberSetupAccountUseCase()
    val networkStateObserver = rememberNetworkStateObserver()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerCoordinator = rememberDrawerCoordinator()
    val drawerGesturesEnabled by drawerCoordinator.gesturesEnabled.collectAsState()
    val currentSettings by settingsRepository.current.collectAsState()
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val fallbackUriHandler = LocalUriHandler.current

    LaunchedEffect(settingsRepository) {
        var isInitialized = false

        fun finishInitialization() {
            if (!isInitialized) {
                isInitialized = true
                onLoadingFinished?.invoke()
            }
        }

        launch {
            // set a timeout on the initialization
            delay(1500)
            finishInitialization()
        }

        settingsRepository.current
            .onEach { settings ->
                if (settings != null) {
                    l10nManager.changeLanguage(settings.lang)
                    themeRepository.changeTheme(settings.theme)
                    themeRepository.changeCommentBarTheme(settings.commentBarTheme)
                    themeRepository.changeFontFamily(settings.fontFamily)
                    themeRepository.changeFontScale(settings.fontScale)
                    themeRepository.changeCustomSeedColor(
                        color = settings.customSeedColor?.let { c -> Color(color = c) },
                    )
                    finishInitialization()
                }
            }.launchIn(this)

        activeAccountMonitor.start()
        setupAccountUseCase()
    }

    LaunchedEffect(drawerState.isOpen) {
        // centralizes the information about drawer opening
        drawerCoordinator.changeDrawerOpened(drawerState.isOpen)
    }
    LaunchedEffect(drawerCoordinator) {
        drawerCoordinator.events
            .onEach { evt ->
                when (evt) {
                    DrawerEvent.Toggle -> {
                        drawerState.apply {
                            launch {
                                if (isClosed) {
                                    open()
                                } else {
                                    close()
                                }
                            }
                        }
                    }

                    DrawerEvent.Close -> {
                        drawerState.apply {
                            launch {
                                if (!isClosed) {
                                    close()
                                }
                            }
                        }
                    }
                }
            }.launchIn(this)
    }

    LaunchedEffect(navigationCoordinator) {
        val customUriHandler = getCustomUriHandler(fallbackUriHandler)
        navigationCoordinator.deepLinkUrl
            .debounce(750)
            .onEach { url ->
                customUriHandler.openInternally(url)
            }.launchIn(this)
    }

    LaunchedEffect(navigationCoordinator) {
        val adapter = DefaultNavigationAdapter(navController)
        navigationCoordinator.setRootNavigator(adapter)
    }

    DisposableEffect(networkStateObserver) {
        networkStateObserver.start()
        onDispose {
            networkStateObserver.stop()
        }
    }

    AppTheme(
        useDynamicColors = currentSettings?.dynamicColors == true,
        barTheme = currentSettings?.barTheme ?: UiBarTheme.Transparent,
    ) {
        ProvideResources {
            ProvideCustomUriHandler {
                ProvideStrings(lang = currentSettings?.lang ?: Locales.EN) {
                    if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            gesturesEnabled = drawerGesturesEnabled,
                            drawerContent = {
                                ProvideCustomFontScale {
                                    DrawerContent()
                                }
                            },
                        ) {
                            val canPop by drawerCoordinator.drawerOpened.collectAsState()
                            val navState = rememberNavigationEventState(NavigationEventInfo.None)
                            NavigationBackHandler(
                                state = navState,
                                isBackEnabled = canPop,
                                onBackCompleted = {
                                    scope.launch {
                                        drawerCoordinator.toggleDrawer()
                                    }
                                },
                            )
                            ProvideCustomFontScale {
                                // preload ViewModels for all top-level sections
                                val timelineModel: TimelineMviModel = getViewModel<TimelineViewModel>()
                                val exploreModel: ExploreMviModel = getViewModel<ExploreViewModel>()
                                val inboxModel: InboxMviModel = getViewModel<InboxViewModel>()
                                val profileModel: ProfileMviModel = getViewModel<ProfileViewModel>()
                                val myAccountModel: MyAccountMviModel = getViewModel<MyAccountViewModel>()
                                val timelineLazyListState = rememberLazyListState()
                                val exploreLazyListState = rememberLazyListState()
                                val inboxLazyListState = rememberLazyListState()
                                val myAccountLazyListState = rememberLazyListState()
                                NavHost(
                                    navController = navController,
                                    startDestination = Destination.Main,
                                ) {
                                    buildNavigationGraph(
                                        timelineViewModel = timelineModel,
                                        exploreViewModel = exploreModel,
                                        inboxViewModel = inboxModel,
                                        profileViewModel = profileModel,
                                        myAccountViewModel = myAccountModel,
                                        timelineLazyListState = timelineLazyListState,
                                        exploreLazyListState = exploreLazyListState,
                                        inboxLazyListState = inboxLazyListState,
                                        myAccountLazyListState = myAccountLazyListState,
                                    )
                                }
                            }
                        }
                    } else {
                        ProvideCustomFontScale {
                            Scaffold(
                                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                            ) { paddingValues ->
                                val startDestination: Destination = Destination.Main
                                var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues),
                                ) {
                                    PermanentNavigationDrawer(
                                        drawerContent = {
                                            PermanentDrawerContent(
                                                currentDestination = selectedDestination,
                                                onSelectDestination = { destination ->
                                                    selectedDestination = destination
                                                    navController.navigate(destination)
                                                },
                                            )
                                        },
                                    ) {
                                        // preload ViewModels for all top-level sections
                                        val timelineViewModel: TimelineMviModel = getViewModel<TimelineViewModel>()
                                        val exploreViewModel: ExploreMviModel = getViewModel<ExploreViewModel>()
                                        val inboxViewModel: InboxMviModel = getViewModel<InboxViewModel>()
                                        val profileViewModel: ProfileMviModel = getViewModel<ProfileViewModel>()
                                        val myAccountViewModel: MyAccountMviModel = getViewModel<MyAccountViewModel>()
                                        val favoritesViewModel: EntryListMviModel =
                                            getViewModel<EntryListViewModel>(arg = EntryListViewModelArgs(type = EntryListType.Favorites))
                                        val bookmarksViewModel: EntryListMviModel =
                                            getViewModel<EntryListViewModel>(arg = EntryListViewModelArgs(type = EntryListType.Bookmarks))
                                        val followedHashtagsViewModel: FollowedHashtagsMviModel =
                                            getViewModel<FollowedHashtagsViewModel>()
                                        val followRequestsViewModel: FollowRequestsMviModel =
                                            getViewModel<FollowRequestsViewModel>()
                                        val circlesViewModel: CirclesMviModel = getViewModel<CirclesViewModel>()
                                        val conversationListViewModel: ConversationListMviModel =
                                            getViewModel<ConversationListViewModel>()
                                        val galleryViewModel: GalleryMviModel = getViewModel<GalleryViewModel>()
                                        val unpublishedViewModel: UnpublishedMviModel =
                                            getViewModel<UnpublishedViewModel>()
                                        val calendarViewModel: CalendarMviModel = getViewModel<CalendarViewModel>()
                                        val shortcutListViewModel: ShortcutListMviModel =
                                            getViewModel<ShortcutListViewModel>()
                                        val nodeInfoViewModel: NodeInfoMviModel = getViewModel<NodeInfoViewModel>()
                                        val timelineLazyListState = rememberLazyListState()
                                        val exploreLazyListState = rememberLazyListState()
                                        val inboxLazyListState = rememberLazyListState()
                                        val myAccountLazyListState = rememberLazyListState()
                                        NavHost(
                                            navController = navController,
                                            startDestination = startDestination,
                                        ) {
                                            buildNavigationGraphExpanded(
                                                timelineViewModel = timelineViewModel,
                                                timelineLazyListState = timelineLazyListState,
                                                exploreViewModel = exploreViewModel,
                                                exploreLazyListState = exploreLazyListState,
                                                inboxViewModel = inboxViewModel,
                                                inboxLazyListState = inboxLazyListState,
                                                profileViewModel = profileViewModel,
                                                myAccountViewModel = myAccountViewModel,
                                                myAccountLazyListState = myAccountLazyListState,
                                                favoritesViewModel = favoritesViewModel,
                                                bookmarksViewModel = bookmarksViewModel,
                                                followedHashtagsViewModel = followedHashtagsViewModel,
                                                followRequestsViewModel = followRequestsViewModel,
                                                circlesViewModel = circlesViewModel,
                                                conversationListViewModel = conversationListViewModel,
                                                galleryViewModel = galleryViewModel,
                                                unpublishedViewModel = unpublishedViewModel,
                                                calendarViewModel = calendarViewModel,
                                                shortcutListViewModel = shortcutListViewModel,
                                                nodeInfoViewModel = nodeInfoViewModel,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

