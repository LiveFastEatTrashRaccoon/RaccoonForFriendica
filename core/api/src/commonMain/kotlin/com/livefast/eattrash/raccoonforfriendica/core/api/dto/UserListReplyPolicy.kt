package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName

enum class UserListReplyPolicy {
    @SerialName("followed")
    FOLLOWED,

    @SerialName("list")
    LIST,

    @SerialName("none")
    NONE,
}
