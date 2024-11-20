package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class AnnouncementModel(
    val id: String,
    val content: String = "",
    val emojis: List<EmojiModel> = emptyList(),
    val read: Boolean = false,
    val updated: String? = null,
    val published: String? = null,
    val reactions: List<ReactionModel> = emptyList(),
)
