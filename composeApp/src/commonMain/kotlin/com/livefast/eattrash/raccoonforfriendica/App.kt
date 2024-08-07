package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.getThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.AppTheme
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.getL10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getSettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getSetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.main.MainScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val l10nManager = remember { getL10nManager() }
    val themeRepository = remember { getThemeRepository() }
    val settingsRepository = remember { getSettingsRepository() }
    val setupAccountUseCase = remember { getSetupAccountUseCase() }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(settingsRepository) {
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
                    }
                }
            }.launchIn(this)

        setupAccountUseCase()
    }

    if (isInitialized) {
        val currentSettings by settingsRepository.current.collectAsState()
        AppTheme(
            useDynamicColors = currentSettings?.dynamicColors ?: false,
        ) {
            ProvideStrings(
                lyricist = l10nManager.lyricist,
            ) {
                Navigator(
                    screen = MainScreen,
                ) { navigator ->
                    LaunchedEffect(navigationCoordinator) {
                        navigationCoordinator.setRootNavigator(navigator)
                    }

                    ModalNavigationDrawer(
                        drawerContent = {},
                    ) {
                        CurrentScreen()
                    }
                }
            }
        }
    }
}
