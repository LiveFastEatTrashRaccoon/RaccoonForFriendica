package com.livefast.eattrash.raccoonforfriendica.core.navigation

import kotlinx.serialization.Serializable

sealed interface BottomNavigationSection {
    @Serializable
    data object Home : BottomNavigationSection

    @Serializable
    data object Explore : BottomNavigationSection

    @Serializable
    data class Inbox(val unreadItems: Int = 0) : BottomNavigationSection

    @Serializable
    data object Profile : BottomNavigationSection
}
