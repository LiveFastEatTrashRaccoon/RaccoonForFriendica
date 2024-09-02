package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaCircle(
    @SerialName("name") val title: String = "",
    @SerialName("gid") val id: String,
)
