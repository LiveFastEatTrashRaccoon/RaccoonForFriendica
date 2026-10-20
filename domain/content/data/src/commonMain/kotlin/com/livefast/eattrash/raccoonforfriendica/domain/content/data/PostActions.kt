package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class PostActions(
    val canShare: Boolean = false,
    val canReply: Boolean = false,
    val canFavorite: Boolean = false,
    val canDislike: Boolean = false,
    val canBookmark: Boolean = false,
    val canReblog: Boolean = false,
    val canEdit: Boolean = false,
    val canDelete: Boolean = false,
    val canReport: Boolean = false,
    val canMute: Boolean = false,
    val canTogglePin: Boolean = false,
    val canBlock: Boolean = false,
    val canQuote: Boolean = false,
)
