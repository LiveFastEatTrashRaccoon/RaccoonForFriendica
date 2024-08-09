package com.livefast.eattrash.raccoonforfriendica.feature.userlist

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import kotlinx.coroutines.launch

internal class UserListViewModel(
    private val userId: String,
    private val type: UserListType,
    private val paginationManager: UserPaginationManager,
    private val userRepository: UserRepository,
) : DefaultMviModel<UserListMviModel.Intent, UserListMviModel.State, UserListMviModel.Effect>(
        initialState = UserListMviModel.State(),
    ),
    UserListMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                loadUser()
                refresh(initial = true)
            }
        }
    }

    private suspend fun loadUser() {
        val user = userRepository.getById(id = userId)
        updateState {
            it.copy(user = user)
        }
    }

    override fun reduce(intent: UserListMviModel.Intent) {
        when (intent) {
            UserListMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            UserListMviModel.Intent.Refresh ->
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
            when (type) {
                UserListType.Follower -> UserPaginationSpecification.Follower(userId)
                UserListType.Following -> UserPaginationSpecification.Following(userId)
            },
        )
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
