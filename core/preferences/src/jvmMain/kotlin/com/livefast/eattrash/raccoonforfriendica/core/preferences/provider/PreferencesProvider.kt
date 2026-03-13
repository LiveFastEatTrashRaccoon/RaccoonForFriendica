package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import java.util.prefs.Preferences

interface PreferencesProvider {
    fun provide(): Preferences
}
