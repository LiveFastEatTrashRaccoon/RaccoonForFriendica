package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountSource(
    @SerialName("fields") val fields: List<Field> = emptyList(),
    @SerialName("language") val language: String? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("privacy") val privacy: String? = null,
    @SerialName("sensitive") val sensitive: Boolean? = null,
)
