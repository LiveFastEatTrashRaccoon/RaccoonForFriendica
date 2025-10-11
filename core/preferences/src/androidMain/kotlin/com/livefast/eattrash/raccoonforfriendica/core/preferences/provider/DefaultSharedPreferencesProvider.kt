package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import android.content.Context
import android.content.SharedPreferences

internal class DefaultSharedPreferencesProvider(private val context: Context) : SharedPreferencesProvider {

    override fun provide(): SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "shared_prefs"
    }
}
