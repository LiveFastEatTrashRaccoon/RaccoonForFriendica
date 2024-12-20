package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.ManageBlocksSection
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ManageBlocksViewModel(
    private val paginationManager: UserPaginationManager,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val imagePreloadManager: ImagePreloadManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : DefaultMviModel<ManageBlocksMviModel.Intent, ManageBlocksMviModel.State, ManageBlocksMviModel.Effect>(
        initialState = ManageBlocksMviModel.State(),
    ),
    ManageBlocksMviModel {
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

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ManageBlocksMviModel.Intent) {
        when (intent) {
            is ManageBlocksMviModel.Intent.ChangeSection ->
                screenModelScope.launch {
                    if (uiState.value.loading) {
                        return@launch
                    }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(ManageBlocksMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }
            ManageBlocksMviModel.Intent.LoadNextPage -> screenModelScope.launch { loadNextPage() }
            ManageBlocksMviModel.Intent.Refresh -> screenModelScope.launch { refresh() }
            is ManageBlocksMviModel.Intent.ToggleMute -> unmute(intent.userId)
            is ManageBlocksMviModel.Intent.ToggleBlock -> unblock(intent.userId)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            when (uiState.value.section) {
                ManageBlocksSection.Muted -> UserPaginationSpecification.Muted
                ManageBlocksSection.Blocked -> UserPaginationSpecification.Blocked
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
        users.preloadImages()
        updateState {
            it.copy(
                items = users,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }

    private fun List<UserModel>.preloadImages() {
        mapNotNull { user ->
            user.avatar?.takeIf { it.isNotEmpty() }
        }.onEach { url ->
            imagePreloadManager.preload(url)
        }
    }

    private suspend fun removeUserFromState(userId: String) {
        updateState {
            it.copy(
                items = it.items.filter { e -> e.id != userId },
            )
        }
    }

    private fun unmute(userId: String) {
        screenModelScope.launch {
            val res = userRepository.unmute(userId)
            if (res != null) {
                removeUserFromState(userId)
            }
        }
    }

    private fun unblock(userId: String) {
        screenModelScope.launch {
            val res = userRepository.unblock(userId)
            if (res != null) {
                removeUserFromState(userId)
            }
        }
    }
}
