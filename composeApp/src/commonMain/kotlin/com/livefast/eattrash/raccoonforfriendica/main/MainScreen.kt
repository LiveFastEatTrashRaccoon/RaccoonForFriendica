package com.livefast.eattrash.raccoonforfriendica.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.core.screen.Screen
import com.livefast.eattrash.raccoonforfriendica.bottomnavigation.BottomNavigationItem
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreScreen
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxScreen
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

object MainScreen : Screen {
    @Composable
    override fun Content() {
        val model: MainMviModel = getViewModel<MainViewModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
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
        val bottomNavController = rememberNavController()

        LaunchedEffect(navigationCoordinator) {
            if (navigationCoordinator.currentBottomNavSection.value == null) {
                navigationCoordinator.setCurrentSection(BottomNavigationSection.Home)
            }

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

            navigationCoordinator.currentBottomNavSection.onEach {
                // when the current tab changes, reset the bottom bar offset to the default value
                model.reduce(MainMviModel.Intent.SetBottomBarOffsetHeightPx(0f))
            }.launchIn(this)

            navigationCoordinator.setBottomNavController(bottomNavController)
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
            },
        ) {
            val timelineModel: TimelineMviModel = getViewModel<TimelineViewModel>()
            val timelineLazyListState = rememberLazyListState()
            val exploreModel: ExploreMviModel = getViewModel<ExploreViewModel>()
            val exploreLazyListState = rememberLazyListState()
            val inboxModel: InboxMviModel = getViewModel<InboxViewModel>()
            val inboxLazyListState = rememberLazyListState()
            val profileModel: ProfileMviModel = getViewModel<ProfileViewModel>()
            val myAccountModel: MyAccountMviModel = getViewModel<MyAccountViewModel>()
            val myAccountLazyListState = rememberLazyListState()
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavigationSection.Home,
            ) {
                composable<BottomNavigationSection.Home> {
                    TimelineScreen(
                        model = timelineModel,
                        lazyListState = timelineLazyListState,
                    )
                }
                composable<BottomNavigationSection.Explore> {
                    ExploreScreen(
                        model = exploreModel,
                        lazyListState = exploreLazyListState,
                    )
                }
                composable<BottomNavigationSection.Inbox> {
                    InboxScreen(
                        model = inboxModel,
                        lazyListState = inboxLazyListState,
                    )
                }
                composable<BottomNavigationSection.Profile> {
                    ProfileScreen(
                        model = profileModel,
                        myAccountModel = myAccountModel,
                        myAccountLazyListState = myAccountLazyListState,
                    )
                }
            }
        }
    }
}
