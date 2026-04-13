package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    @SerialName("state") val state: String? = null,
    @SerialName("quoted_status") val quotedStatus: Status? = null,
    @SerialName("quoted_status_id") val quotedStatusId: String? = null,
)
