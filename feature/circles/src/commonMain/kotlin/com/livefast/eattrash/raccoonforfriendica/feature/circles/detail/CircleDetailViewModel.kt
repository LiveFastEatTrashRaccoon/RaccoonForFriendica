package com.livefast.eattrash.raccoonforfriendica.feature.circles.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import kotlinx.coroutines.launch

class CircleDetailViewModel(
    private val id: String,
    private val paginationManager: UserPaginationManager,
    private val circlesRepository: CirclesRepository,
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
            CircleDetailMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(UserPaginationSpecification.CircleMembers(id))
        val circle = circlesRepository.get(id = id)
        updateState { it.copy(circle = circle) }
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val users = paginationManager.loadNextPage()
        updateState {
            it.copy(
                users = users,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }
}
