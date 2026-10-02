package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.AppTheme
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.ProvideAppCompositionLocals
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.UiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Locales
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultNavigationAdapter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EntryListType
import com.livefast.eattrash.raccoonforfriendica.core.appearance.ProvideCustomFontScale
import com.livefast.eattrash.raccoonforfriendica.di.RootGraph
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openInternally
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarMvi
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMvi
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListMvi
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerContent
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.PermanentDrawerContent
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListMvi
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListViewModelArgs
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMvi
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsMvi
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryMvi
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsMvi
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMvi
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoMvi
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMvi
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMvi
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListMvi
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMvi
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedMvi
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedViewModel
import com.livefast.eattrash.raccoonforfriendica.main.RootMvi
import com.livefast.eattrash.raccoonforfriendica.main.RootViewModel
import com.livefast.eattrash.raccoonforfriendica.navigation.getEntryProvider
import com.livefast.eattrash.raccoonforfriendica.navigation.isDetailDestination
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun App(graph: RootGraph, onLoadingFinished: (() -> Unit)? = null) {
    val uiDeps: UiDeps = graph.uiDeps
    val customUriHandler = uiDeps.getCustomUriHandler(LocalUriHandler.current)
    val drawerCoordinator = uiDeps.drawerCoordinator
    val navigationCoordinator = uiDeps.navigationCoordinator
    val networkStateObserver = uiDeps.networkStateObserver
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerGesturesEnabled by drawerCoordinator.gesturesEnabled.collectAsState()
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(
        configuration = Destination.SavedStateConfiguration,
        Destination.Main,
    )
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val containsDetail = backStack.any { it.isDetailDestination }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        directive = calculatePaneScaffoldDirective(adaptiveInfo).let {
            if (containsDetail) it else it.copy(maxHorizontalPartitions = 1)
        },
        shouldHandleSinglePaneLayout = true,
    )

    LaunchedEffect(drawerCoordinator) {
        snapshotFlow { drawerState.isOpen }.onEach { isDrawerOpen ->
            // centralizes the information about drawer opening
            drawerCoordinator.changeDrawerOpened(isDrawerOpen)
        }.launchIn(this)

        drawerCoordinator.events.onEach { evt ->
            when (evt) {
                DrawerEvent.Toggle -> {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }

                DrawerEvent.Close -> {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    }
                }
            }
        }.launchIn(this)
    }

    LaunchedEffect(navigationCoordinator) {
        val adapter = DefaultNavigationAdapter(backStack)
        navigationCoordinator.setRootNavigator(adapter)

        navigationCoordinator.deepLinkUrl
            .debounce(750.milliseconds)
            .onEach { url ->
                customUriHandler.openInternally(url)
            }.launchIn(this)
    }

    DisposableEffect(networkStateObserver) {
        networkStateObserver.start()
        onDispose {
            networkStateObserver.stop()
        }
    }

    CompositionLocalProvider(
        LocalMetroViewModelFactory provides graph.metroViewModelFactory
    ) {
        val model: RootMvi = metroViewModel<RootViewModel>()
        val uiState by model.uiState.collectAsState()

        LaunchedEffect(model) {
            model.effects.onEach { effect ->
                when (effect) {
                    RootMvi.Effect.InitializationFinished -> onLoadingFinished?.invoke()
                }
            }.launchIn(this)
        }

        ProvideAppCompositionLocals(
            uiDeps = uiDeps,
            lang = uiState.currentSettings?.lang ?: Locales.EN,
            uriHandler = customUriHandler,
        ) {
            AppTheme(
                repository = uiDeps.themeRepository,
                barColorProvider = uiDeps.barColorProvider,
                colorSchemeProvider = uiDeps.colorSchemeProvider,
                useDynamicColors = uiState.currentSettings?.dynamicColors == true,
                barTheme = uiState.currentSettings?.barTheme ?: UiBarTheme.Transparent,
            ) {
                if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = drawerGesturesEnabled,
                        drawerContent = {
                            ProvideCustomFontScale(fontScale = uiState.currentSettings?.fontScale) {
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
                        ProvideCustomFontScale(fontScale = uiState.currentSettings?.fontScale) {
                            // preload ViewModels for all top-level sections
                            val timelineModel: TimelineMvi = metroViewModel<TimelineViewModel>()
                            val exploreModel: ExploreMvi = metroViewModel<ExploreViewModel>()
                            val inboxModel: InboxMvi = metroViewModel<InboxViewModel>()
                            val profileModel: ProfileMvi = metroViewModel<ProfileViewModel>()
                            val myAccountModel: MyAccountMvi = metroViewModel<MyAccountViewModel>()
                            val timelineLazyListState = rememberLazyListState()
                            val exploreLazyListState = rememberLazyListState()
                            val inboxLazyListState = rememberLazyListState()
                            val myAccountLazyListState = rememberLazyListState()
                            Surface(color = MaterialTheme.colorScheme.background) {
                                NavDisplay(
                                    backStack = backStack,
                                    onBack = { navigationCoordinator.pop() },
                                    entryDecorators = listOf(
                                        rememberSaveableStateHolderNavEntryDecorator(),
                                        rememberViewModelStoreNavEntryDecorator(),
                                    ),
                                    sceneStrategies = listOf(listDetailStrategy),
                                    entryProvider = getEntryProvider(
                                        timelineViewModel = timelineModel,
                                        timelineLazyListState = timelineLazyListState,
                                        exploreViewModel = exploreModel,
                                        exploreLazyListState = exploreLazyListState,
                                        inboxViewModel = inboxModel,
                                        inboxLazyListState = inboxLazyListState,
                                        profileViewModel = profileModel,
                                        myAccountViewModel = myAccountModel,
                                        myAccountLazyListState = myAccountLazyListState,
                                    ),
                                )
                            }
                        }
                    }
                } else {
                    ProvideCustomFontScale(fontScale = uiState.currentSettings?.fontScale) {
                        Scaffold(
                            content = { paddingValues ->
                                var selectedDestination by rememberSaveable(stateSaver = Destination.Saver) {
                                    mutableStateOf(Destination.Main)
                                }
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
                                                    backStack[backStack.lastIndex] = destination
                                                },
                                            )
                                        },
                                    ) {
                                        // preload ViewModels for all top-level sections
                                        val timelineViewModel: TimelineMvi = metroViewModel<TimelineViewModel>()
                                        val exploreViewModel: ExploreMvi = metroViewModel<ExploreViewModel>()
                                        val inboxViewModel: InboxMvi = metroViewModel<InboxViewModel>()
                                        val profileViewModel: ProfileMvi = metroViewModel<ProfileViewModel>()
                                        val myAccountViewModel: MyAccountMvi = metroViewModel<MyAccountViewModel>()
                                        val favoritesViewModel: EntryListMvi =
                                            assistedMetroViewModel<EntryListViewModel>(
                                                extras = EntryListViewModel.getExtras(
                                                    EntryListViewModelArgs(
                                                        type = EntryListType.Favorites,
                                                    )
                                                ),
                                            )
                                        val bookmarksViewModel: EntryListMvi =
                                            assistedMetroViewModel<EntryListViewModel>(
                                                extras = EntryListViewModel.getExtras(
                                                    EntryListViewModelArgs(type = EntryListType.Bookmarks)
                                                ),
                                            )
                                        val followedHashtagsViewModel: FollowedHashtagsMvi =
                                            metroViewModel<FollowedHashtagsViewModel>()
                                        val followRequestsViewModel: FollowRequestsMvi =
                                            metroViewModel<FollowRequestsViewModel>()
                                        val circlesViewModel: CirclesMvi = metroViewModel<CirclesViewModel>()
                                        val conversationListViewModel: ConversationListMvi =
                                            metroViewModel<ConversationListViewModel>()
                                        val galleryViewModel: GalleryMvi = metroViewModel<GalleryViewModel>()
                                        val unpublishedViewModel: UnpublishedMvi =
                                            metroViewModel<UnpublishedViewModel>()
                                        val calendarViewModel: CalendarMvi = metroViewModel<CalendarViewModel>()
                                        val shortcutListViewModel: ShortcutListMvi =
                                            metroViewModel<ShortcutListViewModel>()
                                        val nodeInfoViewModel: NodeInfoMvi = metroViewModel<NodeInfoViewModel>()
                                        val timelineLazyListState = rememberLazyListState()
                                        val exploreLazyListState = rememberLazyListState()
                                        val inboxLazyListState = rememberLazyListState()
                                        val myAccountLazyListState = rememberLazyListState()
                                        Surface(color = MaterialTheme.colorScheme.background) {
                                            NavDisplay(
                                                backStack = backStack,
                                                onBack = { navigationCoordinator.pop() },
                                                entryDecorators = listOf(
                                                    rememberSaveableStateHolderNavEntryDecorator(),
                                                    rememberViewModelStoreNavEntryDecorator(),
                                                ),
                                                sceneStrategies = listOf(listDetailStrategy),
                                                entryProvider = getEntryProvider(
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
                                                ),
                                            )
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
