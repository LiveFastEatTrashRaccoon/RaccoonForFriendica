package com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
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
import kotlinx.coroutines.yield
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory(binds = [CircleMembersMviModel::class])
@OptIn(FlowPreview::class)
class CircleMembersViewModel(
    @InjectedParam private val id: String,
    private val paginationManager: UserPaginationManager,
    private val circlesRepository: CirclesRepository,
    private val settingsRepository: SettingsRepository,
    private val searchPaginationManager: UserPaginationManager,
    private val imagePreloadManager: ImagePreloadManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : DefaultMviModel<CircleMembersMviModel.Intent, CircleMembersMviModel.State, CircleMembersMviModel.Effect>(
        initialState = CircleMembersMviModel.State(),
    ),
    CircleMembersMviModel {
    init {
        screenModelScope.launch {
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
                .map { it.searchUsersQuery }
                .distinctUntilChanged()
                .drop(1)
                .debounce(750)
                .onEach { query ->
                    if (uiState.value.addUsersDialogOpened) {
                        refreshSearchUsers(query)
                    }
                }.launchIn(this)
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: CircleMembersMviModel.Intent) {
        when (intent) {
            CircleMembersMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is CircleMembersMviModel.Intent.ToggleAddUsersDialog ->
                screenModelScope.launch {
                    if (intent.opened) {
                        refreshSearchUsers("")
                        updateState { it.copy(addUsersDialogOpened = true) }
                    } else {
                        updateState {
                            it.copy(
                                searchUsersQuery = "",
                                searchUsers = emptyList(),
                                addUsersDialogOpened = false,
                            )
                        }
                    }
                }

            is CircleMembersMviModel.Intent.SetSearchUserQuery ->
                screenModelScope.launch {
                    updateState { it.copy(searchUsersQuery = intent.text) }
                }

            CircleMembersMviModel.Intent.UserSearchLoadNextPage ->
                screenModelScope.launch {
                    loadNextPageSearchUsers()
                }

            is CircleMembersMviModel.Intent.Add -> add(intent.users)
            is CircleMembersMviModel.Intent.Remove -> remove(intent.userId)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(UserPaginationSpecification.CircleMembers(id))
        val circle = circlesRepository.get(id = id)
        updateState { it.copy(circle = circle) }

        updateState { it.copy(loading = true) }
        while (paginationManager.canFetchMore) {
            val users = paginationManager.loadNextPage()
            users.preloadImages()
            updateState {
                it.copy(
                    users = users,
                    initial = false,
                    refreshing = false,
                )
            }
            yield()
        }
        updateState {
            it.copy(loading = false)
        }
    }

    private fun List<UserModel>.preloadImages() {
        mapNotNull { user ->
            user.avatar?.takeIf { it.isNotEmpty() }
        }.onEach { url ->
            imagePreloadManager.preload(url)
        }
    }

    private suspend fun removeItemFromState(id: String) {
        updateState {
            it.copy(
                users = it.users.filter { item -> item.id != id },
            )
        }
    }

    private suspend fun insertItemsInState(items: List<UserModel>) {
        updateState {
            it.copy(
                users = it.users + items,
            )
        }
    }

    private suspend fun refreshSearchUsers(query: String) {
        searchPaginationManager.reset(
            UserPaginationSpecification.SearchFollowing(
                query = query,
                withRelationship = false,
                // exclude members of the current circle
                excludeIds = uiState.value.users.map { it.id },
            ),
        )
        updateState { it.copy(userSearchCanFetchMore = searchPaginationManager.canFetchMore) }
        loadNextPageSearchUsers()
    }

    private suspend fun loadNextPageSearchUsers() {
        if (uiState.value.userSearchLoading) {
            return
        }

        updateState { it.copy(userSearchLoading = true) }
        val users = searchPaginationManager.loadNextPage()
        updateState {
            it.copy(
                searchUsers = users,
                userSearchCanFetchMore = searchPaginationManager.canFetchMore,
                userSearchLoading = false,
            )
        }
    }

    private fun add(users: List<UserModel>) {
        screenModelScope.launch {
            val userIds = users.map { it.id }
            val success = circlesRepository.addMembers(id = id, userIds = userIds)
            if (success) {
                insertItemsInState(users)
            } else {
                emitEffect(CircleMembersMviModel.Effect.Failure)
            }
        }
    }

    private fun remove(userId: String) {
        screenModelScope.launch {
            val success = circlesRepository.removeMembers(id = id, userIds = listOf(userId))
            if (success) {
                removeItemFromState(userId)
            } else {
                emitEffect(CircleMembersMviModel.Effect.Failure)
            }
        }
    }
}
