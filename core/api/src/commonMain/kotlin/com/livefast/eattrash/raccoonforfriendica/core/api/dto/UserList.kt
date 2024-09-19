package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserList(
    @SerialName("exclusive") val exclusive: Boolean = false,
    @SerialName("id") val id: String,
    @SerialName("replies_policy") val repliesPolicy: UserListReplyPolicy? = null,
    @SerialName("title") val title: String = "",
)
