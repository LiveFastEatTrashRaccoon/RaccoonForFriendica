package com.livefast.eattrash.raccoonforfriendica.core.preferences.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import org.koin.core.annotation.Single

@Single
internal class DefaultSettingsWrapper(
    private val settings: Settings,
) : SettingsWrapper {
    override val keys: Set<String> = settings.keys

    override fun hasKey(key: String): Boolean = settings.hasKey(key)

    override fun remove(key: String) {
        settings.remove(key)
    }

    override fun clear() {
        settings.clear()
    }

    override fun get(
        key: String,
        default: Int,
    ): Int = settings[key, default]

    override fun get(
        key: String,
        default: Long,
    ): Long = settings[key, default]

    override fun get(
        key: String,
        default: String,
    ): String = settings[key, default]

    override fun get(
        key: String,
        default: Boolean,
    ): Boolean = settings[key, default]

    override fun get(
        key: String,
        default: Float,
    ): Float = settings[key, default]

    override fun get(
        key: String,
        default: Double,
    ): Double = settings[key, default]

    override fun set(
        key: String,
        value: Int,
    ) {
        settings[key] = value
    }

    override fun set(
        key: String,
        value: Long,
    ) {
        settings[key] = value
    }

    override fun set(
        key: String,
        value: String,
    ) {
        settings[key] = value
    }

    override fun set(
        key: String,
        value: Float,
    ) {
        settings[key] = value
    }

    override fun set(
        key: String,
        value: Double,
    ) {
        settings[key] = value
    }

    override fun set(
        key: String,
        value: Boolean,
    ) {
        settings[key] = value
    }
}
