package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.getThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.AppTheme
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.getL10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerEvent
import com.livefast.eattrash.raccoonforfriendica.core.navigation.ScreenContent
import com.livefast.eattrash.raccoonforfriendica.core.navigation.VoyagerNavigator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getCrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getNetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ProvideCustomFontScale
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getSettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getSetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.ProvideCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerContent
import com.livefast.eattrash.raccoonforfriendica.main.MainScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun App(onLoadingFinished: (() -> Unit)? = null) {
    // initialize crash reporting as soon as possible
    val crashReportManager = remember { getCrashReportManager() }
    LaunchedEffect(crashReportManager) {
        crashReportManager.initialize()
    }

    val navigationCoordinator = remember { getNavigationCoordinator() }
    val l10nManager = remember { getL10nManager() }
    val themeRepository = remember { getThemeRepository() }
    val settingsRepository = remember { getSettingsRepository() }
    val activeAccountMonitor = remember { getActiveAccountMonitor() }
    val setupAccountUseCase = remember { getSetupAccountUseCase() }
    val networkStateObserver = remember { getNetworkStateObserver() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerCoordinator = remember { getDrawerCoordinator() }
    val drawerGesturesEnabled by drawerCoordinator.gesturesEnabled.collectAsState()

    LaunchedEffect(settingsRepository) {
        var isInitialized = false
        settingsRepository.current
            .onEach { settings ->
                if (settings != null) {
                    l10nManager.changeLanguage(settings.lang)
                    themeRepository.changeTheme(settings.theme)
                    themeRepository.changeFontFamily(settings.fontFamily)
                    themeRepository.changeFontScale(settings.fontScale)
                    themeRepository.changeCustomSeedColor(
                        color = settings.customSeedColor?.let { c -> Color(color = c) },
                    )

                    if (!isInitialized) {
                        isInitialized = true
                        onLoadingFinished?.invoke()
                    }
                }
            }.launchIn(this)

        activeAccountMonitor.start()
        setupAccountUseCase()
    }

    LaunchedEffect(drawerCoordinator) {
        // centralizes the information about drawer opening
        snapshotFlow {
            drawerState.isClosed
        }.onEach { closed ->
            drawerCoordinator.changeDrawerOpened(!closed)
        }.launchIn(this)

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
    DisposableEffect(networkStateObserver) {
        networkStateObserver.start()
        onDispose {
            networkStateObserver.stop()
        }
    }

    val currentSettings by settingsRepository.current.collectAsState()
    val scope = rememberCoroutineScope()

    AppTheme(
        useDynamicColors = currentSettings?.dynamicColors ?: false,
    ) {
        ProvideCustomUriHandler {
            ProvideCustomFontScale {
                ProvideStrings(
                    lyricist = l10nManager.lyricist,
                ) {
                    ModalNavigationDrawer(
                        modifier = Modifier.fillMaxSize(),
                        drawerState = drawerState,
                        gesturesEnabled = drawerGesturesEnabled,
                        drawerContent = {
                            ModalDrawerSheet {
                                ScreenContent(DrawerContent())
                            }
                        },
                    ) {
                        Navigator(
                            screen = MainScreen,
                            onBackPressed = {
                                // if the drawer is open, closes it
                                if (drawerCoordinator.drawerOpened.value) {
                                    scope.launch {
                                        drawerCoordinator.toggleDrawer()
                                    }
                                    return@Navigator false
                                }

                                // otherwise use the screen-provided callback
                                val callback = navigationCoordinator.getCanGoBackCallback()
                                callback?.let { it() } ?: true
                            },
                        ) { navigator ->
                            LaunchedEffect(navigationCoordinator) {
                                val adapter = VoyagerNavigator(navigator)
                                navigationCoordinator.setRootNavigator(adapter)
                            }

                            CurrentScreen()
                        }
                    }
                }
            }
        }
    }
}
