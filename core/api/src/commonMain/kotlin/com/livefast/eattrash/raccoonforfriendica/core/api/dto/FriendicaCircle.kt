package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaCircle(
    @SerialName("name") val name: String = "",
    @SerialName("gid") val id: Long,
    @SerialName("user") val users: List<FriendicaContact> = emptyList(),
)
