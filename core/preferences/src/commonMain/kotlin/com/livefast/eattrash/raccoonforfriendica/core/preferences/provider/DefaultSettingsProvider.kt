package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.russhwolf.settings.Settings
import org.koin.core.annotation.Single

@Single
internal expect class DefaultSettingsProvider : SettingsProvider {
    override fun provide(): Settings
}
