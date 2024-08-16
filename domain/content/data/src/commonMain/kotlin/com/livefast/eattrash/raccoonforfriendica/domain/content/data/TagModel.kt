package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class TagModel(
    val name: String,
    val url: String? = null,
    val following: Boolean? = null,
    @Transient
    val followingPending: Boolean = false,
    val history: List<HashtagHistoryItem> = emptyList(),
)
