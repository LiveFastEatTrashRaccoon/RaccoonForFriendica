package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName

enum class UserListReplyPolicy {
    @SerialName("follow")
    FOLLOW,

    @SerialName("list")
    LIST,

    @SerialName("none")
    NONE,
}
