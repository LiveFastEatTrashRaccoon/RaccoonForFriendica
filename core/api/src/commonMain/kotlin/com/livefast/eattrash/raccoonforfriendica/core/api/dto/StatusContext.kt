package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusContext(
    @SerialName("ancestors") val ancestors: List<Status> = emptyList(),
    @SerialName("descendants") val descendants: List<Status> = emptyList(),
)
