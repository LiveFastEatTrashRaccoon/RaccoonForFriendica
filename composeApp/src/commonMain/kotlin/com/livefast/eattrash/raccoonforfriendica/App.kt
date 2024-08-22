package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getSettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ProvideCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getSetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerContent
import com.livefast.eattrash.raccoonforfriendica.main.MainScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun App(onLoadingFinished: (() -> Unit)? = null) {
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val l10nManager = remember { getL10nManager() }
    val themeRepository = remember { getThemeRepository() }
    val settingsRepository = remember { getSettingsRepository() }
    val setupAccountUseCase = remember { getSetupAccountUseCase() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerCoordinator = remember { getDrawerCoordinator() }
    val apiConfigurationRepository = remember { getApiConfigurationRepository() }
    val drawerGesturesEnabled by drawerCoordinator.gesturesEnabled.collectAsState()

    LaunchedEffect(settingsRepository) {
        var isInitialized = false
        settingsRepository.current
            .onEach { settings ->
                if (settings != null) {
                    l10nManager.changeLanguage(settings.lang)
                    themeRepository.changeTheme(settings.theme)
                    themeRepository.changeFontFamily(settings.fontFamily)
                    themeRepository.changeCustomSeedColor(
                        color = settings.customSeedColor?.let { c -> Color(color = c) },
                    )

                    if (!isInitialized) {
                        isInitialized = true
                        onLoadingFinished?.invoke()
                    }
                }
            }.launchIn(this)

        apiConfigurationRepository.initialize()
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

    val currentSettings by settingsRepository.current.collectAsState()
    val scope = rememberCoroutineScope()

    AppTheme(
        useDynamicColors = currentSettings?.dynamicColors ?: false,
    ) {
        ProvideCustomUriHandler {
            ProvideStrings(
                lyricist = l10nManager.lyricist,
            ) {
                ModalNavigationDrawer(
                    modifier = Modifier.fillMaxSize(),
                    drawerState = drawerState,
                    gesturesEnabled = drawerGesturesEnabled,
                    drawerContent = {
                        ModalDrawerSheet {
                            Navigator(DrawerContent())
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

                            true
                        },
                    ) { navigator ->
                        LaunchedEffect(navigationCoordinator) {
                            navigationCoordinator.setRootNavigator(navigator)
                        }

                        ModalNavigationDrawer(
                            modifier = Modifier.fillMaxSize(),
                            drawerState = drawerState,
                            gesturesEnabled = drawerGesturesEnabled,
                            drawerContent = {
                                Navigator(DrawerContent())
                            },
                        ) {
                            CurrentScreen()
                        }
                    }
                }
            }
        }
    }
}
