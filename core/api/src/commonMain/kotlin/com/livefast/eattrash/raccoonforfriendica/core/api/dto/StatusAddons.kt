package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusAddons(
    @SerialName("title") val title: String? = null,
    @SerialName("disliked") val disliked: Boolean = false,
    @SerialName("dislikes_count") val dislikesCount: Int = 0,
)
