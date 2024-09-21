package com.livefast.eattrash.raccoonforfriendica.core.api.form

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusAddons
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CreateStatusForm(
    @SerialName("friendica") val addons: StatusAddons? = null,
    @SerialName("in_reply_to_id") val inReplyTo: String? = null,
    @SerialName("language") val lang: String? = null,
    @SerialName("media_ids") val mediaIds: List<String>? = null,
    @SerialName("scheduled_at") val scheduledAt: String? = null,
    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("spoiler_text") val spoilerText: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("visibility") val visibility: String? = null,
    @SerialName("poll") val poll: CreatePollForm? = null,
)
