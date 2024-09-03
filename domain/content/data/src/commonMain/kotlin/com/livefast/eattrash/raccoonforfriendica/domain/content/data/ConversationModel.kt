package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class ConversationModel(
    val otherUser: UserModel,
    val lastMessage: DirectMessageModel,
    val messageCount: Int = 0,
)
