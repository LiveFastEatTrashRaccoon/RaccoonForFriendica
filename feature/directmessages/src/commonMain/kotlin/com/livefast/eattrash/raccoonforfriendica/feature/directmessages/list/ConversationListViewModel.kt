package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ConversationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
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
class ConversationListViewModel(
    private val paginationManager: DirectMessagesPaginationManager,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userPaginationManager: UserPaginationManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviModelDelegate<ConversationListMviModel.Intent, ConversationListMviModel.State, ConversationListMviModel.Effect>
    by DefaultMviModelDelegate(initialState = ConversationListMviModel.State()),
    ConversationListMviModel {
    init {
        viewModelScope.launch {
            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
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

    override fun reduce(intent: ConversationListMviModel.Intent) {
        when (intent) {
            ConversationListMviModel.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }

            ConversationListMviModel.Intent.LoadNextPage ->
                viewModelScope.launch {
                    loadNextPage()
                }

            is ConversationListMviModel.Intent.UserSearchSetQuery ->
                viewModelScope.launch {
                    updateState { it.copy(userSearchQuery = intent.query) }
                }

            ConversationListMviModel.Intent.UserSearchClear ->
                viewModelScope.launch {
                    updateState { it.copy(userSearchUsers = emptyList()) }
                }

            ConversationListMviModel.Intent.UserSearchLoadNextPage ->
                viewModelScope.launch {
                    loadNextPageUsers()
                }

            is ConversationListMviModel.Intent.MarkConversationAsRead ->
                viewModelScope.launch {
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
        check(!uiState.value.loading) { return }

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
            emitEffect(ConversationListMviModel.Effect.BackToTop)
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
        check(!uiState.value.userSearchLoading) { return }

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
