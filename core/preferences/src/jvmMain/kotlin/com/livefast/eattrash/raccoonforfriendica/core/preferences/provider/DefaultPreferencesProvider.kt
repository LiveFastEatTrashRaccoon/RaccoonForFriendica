package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.util.prefs.Preferences

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultPreferencesProvider : PreferencesProvider {
    override fun provide(): Preferences = Preferences.userRoot()
}
