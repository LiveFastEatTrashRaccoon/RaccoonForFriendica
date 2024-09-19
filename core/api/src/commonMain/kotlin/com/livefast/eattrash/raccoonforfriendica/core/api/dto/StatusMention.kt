package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusMention(
    @SerialName("acct") val acct: String,
    @SerialName("id") val id: String,
    @SerialName("url") val url: String,
    @SerialName("username") val username: String,
)
