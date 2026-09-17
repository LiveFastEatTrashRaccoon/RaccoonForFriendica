package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSUserDefaults

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultSettingsProvider(private val encryptionHelper: EncryptionHelper) : SettingsProvider {
    override fun provide(): Settings = EncryptingSettings(
        settings = NSUserDefaultsSettings(delegate = NSUserDefaults(suiteName = DEFAULT_NAME)),
        encryptionHelper = encryptionHelper,
    )

    companion object {
        private const val DEFAULT_NAME = "shared_prefs"
    }
}
