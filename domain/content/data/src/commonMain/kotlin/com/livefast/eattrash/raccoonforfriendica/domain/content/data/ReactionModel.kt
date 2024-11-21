package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class ReactionModel(
    val count: Int = 0,
    val isMe: Boolean = false,
    val name: String,
    val url: String? = null,
    val staticUrl: String? = null,
)
