package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class TimelineContextModel(
    val ancestors: List<TimelineEntryModel> = emptyList(),
    val descendants: List<TimelineEntryModel> = emptyList(),
)
