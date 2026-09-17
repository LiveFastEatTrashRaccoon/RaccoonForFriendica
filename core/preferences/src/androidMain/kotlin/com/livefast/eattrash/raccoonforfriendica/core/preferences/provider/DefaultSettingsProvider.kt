package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultSettingsProvider(private val context: Context, private val encryptionHelper: EncryptionHelper) :
    SettingsProvider {
    override fun provide(): Settings = EncryptingSharedPreferencesSettings(
        preferences = context.getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE),
        encryptionHelper = encryptionHelper,
    )

    companion object {
        private const val DEFAULT_NAME = "shared_prefs"
    }
}
