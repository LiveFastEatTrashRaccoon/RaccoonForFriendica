package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import android.content.SharedPreferences

interface SharedPreferencesProvider {
    fun provide(): SharedPreferences
}
