package com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
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
) : ViewModel(),
    MviModelDelegate<EventDetailMviModel.Intent, EventDetailMviModel.State, EventDetailMviModel.Effect>
    by DefaultMviModelDelegate(initialState = EventDetailMviModel.State()),
    EventDetailMviModel {
    init {
        viewModelScope.launch {
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
