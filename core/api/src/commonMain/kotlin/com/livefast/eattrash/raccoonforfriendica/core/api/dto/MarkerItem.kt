package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarkerItem(
    @SerialName("last_read_id") val lastReadId: String,
    @SerialName("version") val version: Int = 0,
    @SerialName("updated_at") val updatedAt: String? = null,
)
