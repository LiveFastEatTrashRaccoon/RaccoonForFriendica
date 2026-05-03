package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.saveable.Saver
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface BottomNavigationSection {
    @Serializable
    data object Home : BottomNavigationSection

    @Serializable
    data object Explore : BottomNavigationSection

    @Serializable
    data class Inbox(val unreadItems: Int = 0) : BottomNavigationSection

    @Serializable
    data object Profile : BottomNavigationSection

    companion object {
        val Saver = Saver<BottomNavigationSection, String>(
            save = { Json.encodeToString(it) },
            restore = { Json.decodeFromString<BottomNavigationSection>(it) },
        )
    }
}
