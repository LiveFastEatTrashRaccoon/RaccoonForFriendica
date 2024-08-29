package com.livefast.eattrash.raccoonforfriendica.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.livefast.eattrash.raccoonforfriendica.bottomnavigation.BottomNavigationItem
import com.livefast.eattrash.raccoonforfriendica.bottomnavigation.HomeTab
import com.livefast.eattrash.raccoonforfriendica.bottomnavigation.toTab
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

object MainScreen : Screen {
    @Composable
    override fun Content() {
        val model = getScreenModel<MainMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val currentSection by navigationCoordinator.currentSection.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val maxTopInsetPx = with(LocalDensity.current) { Dimensions.maxTopBarInset.toPx() }
        var bottomBarHeightPx by remember { mutableStateOf(0f) }
        val bottomNavigationInsetPx =
            with(LocalDensity.current) {
                WindowInsets.navigationBars.getBottom(this)
            }
        val bottomNavigationInset =
            with(LocalDensity.current) {
                bottomNavigationInsetPx.toDp()
            }
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
                                -(bottomBarHeightPx + bottomNavigationInsetPx + maxTopInsetPx),
                                0f,
                            )
                        model.reduce(MainMviModel.Intent.SetBottomBarOffsetHeightPx(newOffset))
                        return Offset.Zero
                    }
                }
            }
        navigationCoordinator.setBottomBarScrollConnection(scrollConnection)
        val exitMessage = LocalStrings.current.messageConfirmExit

        LaunchedEffect(navigationCoordinator) {
            if (navigationCoordinator.currentSection.value == null) {
                navigationCoordinator.setCurrentSection(BottomNavigationSection.Home)
            }

            navigationCoordinator.exitMessageVisible
                .onEach { visible ->
                    if (visible) {
                        snackbarHostState.showSnackbar(
                            message = exitMessage,
                            duration = SnackbarDuration.Short,
                        )
                        navigationCoordinator.setExitMessageVisible(false)
                    }
                }.launchIn(this)
        }

        TabNavigator(HomeTab) { tabNavigator ->
            LaunchedEffect(tabNavigator.current) {
                // when the current tab changes, reset the bottom bar offset to the default value
                model.reduce(MainMviModel.Intent.SetBottomBarOffsetHeightPx(0f))
            }
            LaunchedEffect(navigationCoordinator) {
                navigationCoordinator.currentSection
                    .onEach { section ->
                        section?.toTab()?.also { tab ->
                            tabNavigator.current = tab
                        }
                    }.launchIn(this)
            }

            Scaffold(
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
                                left = 0.dp,
                                top = 0.dp,
                                right = 0.dp,
                                bottom = bottomNavigationInset,
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
                CurrentTab()
            }
        }
    }
}
