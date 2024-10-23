package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushSubscriptionAlerts(
    @SerialName("follow") val follow: Boolean = false,
    @SerialName("favorite") val favorite: Boolean = false,
    @SerialName("reblog") val reblog: Boolean = false,
    @SerialName("mention") val mention: Boolean = false,
    @SerialName("poll") val poll: Boolean = false,
)
