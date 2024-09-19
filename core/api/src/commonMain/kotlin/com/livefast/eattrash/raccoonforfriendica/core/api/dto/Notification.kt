package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("account") val account: Account? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("dismissed") val dismissed: Boolean = false,
    @SerialName("id") val id: String,
    @SerialName("status") val status: Status? = null,
    @SerialName("type") val type: NotificationType,
)
