package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.main

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository.AcknowledgementsRepository
import kotlinx.coroutines.launch

internal class AcknowledgementsViewModel(private val acknowledgementsRepository: AcknowledgementsRepository) :
    DefaultMviModel<AcknowledgementsMviModel.Intent, AcknowledgementsMviModel.State, AcknowledgementsMviModel.Effect>(
        initialState = AcknowledgementsMviModel.State(),
    ),
    AcknowledgementsMviModel {
    init {
        screenModelScope.launch {
            refresh(initial = true)
        }
    }

    override fun reduce(intent: AcknowledgementsMviModel.Intent) {
        when (intent) {
            AcknowledgementsMviModel.Intent.Refresh ->
                screenModelScope.launch {
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
