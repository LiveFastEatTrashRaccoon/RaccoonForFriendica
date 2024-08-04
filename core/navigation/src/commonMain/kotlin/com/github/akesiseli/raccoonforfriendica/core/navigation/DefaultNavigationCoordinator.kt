package com.github.akesiseli.raccoonforfriendica.core.navigation

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DefaultNavigationCoordinator : NavigationCoordinator {
    override val currentSection =
        MutableStateFlow<BottomNavigationSection?>(null)
    override val canPop = MutableStateFlow(false)

    private var rootNavigator: Navigator? = null
    private var bottomBarScrollConnection: NestedScrollConnection? = null

    override fun setCurrentSection(section: BottomNavigationSection) {
        currentSection.update { section }
    }

    override fun setRootNavigator(navigator: Navigator) {
        rootNavigator = navigator
        canPop.update { navigator.canPop }
    }

    override fun setBottomBarScrollConnection(connection: NestedScrollConnection?) {
        bottomBarScrollConnection = connection
    }

    override fun getBottomBarScrollConnection() = bottomBarScrollConnection

    override fun push(screen: Screen) {
        rootNavigator?.push(screen)
        canPop.update {
            rootNavigator?.canPop == true
        }
    }

    override fun pop() {
        rootNavigator?.pop()
        canPop.update {
            rootNavigator?.canPop == true
        }
    }
}
