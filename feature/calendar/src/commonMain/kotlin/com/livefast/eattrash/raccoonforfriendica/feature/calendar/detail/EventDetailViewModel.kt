package com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AssistedInject
class EventDetailViewModel(
    @Assisted private val eventId: String,
    eventCache: LocalItemCache<EventModel>,
    private val settingsRepository: SettingsRepository,
) : ViewModel(),
    MviDelegate<EventDetailMvi.Intent, EventDetailMvi.State, EventDetailMvi.Effect>
    by DefaultMviDelegate(initialState = EventDetailMvi.State()),
    EventDetailMvi {

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

    companion object {
        fun getExtras(eventId: String) = CreationExtras {
            this[KEY_EVENT_ID] = eventId
        }
    }
}

private val KEY_EVENT_ID = CreationExtras.Key<String>()

@AssistedFactory
@ViewModelAssistedFactoryKey(EventDetailViewModel::class)
@ContributesIntoMap(AppScope::class)
interface EventDetailViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): EventDetailViewModel =
        create(extras[KEY_EVENT_ID] ?: error("eventId argument not found"))

    fun create(@Assisted eventId: String): EventDetailViewModel
}
