package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import android.content.Context
import android.content.SharedPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Named("default")
@Inject
class DefaultSharedPreferencesProvider(private val context: Context) : SharedPreferencesProvider {

    override fun provide(): SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "shared_prefs"
    }
}
