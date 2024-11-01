package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class CircleModel(
    val exclusive: Boolean = false,
    val id: String,
    val name: String = "",
    val replyPolicy: CircleReplyPolicy = CircleReplyPolicy.List,
    val type: CircleType = CircleType.Other,
)

val CircleModel.canBeEdited: Boolean get() = type == CircleType.UserDefined
