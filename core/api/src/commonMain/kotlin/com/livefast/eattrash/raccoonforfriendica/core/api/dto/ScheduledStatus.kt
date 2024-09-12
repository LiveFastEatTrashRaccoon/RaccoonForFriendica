package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduledStatus(
    @SerialName("id") val id: String,
    @SerialName("scheduled_at") val scheduledAt: String? = null,
    @SerialName("media_attachments") val attachments: List<MediaAttachment> = emptyList(),
    @SerialName("params") val params: ScheduledStatusParams? = null,
)
