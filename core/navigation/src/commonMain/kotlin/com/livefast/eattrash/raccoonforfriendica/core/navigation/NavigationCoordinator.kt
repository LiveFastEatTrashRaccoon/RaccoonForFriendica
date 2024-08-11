package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.Stable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.StateFlow

@Stable
interface NavigationCoordinator {
    val currentSection: StateFlow<BottomNavigationSection?>
    val canPop: StateFlow<Boolean>
    val exitMessageVisible: StateFlow<Boolean>

    fun setCurrentSection(section: BottomNavigationSection)

    fun getBottomBarScrollConnection(): NestedScrollConnection?

    fun setBottomBarScrollConnection(connection: NestedScrollConnection?)

    fun setExitMessageVisible(value: Boolean)

    fun setRootNavigator(navigator: Navigator)

    fun push(screen: Screen)

    fun pop()

    fun popUntilRoot()
}
