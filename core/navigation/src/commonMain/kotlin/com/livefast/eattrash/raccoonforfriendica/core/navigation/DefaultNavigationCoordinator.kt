package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
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
    override val currentBottomNavSection = MutableStateFlow<BottomNavigationSection?>(null)
    override val onDoubleTabSelection = MutableSharedFlow<BottomNavigationSection>()
    override val canPop = MutableStateFlow(false)
    override val exitMessageVisible = MutableStateFlow(false)
    override val deepLinkUrl = MutableSharedFlow<String>()
    override val globalMessage = MutableSharedFlow<String>()

    private var rootNavController: NavigationAdapter? = null
    private var bottomNavController: BottomNavigationAdapter? = null
    private var bottomBarScrollConnection: NestedScrollConnection? = null
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun setBottomNavigator(adapter: BottomNavigationAdapter) {
        bottomNavController = adapter
    }

    override fun setCurrentSection(section: BottomNavigationSection) {
        bottomNavController?.navigate(section)
        currentBottomNavSection.getAndUpdate { old ->
            if (old == section) {
                scope.launch {
                    onDoubleTabSelection.emit(section)
                }
            }
            section
        }
    }

    override fun setRootNavigator(adapter: NavigationAdapter) {
        rootNavController = adapter
        refreshCanPop()
    }

    override fun popUntilRoot() {
        rootNavController?.popUntilRoot()
        refreshCanPop()
    }

    override fun setExitMessageVisible(value: Boolean) {
        exitMessageVisible.update { value }
    }

    override fun setBottomBarScrollConnection(connection: NestedScrollConnection?) {
        bottomBarScrollConnection = connection
    }

    override fun getBottomBarScrollConnection() = bottomBarScrollConnection

    override fun push(destination: Destination) {
        rootNavController?.navigate(destination)
        refreshCanPop()
    }

    override fun replace(destination: Destination) {
        rootNavController?.navigate(destination, replaceTop = true)
    }

    override fun pop() {
        rootNavController?.pop()
        refreshCanPop()
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

    private fun refreshCanPop() {
        canPop.update {
            rootNavController?.canPop ?: false
        }
    }
}
