package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Field(
    @SerialName("name") val name: String,
    @SerialName("value") val value: String,
    @SerialName("verified_at") val verifiedAt: String? = null,
)
