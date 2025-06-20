package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration

internal class DefaultNavigationCoordinator(dispatcher: CoroutineDispatcher = Dispatchers.Main) :
    NavigationCoordinator {
    override val currentSection = MutableStateFlow<BottomNavigationSection?>(null)
    override val onDoubleTabSelection = MutableSharedFlow<BottomNavigationSection>()
    override val canPop = MutableStateFlow(false)
    override val exitMessageVisible = MutableStateFlow(false)
    override val deepLinkUrl = MutableSharedFlow<String>()
    override val globalMessage = MutableSharedFlow<String>()

    private var rootNavigator: NavigatorAdapter? = null
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

    override fun setRootNavigator(navigator: NavigatorAdapter) {
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

    override suspend fun submitDeeplink(url: String) {
        delay(750)
        deepLinkUrl.emit(url)
    }

    override fun showGlobalMessage(message: String, delay: Duration) {
        scope.launch {
            delay(delay)
            globalMessage.emit(message)
        }
    }
}
