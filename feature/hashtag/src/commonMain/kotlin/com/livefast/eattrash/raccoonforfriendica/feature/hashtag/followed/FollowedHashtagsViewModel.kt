package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TagUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FollowedHashtagsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FollowedHashtagCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FollowedHashtagsViewModel(
    private val cache: FollowedHashtagCache,
    private val paginationManager: FollowedHashtagsPaginationManager,
    private val tagRepository: TagRepository,
    private val settingsRepository: SettingsRepository,
    private val hapticFeedback: HapticFeedback,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<FollowedHashtagsMviModel.Intent, FollowedHashtagsMviModel.State, FollowedHashtagsMviModel.Effect>(
    initialState = FollowedHashtagsMviModel.State(),
),
    FollowedHashtagsMviModel {
    init {
        screenModelScope.launch {
            notificationCenter
                .subscribe(TagUpdatedEvent::class)
                .onEach { event ->
                    updateItemInState(event.tag.name) { event.tag }
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
                val items = cache.getAll()
                if (items.isNotEmpty()) {
                    refresh(initial = true)
                } else {
                    updateState {
                        it.copy(
                            items = items,
                            initial = false,
                        )
                    }
                }
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
        check(!uiState.value.loading) { return }

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

    private suspend fun updateItemInState(name: String, block: (TagModel) -> TagModel) {
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

    private fun toggleTagFollow(name: String, follow: Boolean) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateItemInState(name) { it.copy(followingPending = true) }
            val newTag =
                if (!follow) {
                    tagRepository.unfollow(name)
                } else {
                    tagRepository.follow(name)
                }?.also {
                    notificationCenter.send(TagUpdatedEvent(tag = it))
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
