package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class TimelineContextModel(
    val ancestors: List<PostModel> = emptyList(),
    val descendants: List<PostModel> = emptyList(),
)
