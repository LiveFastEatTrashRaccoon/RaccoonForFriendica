package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

internal class DefaultSettingsProvider : SettingsProvider {

    override fun provide(): Settings = PreferencesSettings(Preferences.userRoot())
}
