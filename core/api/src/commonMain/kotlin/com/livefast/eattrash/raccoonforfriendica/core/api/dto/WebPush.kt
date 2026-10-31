package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebPush(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("preferred_locale") val preferredLocale: String? = null,
    @SerialName("notification_id") val id: Long,
    @SerialName("notification_type") val type: NotificationType,
    @SerialName("icon") val icon: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("body") val body: String? = null,
)
