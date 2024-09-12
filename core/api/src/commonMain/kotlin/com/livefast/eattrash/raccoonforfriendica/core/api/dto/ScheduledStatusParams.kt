package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduledStatusParams(
    @SerialName("idempotency") val idempotency: String? = null,
    @SerialName("in_reply_to_id") val inReplyToId: String? = null,
    @SerialName("language") val lang: String? = null,
    @SerialName("media_attachments") val attachments: List<MediaAttachment> = emptyList(),
    @SerialName("media_ids") val mediaIds: List<String> = emptyList(),
    @SerialName("poll") val poll: Poll? = null,
    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("spoiler_text") val spoilerText: String? = null,
    @SerialName("text") val text: String? = null,
    @SerialName("visibility") val visibility: String = ContentVisibility.PUBLIC,
    @SerialName("with_rate_limit") val withRateLimit: Boolean = false,
)
