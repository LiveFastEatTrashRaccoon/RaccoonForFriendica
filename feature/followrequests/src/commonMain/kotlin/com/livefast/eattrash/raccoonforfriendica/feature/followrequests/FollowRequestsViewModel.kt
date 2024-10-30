package com.livefast.eattrash.raccoonforfriendica.feature.followrequests

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FollowRequestPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FollowRequestsViewModel(
    private val paginationManager: FollowRequestPaginationManager,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
) : DefaultMviModel<FollowRequestsMviModel.Intent, FollowRequestsMviModel.State, FollowRequestsMviModel.Effect>(
        initialState = FollowRequestsMviModel.State(),
    ),
    FollowRequestsMviModel {
    init {
        screenModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            autoloadImages = settings?.autoloadImages ?: true,
                        )
                    }
                }.launchIn(this)
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: FollowRequestsMviModel.Intent) {
        when (intent) {
            FollowRequestsMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            FollowRequestsMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            is FollowRequestsMviModel.Intent.Accept -> accept(intent.id)
            is FollowRequestsMviModel.Intent.Reject -> reject(intent.id)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset()
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val entries = paginationManager.loadNextPage()
        updateState {
            it.copy(
                items = entries,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }

    private suspend fun updateItemInState(
        id: String,
        block: (UserModel) -> UserModel,
    ) {
        updateState {
            it.copy(
                items =
                    it.items.map { user ->
                        if (user.id == id) {
                            user.let(block)
                        } else {
                            user
                        }
                    },
            )
        }
    }

    private suspend fun removeItemFromState(id: String) {
        updateState {
            it.copy(
                items =
                    it.items.filter { e -> e.id != id },
            )
        }
    }

    private fun accept(id: String) {
        screenModelScope.launch {
            updateItemInState(id) { it.copy(relationshipStatusPending = true) }
            val success = userRepository.acceptFollowRequest(id)
            if (success) {
                removeItemFromState(id)
            } else {
                updateItemInState(id) { it.copy(relationshipStatusPending = false) }
                emitEffect(FollowRequestsMviModel.Effect.Failure)
            }
        }
    }

    private fun reject(id: String) {
        screenModelScope.launch {
            updateItemInState(id) { it.copy(relationshipStatusPending = true) }
            val success = userRepository.rejectFollowRequest(id)
            if (success) {
                removeItemFromState(id)
            } else {
                updateItemInState(id) { it.copy(relationshipStatusPending = false) }
                emitEffect(FollowRequestsMviModel.Effect.Failure)
            }
        }
    }
}
