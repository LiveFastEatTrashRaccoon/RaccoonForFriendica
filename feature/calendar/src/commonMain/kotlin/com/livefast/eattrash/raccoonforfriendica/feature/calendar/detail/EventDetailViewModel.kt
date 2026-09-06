package com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailViewModel.Companion.KEY_ARGS
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
    @Assisted args: EventDetailViewModelArgs,
    eventCache: LocalItemCache<EventModel>,
    private val settingsRepository: SettingsRepository,
) : ViewModel(),
    MviModelDelegate<EventDetailMviModel.Intent, EventDetailMviModel.State, EventDetailMviModel.Effect>
    by DefaultMviModelDelegate(initialState = EventDetailMviModel.State()),
    EventDetailMviModel {

    private val eventId = args.id

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
        val KEY_ARGS = CreationExtras.Key<EventDetailViewModelArgs>()
    }
}

data class EventDetailViewModelArgs(val id: String)

@AssistedFactory
@ViewModelAssistedFactoryKey(EventDetailViewModel::class)
@ContributesIntoMap(AppScope::class)
interface EventDetailViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): EventDetailViewModel =
        create(extras[KEY_ARGS] ?: error("ViewModel creation args not found"))

    fun create(@Assisted args: EventDetailViewModelArgs): EventDetailViewModel
}
