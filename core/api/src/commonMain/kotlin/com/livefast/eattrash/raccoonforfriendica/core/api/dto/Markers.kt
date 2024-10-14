package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Markers(
    @SerialName("home") val home: MarkerItem? = null,
    @SerialName("notifications") val notifications: MarkerItem? = null,
)
