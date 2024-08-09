package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class TagModel(
    val name: String,
    val url: String? = null,
    val history: List<HashtagHistoryItem> = emptyList(),
)
