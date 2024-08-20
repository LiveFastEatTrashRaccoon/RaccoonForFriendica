package com.livefast.eattrash.raccoonforfriendica.core.navigation

sealed interface BottomNavigationSection {
    data object Home : BottomNavigationSection

    data object Explore : BottomNavigationSection

    data class Inbox(
        val unreadItems: Int = 0,
    ) : BottomNavigationSection

    data object Profile : BottomNavigationSection
}
