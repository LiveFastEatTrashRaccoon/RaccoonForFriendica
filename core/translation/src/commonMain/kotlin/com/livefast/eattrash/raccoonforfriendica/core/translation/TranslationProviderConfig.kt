package com.livefast.eattrash.raccoonforfriendica.core.translation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TranslationProviderConfig(
    val name: String,
    val url: String,
    val id: String = "",
    val apiKey: String? = null,
    @Transient
    val default: Boolean = false,
)
