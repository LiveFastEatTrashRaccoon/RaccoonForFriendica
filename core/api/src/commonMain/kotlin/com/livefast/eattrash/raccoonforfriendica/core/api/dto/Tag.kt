package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    @SerialName("following") val following: Boolean? = null,
    @SerialName("history") val history: List<HistoryItem>? = null,
    @SerialName("name") val name: String,
    @SerialName("url") val url: String? = null,
)
