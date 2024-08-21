package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserList(
    @SerialName("title") val title: String = "",
    @SerialName("id") val id: String,
    @SerialName("replies_policy") val repliesPolicy: UserListReplyPolicy? = null,
    @SerialName("exclusive") val exclusive: Boolean = false,
)
