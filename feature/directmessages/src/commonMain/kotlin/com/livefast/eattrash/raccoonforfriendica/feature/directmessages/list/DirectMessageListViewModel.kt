package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ConversationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class DirectMessageListViewModel(
    private val paginationManager: DirectMessagesPaginationManager,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userPaginationManager: UserPaginationManager,
) : DefaultMviModel<DirectMessageListMviModel.Intent, DirectMessageListMviModel.State, DirectMessageListMviModel.Effect>(
        initialState = DirectMessageListMviModel.State(),
    ),
    DirectMessageListMviModel {
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
            uiState
                .map { it.userSearchQuery }
                .distinctUntilChanged()
                .drop(1)
                .debounce(750)
                .onEach { query ->
                    refreshUsers(query)
                }.launchIn(this)
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

            is DirectMessageListMviModel.Intent.UserSearchSetQuery ->
                screenModelScope.launch {
                    updateState { it.copy(userSearchQuery = intent.query) }
                }

            DirectMessageListMviModel.Intent.UserSearchClear ->
                screenModelScope.launch {
                    updateState { it.copy(userSearchUsers = emptyList()) }
                }

            DirectMessageListMviModel.Intent.UserSearchLoadNextPage ->
                screenModelScope.launch {
                    loadNextPageUsers()
                }

            is DirectMessageListMviModel.Intent.MarkConversationAsRead ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            items =
                                it.items.mapIndexed { idx, conversation ->
                                    if (idx == intent.index) {
                                        conversation.copy(unreadCount = 0)
                                    } else {
                                        conversation
                                    }
                                },
                        )
                    }
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
                            unreadCount = messages.count { m -> !m.read },
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

    private suspend fun refreshUsers(query: String) {
        userPaginationManager.reset(
            UserPaginationSpecification.SearchFollowing(
                query = query,
                withRelationship = false,
            ),
        )
        updateState { it.copy(userSearchCanFetchMore = userPaginationManager.canFetchMore) }
        loadNextPageUsers()
    }

    private suspend fun loadNextPageUsers() {
        if (uiState.value.userSearchLoading) {
            return
        }

        updateState { it.copy(userSearchLoading = true) }
        val users = userPaginationManager.loadNextPage()
        updateState {
            it.copy(
                userSearchUsers = users,
                userSearchCanFetchMore = userPaginationManager.canFetchMore,
                userSearchLoading = false,
            )
        }
    }
}
