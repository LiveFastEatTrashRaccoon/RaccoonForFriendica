package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceThumbnail(
    @SerialName("url") val url: String? = null,
    @SerialName("blurhash") val blurhash: String? = null,
)
