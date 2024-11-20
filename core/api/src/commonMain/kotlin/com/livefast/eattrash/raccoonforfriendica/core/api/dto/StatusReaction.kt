package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusReaction(
    @SerialName("count") val count: Int = 0,
    @SerialName("me") val me: Boolean? = null,
    @SerialName("name") val name: String,
    @SerialName("static_url") val staticUrl: String,
    @SerialName("url") val url: String,
)
