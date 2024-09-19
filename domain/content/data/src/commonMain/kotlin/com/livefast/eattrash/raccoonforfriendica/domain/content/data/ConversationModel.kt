package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class ConversationModel(
    val lastMessage: DirectMessageModel,
    val messageCount: Int = 0,
    val otherUser: UserModel,
    val unreadCount: Int = 0,
)
