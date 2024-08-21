package com.livefast.eattrash.raccoonforfriendica.feature.circles.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.feature.circles.domain.CirclesCache
import kotlinx.coroutines.launch

class CircleDetailViewModel(
    private val id: String,
    private val circlesRepository: CirclesRepository,
    private val circlesCache: CirclesCache,
) : DefaultMviModel<CircleDetailMviModel.Intent, CircleDetailMviModel.State, CircleDetailMviModel.Effect>(
        initialState = CircleDetailMviModel.State(),
    ),
    CircleDetailMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: CircleDetailMviModel.Intent) {
        when (intent) {
            CircleDetailMviModel.Intent.Refresh ->
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
                loading = true,
            )
        }
        val value =
            if (initial) {
                val cached = circlesCache.get(id)
                cached ?: circlesRepository.get(id)?.also { circlesCache.put(id, it) }
            } else {
                circlesRepository.get(id)?.also { circlesCache.put(id, it) }
            }
        updateState {
            it.copy(
                initial = false,
                refreshing = false,
                loading = false,
                circle = value,
            )
        }
    }
}
