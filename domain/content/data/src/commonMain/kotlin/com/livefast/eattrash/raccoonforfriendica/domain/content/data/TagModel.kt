package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class TagModel(
    val name: String,
    val url: String? = null,
    val following: Boolean? = null,
    val history: List<HashtagHistoryItem> = emptyList(),
)
