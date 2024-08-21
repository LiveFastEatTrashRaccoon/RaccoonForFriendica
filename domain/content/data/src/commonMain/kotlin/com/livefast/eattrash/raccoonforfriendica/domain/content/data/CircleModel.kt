package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class CircleModel(
    val id: String,
    val name: String = "",
    val replyPolicy: CircleReplyPolicy = CircleReplyPolicy.List,
    val exclusive: Boolean = false,
)
