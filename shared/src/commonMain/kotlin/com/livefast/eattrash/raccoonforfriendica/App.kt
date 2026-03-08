package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.getThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.AppTheme
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Locales
import com.livefast.eattrash.raccoonforfriendica.core.l10n.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.getL10nManager
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DefaultNavigationAdapter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerEvent
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getCrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getNetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ProvideCustomFontScale
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getSettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getSetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.ProvideCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di.getCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openInternally
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.DrawerContent
import com.livefast.eattrash.raccoonforfriendica.navigation.buildNavigationGraph
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
        ProvideCustomUriHandler {
            ProvideStrings(lang = currentSettings?.lang ?: Locales.EN) {
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
                    PredictiveBackHandler(enabled = canPop) {
                        // if the drawer is open, closes it
                        scope.launch {
                            drawerCoordinator.toggleDrawer()
                        }
                    }
                    ProvideCustomFontScale {
                        NavHost(
                            navController = navController,
                            startDestination = Destination.Main,
                        ) {
                            buildNavigationGraph()
                        }
                    }
                }
            }
        }
    }
}
