package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduledStatus(
    @SerialName("id") val id: String,
    @SerialName("media_attachments") val attachments: List<MediaAttachment> = emptyList(),
    @SerialName("params") val params: ScheduledStatusParams? = null,
    @SerialName("scheduled_at") val scheduledAt: String? = null,
)
