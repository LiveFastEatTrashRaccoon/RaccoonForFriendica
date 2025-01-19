package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class TranslatedTimelineEntryModel(
    val source: TimelineEntryModel,
    val target: TimelineEntryModel,
    val provider: String? = null,
)
