package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaMetadata(@SerialName("original") val original: MediaMetadataItem? = null)

@Serializable
data class MediaMetadataItem(
    @SerialName("duration") val duration: Double? = null,
    @SerialName("height") val height: Int? = null,
    @SerialName("width") val width: Int? = null,
)
