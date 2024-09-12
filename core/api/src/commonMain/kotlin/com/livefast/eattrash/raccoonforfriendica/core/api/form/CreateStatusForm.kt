package com.livefast.eattrash.raccoonforfriendica.core.api.form

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusAddons
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CreateStatusForm(
    @SerialName("status") val status: String? = null,
    @SerialName("media_ids") val mediaIds: List<String>? = null,
    @SerialName("visibility") val visibility: String? = null,
    @SerialName("friendica") val addons: StatusAddons? = null,
    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("language") val lang: String? = null,
    @SerialName("in_reply_to_id") val inReplyTo: String? = null,
    @SerialName("spoiler_text") val spoilerText: String? = null,
    @SerialName("scheduled_at") val scheduledAt: String? = null,
)
