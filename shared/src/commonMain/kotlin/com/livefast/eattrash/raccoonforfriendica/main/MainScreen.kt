package com.livefast.eattrash.raccoonforfriendica.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.livefast.eattrash.raccoonforfriendica.bottomnavigation.BottomNavigationItem
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultBottomNavigationAdapter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreScreen
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineScreen
import com.livefast.eattrash.raccoonforfriendica.navigation.bottomGetEntryProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    timelineViewModel: TimelineMviModel,
    exploreViewModel: ExploreMviModel,
    inboxViewModel: InboxMviModel,
    profileViewModel: ProfileMviModel,
    myAccountViewModel: MyAccountMviModel,
    timelineLazyListState: LazyListState,
    exploreLazyListState: LazyListState,
    inboxLazyListState: LazyListState,
    myAccountLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    lockedSection: BottomNavigationSection = BottomNavigationSection.Home,
) {
    val model: MainMviModel = getViewModel<MainViewModel>()
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = rememberNavigationCoordinator()
    val currentSection by navigationCoordinator.currentBottomNavSection.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var bottomBarHeightPx by remember { mutableFloatStateOf(0f) }
    val bottomNavigationInsetPx = WindowInsets.navigationBars.getBottom(LocalDensity.current)
    val scrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    val delta = available.y
                    val newOffset =
                        (uiState.bottomBarOffsetHeightPx + delta).coerceIn(
                            // 2 times:
                            // - once for the actual offset due to the translation amount
                            // - once for the bottom inset artificially applied to NavigationBar
                            -(bottomBarHeightPx + bottomNavigationInsetPx) * 2,
                            0f,
                        )
                    model.reduce(MainMviModel.Intent.SetBottomBarOffsetHeightPx(newOffset))
                    return Offset.Zero
                }
            }
        }
    navigationCoordinator.setBottomBarScrollConnection(scrollConnection)
    val exitMessage = LocalStrings.current.messageConfirmExit
    val backStack = rememberNavBackStack(
        configuration = BottomNavigationSection.SavedStateConfiguration,
        BottomNavigationSection.Home
    )

    val hasBottomNavigation = isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)
    LaunchedEffect(navigationCoordinator, hasBottomNavigation) {
        navigationCoordinator.exitMessageVisible
            .onEach { visible ->
                if (visible) {
                    snackbarHostState.showSnackbar(message = exitMessage)
                    navigationCoordinator.setExitMessageVisible(false)
                }
            }.launchIn(this)
        navigationCoordinator.globalMessage
            .onEach { message ->
                snackbarHostState.showSnackbar(message = message)
            }.launchIn(this)

        if (hasBottomNavigation) {
            val adapter = DefaultBottomNavigationAdapter(backStack)
            navigationCoordinator.setBottomNavigator(adapter)

            navigationCoordinator.currentBottomNavSection.onEach {
                // when the current tab changes, reset the bottom bar offset to the default value
                model.reduce(MainMviModel.Intent.SetBottomBarOffsetHeightPx(0f))
            }.launchIn(this)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            ) { data ->
                Snackbar(
                    modifier =
                        Modifier
                            .graphicsLayer {
                                translationY =
                                    (-uiState.bottomBarOffsetHeightPx)
                                        .coerceAtMost(bottomBarHeightPx - bottomNavigationInsetPx)
                            },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    snackbarData = data,
                )
            }
        },
        bottomBar = {
            if (hasBottomNavigation) {
                NavigationBar(
                    modifier =
                        Modifier
                            .onGloballyPositioned {
                                if (bottomBarHeightPx == 0f) {
                                    bottomBarHeightPx = it.size.toSize().height
                                }
                            }.offset {
                                IntOffset(
                                    x = 0,
                                    y = -uiState.bottomBarOffsetHeightPx.roundToInt(),
                                )
                            },
                    windowInsets =
                        WindowInsets(
                            left = 0,
                            top = 0,
                            right = 0,
                            bottom = bottomNavigationInsetPx,
                        ),
                    tonalElevation = 0.dp,
                ) {
                    for (section in uiState.bottomNavigationSections) {
                        BottomNavigationItem(
                            section = section,
                            selected = section == currentSection,
                            onClick = {
                                navigationCoordinator.setCurrentSection(section)
                            },
                        )
                    }
                }
            }
        },
    ) {
        if (hasBottomNavigation) {
            Surface(color = MaterialTheme.colorScheme.background) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { navigationCoordinator.pop() },
                    entryProvider = bottomGetEntryProvider(
                        timelineViewModel = timelineViewModel,
                        timelineLazyListState = timelineLazyListState,
                        exploreViewModel = exploreViewModel,
                        exploreLazyListState = exploreLazyListState,
                        inboxViewModel = inboxViewModel,
                        inboxLazyListState = inboxLazyListState,
                        profileViewModel = profileViewModel,
                        myAccountViewModel = myAccountViewModel,
                        myAccountLazyListState = myAccountLazyListState
                    )
                )
            }
        } else {
            when(lockedSection) {
                BottomNavigationSection.Home -> {
                    TimelineScreen(
                        model = timelineViewModel,
                        lazyListState = timelineLazyListState,
                    )
                }
                BottomNavigationSection.Explore -> {
                    ExploreScreen(
                        model = exploreViewModel,
                        lazyListState = exploreLazyListState,
                    )
                }
                is BottomNavigationSection.Inbox -> {
                    InboxScreen(
                        model = inboxViewModel,
                        lazyListState = inboxLazyListState,
                    )
                }
                BottomNavigationSection.Profile -> {
                    ProfileScreen(
                        model = profileViewModel,
                        myAccountModel = myAccountViewModel,
                        myAccountLazyListState = myAccountLazyListState,
                    )
                }
            }
        }
    }
}
