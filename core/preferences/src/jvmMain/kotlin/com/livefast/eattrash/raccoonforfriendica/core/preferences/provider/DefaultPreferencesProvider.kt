package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import org.koin.core.annotation.Single
import java.util.prefs.Preferences

@Single
internal class DefaultPreferencesProvider : PreferencesProvider {
    override fun provide(): Preferences = Preferences.userRoot()
}
