package com.livefast.eattrash.raccoonforfriendica.core.navigation

sealed interface BottomNavigationSection {
    data object Home : BottomNavigationSection

    data object Explore : BottomNavigationSection

    data object Inbox : BottomNavigationSection

    data object Profile : BottomNavigationSection
}
