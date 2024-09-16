package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaAttachment(
    @SerialName("description") val description: String? = null,
    @SerialName("id") val id: String,
    @SerialName("meta") val meta: MediaMetadata? = null,
    @SerialName("preview_url") val previewUrl: String? = null,
    @SerialName("type") val type: MediaType,
    @SerialName("url") val url: String = "",
    @SerialName("blurhash") val blurHash: String? = null,
)
