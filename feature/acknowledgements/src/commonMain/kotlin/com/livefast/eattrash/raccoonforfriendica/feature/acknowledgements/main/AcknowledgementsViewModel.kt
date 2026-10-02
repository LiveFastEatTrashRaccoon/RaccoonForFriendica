package com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.repository.AcknowledgementsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class AcknowledgementsViewModel(private val acknowledgementsRepository: AcknowledgementsRepository) :
    ViewModel(),
    MviDelegate<AcknowledgementsMvi.Intent, AcknowledgementsMvi.State, AcknowledgementsMvi.Effect>
    by DefaultMviDelegate(initialState = AcknowledgementsMvi.State()),
    AcknowledgementsMvi {
    init {
        viewModelScope.launch {
            refresh(initial = true)
        }
    }

    override fun reduce(intent: AcknowledgementsMvi.Intent) {
        when (intent) {
            AcknowledgementsMvi.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(
                initial = initial,
                refreshing = !initial,
            )
        }
        val items = acknowledgementsRepository.getAll()
        updateState {
            it.copy(
                items = items.orEmpty(),
                initial = false,
                refreshing = false,
            )
        }
    }
}
