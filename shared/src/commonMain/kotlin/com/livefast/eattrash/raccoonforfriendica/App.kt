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
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.ProvideUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.UiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Locales
import com.livefast.eattrash.raccoonforfriendica.core.l10n.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultNavigationAdapter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerEvent
import com.livefast.eattrash.raccoonforfriendica.core.resources.ProvideResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EntryListType
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ProvideCustomFontScale
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openInternally
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerContent
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.PermanentDrawerContent
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.di.EntryListViewModelArgs
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreViewModel
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
import com.livefast.eattrash.raccoonforfriendica.main.RootMviModel
import com.livefast.eattrash.raccoonforfriendica.main.RootViewModel
import com.livefast.eattrash.raccoonforfriendica.navigation.getEntryProvider
import com.livefast.eattrash.raccoonforfriendica.navigation.isDetailDestination
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun App(onLoadingFinished: (() -> Unit)? = null) {
    val model: RootMviModel = koinViewModel<RootViewModel>()
    val uiState by model.uiState.collectAsState()
    val uiDeps: UiDeps = koinInject()
    val barColorProvider = uiDeps.barColorProvider
    val colorSchemeProvider = uiDeps.colorSchemeProvider
    val customUriHandler = uiDeps.getCustomUriHandler(LocalUriHandler.current)
    val drawerCoordinator = uiDeps.drawerCoordinator
    val navigationCoordinator = uiDeps.navigationCoordinator
    val networkStateObserver = uiDeps.networkStateObserver
    val themeRepository = uiDeps.themeRepository

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

    LaunchedEffect(model) {
        model.effects.onEach { effect ->
            when (effect) {
                RootMviModel.Effect.InitializationFinished -> onLoadingFinished?.invoke()
            }
        }.launchIn(this)
    }

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

    ProvideUiDeps(uiDeps) {
        ProvideResources(resources = uiDeps.resources) {
            CompositionLocalProvider(LocalUriHandler provides customUriHandler) {
                ProvideStrings(lang = uiState.currentSettings?.lang ?: Locales.EN, strings = uiDeps.strings) {
                    AppTheme(
                        repository = themeRepository,
                        barColorProvider = barColorProvider,
                        colorSchemeProvider = colorSchemeProvider,
                        useDynamicColors = uiState.currentSettings?.dynamicColors == true,
                        barTheme = uiState.currentSettings?.barTheme ?: UiBarTheme.Transparent,
                    ) {
                        if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
                            ModalNavigationDrawer(
                                drawerState = drawerState,
                                gesturesEnabled = drawerGesturesEnabled,
                                drawerContent = {
                                    ProvideCustomFontScale(currentSettings = uiState.currentSettings) {
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
                                ProvideCustomFontScale(currentSettings = uiState.currentSettings) {
                                    // preload ViewModels for all top-level sections
                                    val timelineModel: TimelineMviModel = koinViewModel<TimelineViewModel>()
                                    val exploreModel: ExploreMviModel = koinViewModel<ExploreViewModel>()
                                    val inboxModel: InboxMviModel = koinViewModel<InboxViewModel>()
                                    val profileModel: ProfileMviModel = koinViewModel<ProfileViewModel>()
                                    val myAccountModel: MyAccountMviModel = koinViewModel<MyAccountViewModel>()
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
                            ProvideCustomFontScale(currentSettings = uiState.currentSettings) {
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
                                                val timelineViewModel: TimelineMviModel =
                                                    koinViewModel<TimelineViewModel>()
                                                val exploreViewModel: ExploreMviModel =
                                                    koinViewModel<ExploreViewModel>()
                                                val inboxViewModel: InboxMviModel = koinViewModel<InboxViewModel>()
                                                val profileViewModel: ProfileMviModel =
                                                    koinViewModel<ProfileViewModel>()
                                                val myAccountViewModel: MyAccountMviModel =
                                                    koinViewModel<MyAccountViewModel>()
                                                val favoritesViewModel: EntryListMviModel =
                                                    koinViewModel<EntryListViewModel> {
                                                        parametersOf(EntryListViewModelArgs(type = EntryListType.Favorites))
                                                    }
                                                val bookmarksViewModel: EntryListMviModel =
                                                    koinViewModel<EntryListViewModel> {
                                                        parametersOf(EntryListViewModelArgs(type = EntryListType.Bookmarks))
                                                    }
                                                val followedHashtagsViewModel: FollowedHashtagsMviModel =
                                                    koinViewModel<FollowedHashtagsViewModel>()
                                                val followRequestsViewModel: FollowRequestsMviModel =
                                                    koinViewModel<FollowRequestsViewModel>()
                                                val circlesViewModel: CirclesMviModel =
                                                    koinViewModel<CirclesViewModel>()
                                                val conversationListViewModel: ConversationListMviModel =
                                                    koinViewModel<ConversationListViewModel>()
                                                val galleryViewModel: GalleryMviModel =
                                                    koinViewModel<GalleryViewModel>()
                                                val unpublishedViewModel: UnpublishedMviModel =
                                                    koinViewModel<UnpublishedViewModel>()
                                                val calendarViewModel: CalendarMviModel =
                                                    koinViewModel<CalendarViewModel>()
                                                val shortcutListViewModel: ShortcutListMviModel =
                                                    koinViewModel<ShortcutListViewModel>()
                                                val nodeInfoViewModel: NodeInfoMviModel =
                                                    koinViewModel<NodeInfoViewModel>()
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
    }
}
