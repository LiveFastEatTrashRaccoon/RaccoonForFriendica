package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceConfigurationStatuses(
    @SerialName("max_characters") val maxCharacters: Int? = null,
    @SerialName("max_media_attachments") val maxMediaAttachments: Int? = null,
)
