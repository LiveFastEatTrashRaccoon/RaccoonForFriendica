package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface BottomNavigationSection : NavKey {
    @Serializable
    data object Home : BottomNavigationSection

    @Serializable
    data object Explore : BottomNavigationSection

    @Serializable
    data class Inbox(val unreadItems: Int = 0) : BottomNavigationSection

    @Serializable
    data object Profile : BottomNavigationSection

    companion object {
        val SavedStateConfiguration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Home::class, Home.serializer())
                    subclass(Explore::class, Explore.serializer())
                    subclass(Inbox::class, Inbox.serializer())
                    subclass(Profile::class, Profile.serializer())
                }
            }
        }
        val Saver = Saver<BottomNavigationSection, String>(
            save = { Json.encodeToString(it) },
            restore = { Json.decodeFromString<BottomNavigationSection>(it) },
        )
    }
}
