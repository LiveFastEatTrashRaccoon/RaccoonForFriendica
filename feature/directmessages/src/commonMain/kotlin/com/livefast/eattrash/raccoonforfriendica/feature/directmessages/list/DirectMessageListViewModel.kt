package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ConversationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DirectMessageListViewModel(
    private val paginationManager: DirectMessagesPaginationManager,
    private val identityRepository: IdentityRepository,
) : DefaultMviModel<DirectMessageListMviModel.Intent, DirectMessageListMviModel.State, DirectMessageListMviModel.Effect>(
        initialState = DirectMessageListMviModel.State(),
    ),
    DirectMessageListMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
                    refresh(initial = true)
                }.launchIn(this)
        }
    }

    override fun reduce(intent: DirectMessageListMviModel.Intent) {
        when (intent) {
            DirectMessageListMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            DirectMessageListMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            DirectMessagesPaginationSpecification.All,
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val currentUserId = uiState.value.currentUserId
        val wasRefreshing = uiState.value.refreshing
        val items =
            paginationManager
                .loadNextPage()
                .groupBy {
                    it.parentUri
                }.mapNotNull { (_, messages) ->
                    val lastMessage =
                        messages.takeIf { it.isNotEmpty() }?.maxBy { it.created.orEmpty() }
                    val user =
                        lastMessage?.sender?.takeIf { it.id != currentUserId }
                            ?: lastMessage?.recipient
                    if (user == null || lastMessage == null) {
                        null
                    } else {
                        ConversationModel(
                            otherUser = user,
                            lastMessage = lastMessage,
                            messageCount = messages.size,
                        )
                    }
                }
        updateState {
            it.copy(
                items = items,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
        if (wasRefreshing) {
            emitEffect(DirectMessageListMviModel.Effect.BackToTop)
        }
    }
}
