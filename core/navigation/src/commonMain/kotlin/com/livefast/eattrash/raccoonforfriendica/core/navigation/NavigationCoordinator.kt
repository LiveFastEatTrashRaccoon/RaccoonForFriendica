package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.Stable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

@Stable
interface NavigationCoordinator {
    val currentSection: StateFlow<BottomNavigationSection?>
    val onDoubleTabSelection: Flow<BottomNavigationSection>
    val canPop: StateFlow<Boolean>
    val exitMessageVisible: StateFlow<Boolean>
    val deepLinkUrl: SharedFlow<String>
    val globalMessage: Flow<String>

    fun setCurrentSection(section: BottomNavigationSection)

    fun getBottomBarScrollConnection(): NestedScrollConnection?

    fun setBottomBarScrollConnection(connection: NestedScrollConnection?)

    fun setCanGoBackCallback(value: (() -> Boolean)?)

    fun getCanGoBackCallback(): (() -> Boolean)?

    fun setExitMessageVisible(value: Boolean)

    fun setRootNavigator(navigator: NavigatorAdapter)

    fun replace(screen: Screen)

    fun push(screen: Screen)

    fun pop()

    fun popUntilRoot()

    suspend fun submitDeeplink(url: String)

    fun showGlobalMessage(message: String, delay: Duration = Duration.ZERO)
}
