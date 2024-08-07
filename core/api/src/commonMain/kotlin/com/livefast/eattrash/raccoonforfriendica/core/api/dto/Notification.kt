package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("id") val id: String,
    @SerialName("type") val type: NotificationType,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("account") val account: Account? = null,
    @SerialName("status") val status: Status? = null,
)
