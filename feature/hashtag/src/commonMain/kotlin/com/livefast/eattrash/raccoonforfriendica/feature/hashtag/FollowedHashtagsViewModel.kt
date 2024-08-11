package com.livefast.eattrash.raccoonforfriendica.feature.hashtag

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FollowedHashtagsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import kotlinx.coroutines.launch

class FollowedHashtagsViewModel(
    private val paginationManager: FollowedHashtagsPaginationManager,
    private val tagRepository: TagRepository,
) : DefaultMviModel<FollowedHashtagsMviModel.Intent, FollowedHashtagsMviModel.State, FollowedHashtagsMviModel.Effect>(
        initialState = FollowedHashtagsMviModel.State(),
    ),
    FollowedHashtagsMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: FollowedHashtagsMviModel.Intent) {
        when (intent) {
            FollowedHashtagsMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            FollowedHashtagsMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            is FollowedHashtagsMviModel.Intent.ToggleTagFollow ->
                toggleTagFollow(
                    intent.name,
                    intent.newValue,
                )
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
        name: String,
        block: (TagModel) -> TagModel,
    ) {
        updateState {
            it.copy(
                items =
                    it.items.map { tag ->
                        if (tag.name == name) {
                            tag.let(block)
                        } else {
                            tag
                        }
                    },
            )
        }
    }

    private suspend fun removeItemFromState(name: String) {
        updateState {
            it.copy(
                items =
                    it.items.filter { tag -> tag.name != name },
            )
        }
    }

    private fun toggleTagFollow(
        name: String,
        follow: Boolean,
    ) {
        screenModelScope.launch {
            val newTag =
                if (!follow) {
                    tagRepository.unfollow(name)
                } else {
                    tagRepository.follow(name)
                }
            if (newTag != null) {
                if (!follow) {
                    removeItemFromState(name)
                } else {
                    updateItemInState(name) { newTag }
                }
            }
        }
    }
}
