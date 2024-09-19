package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceIcon(
    @SerialName("size") val size: String? = null,
    @SerialName("url") val url: String? = null,
)
