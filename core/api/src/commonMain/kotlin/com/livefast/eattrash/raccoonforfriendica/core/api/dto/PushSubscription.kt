package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushSubscription(
    @SerialName("id") val id: String,
    @SerialName("endpoint") val endpoint: String? = null,
    // corresponds to the server's VAPID key
    @SerialName("server_key") val serverKey: String? = null,
    @SerialName("alerts") val alerts: PushSubscriptionAlerts? = null,
)
