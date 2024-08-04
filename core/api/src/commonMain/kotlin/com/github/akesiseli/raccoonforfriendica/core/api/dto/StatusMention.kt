package com.github.akesiseli.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusMention(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("url") val url: String,
    @SerialName("acct") val acct: String,
)
