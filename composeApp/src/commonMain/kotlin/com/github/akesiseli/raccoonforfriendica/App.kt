package com.github.akesiseli.raccoonforfriendica

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.AppTheme
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.CornerSize
import com.github.akesiseli.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.github.akesiseli.raccoonforfriendica.main.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
@OptIn(ExperimentalMaterialApi::class)
fun App() {
    val navigationCoordinator = remember { getNavigationCoordinator() }

    AppTheme(
        useDynamicColors = true,
    ) {
        BottomSheetNavigator(
            sheetShape =
                RoundedCornerShape(
                    topStart = CornerSize.xl,
                    topEnd = CornerSize.xl,
                ),
            sheetBackgroundColor = MaterialTheme.colorScheme.background,
        ) { _ ->
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
