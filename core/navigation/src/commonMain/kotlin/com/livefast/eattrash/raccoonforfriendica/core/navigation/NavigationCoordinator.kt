package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.Stable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface NavigationCoordinator {
    val currentSection: StateFlow<BottomNavigationSection?>
    val onDoubleTabSelection: Flow<BottomNavigationSection>
    val canPop: StateFlow<Boolean>
    val exitMessageVisible: StateFlow<Boolean>

    fun setCurrentSection(section: BottomNavigationSection)

    fun getBottomBarScrollConnection(): NestedScrollConnection?

    fun setBottomBarScrollConnection(connection: NestedScrollConnection?)

    fun setCanGoBackCallback(value: (() -> Boolean)?)

    fun getCanGoBackCallback(): (() -> Boolean)?

    fun setExitMessageVisible(value: Boolean)

    fun setRootNavigator(navigator: Navigator)

    fun replace(screen: Screen)

    fun push(screen: Screen)

    fun pop()

    fun popUntilRoot()
}
