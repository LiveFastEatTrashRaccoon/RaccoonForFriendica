package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import java.util.prefs.Preferences

internal class DefaultPreferencesProvider : PreferencesProvider {
    override fun provide(): Preferences = Preferences.userRoot()
}
