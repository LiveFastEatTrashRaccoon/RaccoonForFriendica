package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface EventType {
    data object Event : EventType

    data object Birthday : EventType
}

data class EventModel(
    val id: String,
    val uri: String,
    val title: String,
    val description: String? = null,
    val startTime: String,
    val endTime: String? = null,
    val type: EventType = EventType.Event,
    val ongoing: Boolean = false,
    val place: String? = null,
)
