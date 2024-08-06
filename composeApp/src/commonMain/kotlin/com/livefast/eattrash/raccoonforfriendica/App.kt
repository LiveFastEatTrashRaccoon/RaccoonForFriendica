package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.AppTheme
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.getL10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.main.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val l10nManager = remember { getL10nManager() }

    AppTheme(
        useDynamicColors = true,
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
