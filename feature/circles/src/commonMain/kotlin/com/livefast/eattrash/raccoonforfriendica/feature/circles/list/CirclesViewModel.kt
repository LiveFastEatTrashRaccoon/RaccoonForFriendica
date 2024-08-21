package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.feature.circles.domain.CirclesCache
import kotlinx.coroutines.launch

class CirclesViewModel(
    private val circlesRepository: CirclesRepository,
    private val circlesCache: CirclesCache,
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
        val items =
            if (initial) {
                val cached = circlesCache.get()
                cached ?: circlesRepository.getAll().also { circlesCache.put(it) }
            } else {
                circlesRepository.getAll().also { circlesCache.put(it) }
            }
        updateState {
            it.copy(
                initial = false,
                refreshing = false,
                items = items,
            )
        }
    }
}
