package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class TagModel(
    val following: Boolean? = null,
    @Transient val followingPending: Boolean = false,
    val history: List<HashtagHistoryItem> = emptyList(),
    val name: String,
    val url: String? = null,
)
