package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.util.prefs.Preferences

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultSettingsProvider(private val encryptionHelper: EncryptionHelper) : SettingsProvider {

    override fun provide(): Settings = EncryptingPreferencesSettings(
        preferences = Preferences.userRoot(),
        encryptionHelper = encryptionHelper,
    )
}
