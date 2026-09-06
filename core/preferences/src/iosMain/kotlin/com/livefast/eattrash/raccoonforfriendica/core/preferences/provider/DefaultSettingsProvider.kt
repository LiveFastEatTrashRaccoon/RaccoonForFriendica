package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultSettingsProvider : SettingsProvider {
    @OptIn(ExperimentalSettingsImplementation::class)
    override fun provide(): Settings = KeychainSettings(service = DEFAULT_NAME)

    companion object {
        private const val DEFAULT_NAME = "secret_shared_prefs"
    }
}
