package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaCircle(@SerialName("gid") val id: String, @SerialName("name") val title: String = "")
