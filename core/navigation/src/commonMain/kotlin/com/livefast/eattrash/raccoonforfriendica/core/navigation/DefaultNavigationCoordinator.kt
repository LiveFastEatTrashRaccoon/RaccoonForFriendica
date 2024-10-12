package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultNavigationCoordinator(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : NavigationCoordinator {
    override val currentSection = MutableStateFlow<BottomNavigationSection?>(null)
    override val onDoubleTabSelection = MutableSharedFlow<BottomNavigationSection>()
    override val canPop = MutableStateFlow(false)
    override val exitMessageVisible = MutableStateFlow(false)

    private var rootNavigator: Navigator? = null
    private var bottomBarScrollConnection: NestedScrollConnection? = null
    private var canGoBackCallback: (() -> Boolean)? = null
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun setCurrentSection(section: BottomNavigationSection) {
        currentSection.getAndUpdate { old ->
            if (old == section) {
                scope.launch {
                    onDoubleTabSelection.emit(section)
                }
            }
            section
        }
    }

    override fun setRootNavigator(navigator: Navigator) {
        rootNavigator = navigator
        canPop.update { navigator.canPop }
    }

    override fun setExitMessageVisible(value: Boolean) {
        exitMessageVisible.update { value }
    }

    override fun setBottomBarScrollConnection(connection: NestedScrollConnection?) {
        bottomBarScrollConnection = connection
    }

    override fun getBottomBarScrollConnection() = bottomBarScrollConnection

    override fun setCanGoBackCallback(value: (() -> Boolean)?) {
        canGoBackCallback = value
    }

    override fun getCanGoBackCallback(): (() -> Boolean)? = canGoBackCallback

    override fun push(screen: Screen) {
        rootNavigator?.push(screen)
        canPop.update {
            rootNavigator?.canPop == true
        }
    }

    override fun replace(screen: Screen) {
        rootNavigator?.replace(screen)
    }

    override fun pop() {
        rootNavigator?.pop()
        canPop.update {
            rootNavigator?.canPop == true
        }
    }

    override fun popUntilRoot() {
        rootNavigator?.popUntilRoot()
    }
}
