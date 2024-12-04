package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.russhwolf.settings.Settings

interface SettingsProvider {
    fun provide(): Settings
}
