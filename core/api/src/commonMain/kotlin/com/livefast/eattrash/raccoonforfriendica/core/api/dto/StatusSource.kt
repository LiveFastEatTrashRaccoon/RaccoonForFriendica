package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusSource(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String? = null,
    @SerialName("spoiler_text") val spoilerText: String? = null,
)
