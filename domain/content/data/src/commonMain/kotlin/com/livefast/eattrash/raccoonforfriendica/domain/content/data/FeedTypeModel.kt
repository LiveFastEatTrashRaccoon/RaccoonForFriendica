package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class FeedTypeModel(
    val id: Long = 0,
    val accountId: Long = 0,
    val type: TimelineType = TimelineType.Local,
    val index: Int = 0,
    val info: String? = null,
)
