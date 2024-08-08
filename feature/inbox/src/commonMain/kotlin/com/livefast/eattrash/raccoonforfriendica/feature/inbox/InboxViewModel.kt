package com.livefast.eattrash.raccoonforfriendica.feature.inbox

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.NotificationsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.NotificationsPaginationSpecification
import kotlinx.coroutines.launch

class InboxViewModel(
    private val paginationManager: NotificationsPaginationManager,
) : DefaultMviModel<InboxMviModel.Intent, InboxMviModel.State, InboxMviModel.Effect>(
        initialState = InboxMviModel.State(),
    ),
    InboxMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: InboxMviModel.Intent) {
        when (intent) {
            InboxMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            InboxMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            NotificationsPaginationSpecification.Default(),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val notifications = paginationManager.loadNextPage()
        updateState {
            it.copy(
                notifications = notifications,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }
}
