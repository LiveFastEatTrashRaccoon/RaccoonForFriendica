package com.livefast.eattrash.raccoonforfriendica.core.preferences

internal interface SettingsWrapper {
    val keys: Set<String>

    fun hasKey(key: String): Boolean

    fun remove(key: String)

    fun clear()

    operator fun get(
        key: String,
        default: Int,
    ): Int

    operator fun get(
        key: String,
        default: Long,
    ): Long

    operator fun get(
        key: String,
        default: String,
    ): String

    operator fun get(
        key: String,
        default: Boolean,
    ): Boolean

    operator fun get(
        key: String,
        default: Float,
    ): Float

    operator fun get(
        key: String,
        default: Double,
    ): Double

    operator fun set(
        key: String,
        value: Int,
    )

    operator fun set(
        key: String,
        value: Long,
    )

    operator fun set(
        key: String,
        value: String,
    )

    operator fun set(
        key: String,
        value: Float,
    )

    operator fun set(
        key: String,
        value: Double,
    )

    operator fun set(
        key: String,
        value: Boolean,
    )
}
