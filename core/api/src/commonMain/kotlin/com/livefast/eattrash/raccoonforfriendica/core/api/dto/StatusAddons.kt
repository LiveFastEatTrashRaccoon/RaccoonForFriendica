package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusAddons(
    @SerialName("title") val title: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("disliked") val disliked: Boolean? = null,
    @SerialName("dislikes_count") val dislikesCount: Int? = null,
    @SerialName("platform") val platform: String? = null,
    @SerialName("network") val network: String? = null,
)
