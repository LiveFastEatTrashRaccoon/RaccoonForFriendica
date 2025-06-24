package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.Stable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

@Stable
interface NavigationCoordinator {
    val currentBottomNavSection: StateFlow<BottomNavigationSection?>
    val onDoubleTabSelection: Flow<BottomNavigationSection>
    val canPop: StateFlow<Boolean>
    val exitMessageVisible: StateFlow<Boolean>
    val deepLinkUrl: SharedFlow<String>
    val globalMessage: Flow<String>

    fun setRootNavigator(adapter: NavigationAdapter)

    fun setBottomNavigator(adapter: BottomNavigationAdapter)

    fun setCurrentSection(section: BottomNavigationSection)

    fun getBottomBarScrollConnection(): NestedScrollConnection?

    fun setBottomBarScrollConnection(connection: NestedScrollConnection?)

    fun setExitMessageVisible(value: Boolean)

    fun replace(destination: Destination)

    fun push(destination: Destination)

    fun pop()

    fun popUntilRoot()

    suspend fun submitDeeplink(url: String)

    fun showGlobalMessage(message: String, delay: Duration = Duration.ZERO)
}
