package com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel

interface EventDetailMvi : Mvi<EventDetailMvi.Intent, EventDetailMvi.State, EventDetailMvi.Effect> {
    sealed interface Intent

    data class State(val event: EventModel? = null, val hideNavigationBarWhileScrolling: Boolean = true)

    sealed interface Effect
}
