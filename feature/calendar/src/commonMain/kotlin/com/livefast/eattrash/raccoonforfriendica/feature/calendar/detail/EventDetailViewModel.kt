package com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class EventDetailViewModel(
    eventId: String,
    eventCache: LocalItemCache<EventModel>,
    private val settingsRepository: SettingsRepository,
) : DefaultMviModel<EventDetailMviModel.Intent, EventDetailMviModel.State, EventDetailMviModel.Effect>(
    initialState = EventDetailMviModel.State(),
),
    EventDetailMviModel {
    init {
        screenModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            val event = eventCache.get(eventId)
            updateState {
                it.copy(event = event)
            }
        }
    }
}
