package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import kotlinx.coroutines.launch

class CirclesViewModel(
    private val circlesRepository: CirclesRepository,
) : DefaultMviModel<CirclesMviModel.Intent, CirclesMviModel.State, CirclesMviModel.Effect>(
        initialState = CirclesMviModel.State(),
    ),
    CirclesMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: CirclesMviModel.Intent) {
        when (intent) {
            CirclesMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val items = circlesRepository.getAll()
        updateState {
            it.copy(
                initial = false,
                refreshing = false,
                items = items,
            )
        }
    }
}
