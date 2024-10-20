package com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import kotlinx.coroutines.launch

class EventDetailViewModel(
    eventId: String,
    eventCache: LocalItemCache<EventModel>,
) : DefaultMviModel<EventDetailMviModel.Intent, EventDetailMviModel.State, EventDetailMviModel.Effect>(
        initialState = EventDetailMviModel.State(),
    ),
    EventDetailMviModel {
    init {
        screenModelScope.launch {
            val event = eventCache.get(eventId)
            updateState {
                it.copy(event = event)
            }
        }
    }
}
