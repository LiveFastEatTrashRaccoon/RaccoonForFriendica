package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    @SerialName("content") val content: String,
    @SerialName("spoiler_text") val spoiler: String? = null,
    @SerialName("media_attachments") val attachments: List<MediaAttachment> = emptyList(),
    @SerialName("poll") val poll: Poll? = null,
    @SerialName("provider") val provider: String? = null,
    @SerialName("detected_source_language") val detectedSourceLanguage: String? = null,
)
