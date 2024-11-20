package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    @SerialName("all_day") val allDay: Boolean? = null,
    @SerialName("content") val content: String = "",
    @SerialName("emojis") val emojis: List<CustomEmoji> = emptyList(),
    @SerialName("ends_at") val endsAt: String? = null,
    @SerialName("id") val id: String,
    @SerialName("mentions") val mentions: List<StatusMention> = emptyList(),
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("reactions") val reactions: List<StatusReaction> = emptyList(),
    @SerialName("read") val read: Boolean = false,
    @SerialName("starts_at") val startsAt: String? = null,
    @SerialName("statuses") val statuses: List<StatusReference> = emptyList(),
    @SerialName("tags") val tags: List<Tag> = emptyList(),
    @SerialName("updated_at") val updatedAt: String? = null,
)
