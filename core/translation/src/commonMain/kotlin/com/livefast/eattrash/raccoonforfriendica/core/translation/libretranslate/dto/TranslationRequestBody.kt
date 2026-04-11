package com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslationRequestBody(
    @SerialName("q") val text: String,
    @SerialName("source") val source: String,
    @SerialName("target") val target: String,
    @SerialName("format") val format: String,
    @SerialName("api_key")val apiKey: String,
)
