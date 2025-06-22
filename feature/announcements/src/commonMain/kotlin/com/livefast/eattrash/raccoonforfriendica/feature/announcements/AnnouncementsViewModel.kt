package com.livefast.eattrash.raccoonforfriendica.feature.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AnnouncementModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementsManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AnnouncementsViewModel(
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val announcementRepository: AnnouncementRepository,
    private val emojiRepository: EmojiRepository,
    private val announcementsManager: AnnouncementsManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviModelDelegate<AnnouncementsMviModel.Intent, AnnouncementsMviModel.State, AnnouncementsMviModel.Effect>
    by DefaultMviModelDelegate(initialState = AnnouncementsMviModel.State()),
    AnnouncementsMviModel {
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

            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState {
                        it.copy(currentUserId = currentUser?.id)
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

            val customEmojis = emojiRepository.getAll().orEmpty()
            updateState {
                it.copy(availableEmojis = customEmojis)
            }

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: AnnouncementsMviModel.Intent) {
        when (intent) {
            AnnouncementsMviModel.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }

            is AnnouncementsMviModel.Intent.AddReaction ->
                addReaction(
                    id = intent.id,
                    name = intent.name,
                )

            is AnnouncementsMviModel.Intent.RemoveReaction ->
                removeReaction(
                    id = intent.id,
                    name = intent.name,
                )
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        if (!initial) {
            markAllAsRead()
        }

        val items = announcementRepository.getAll(refresh = !initial).orEmpty()
        updateState {
            it.copy(
                items = items,
                initial = false,
                refreshing = false,
            )
        }
        if (initial) {
            emitEffect(AnnouncementsMviModel.Effect.BackToTop)
        }
    }

    private suspend fun updateItemInState(id: String, block: (AnnouncementModel) -> AnnouncementModel) {
        updateState {
            it.copy(
                items =
                it.items.map { item ->
                    if (item.id == id) {
                        item.let(block)
                    } else {
                        item
                    }
                },
            )
        }
    }

    private suspend fun markAllAsRead() {
        val items = uiState.value.items.filter { !it.read }
        items.forEach { item ->
            val success = announcementRepository.markAsRead(item.id)
            if (success) {
                updateItemInState(item.id) { it.copy(read = true) }
                announcementsManager.decrementUnreadCount()
            }
        }
    }

    private suspend fun setReactionLoading(id: String, name: String, loading: Boolean) {
        val newItems =
            uiState.value.items.map {
                if (it.id == id) {
                    val reactions =
                        it.reactions.map { r ->
                            if (r.name == name) {
                                r.copy(loading = loading)
                            } else {
                                r
                            }
                        }
                    it.copy(
                        reactions = reactions,
                    )
                } else {
                    it
                }
            }
        updateState { it.copy(items = newItems) }
    }

    private suspend fun refreshItem(id: String) {
        val newItem =
            announcementRepository.getAll(refresh = true)?.firstOrNull { it.id == id } ?: return
        val newItems =
            uiState.value.items.map {
                if (it.id == id) {
                    newItem
                } else {
                    it
                }
            }
        updateState { it.copy(items = newItems) }
    }

    private fun addReaction(id: String, name: String) {
        viewModelScope.launch {
            setReactionLoading(id = id, name = name, loading = true)
            val success = announcementRepository.addReaction(id = id, reaction = name)
            if (success) {
                refreshItem(id)
            } else {
                setReactionLoading(id = id, name = name, loading = false)
            }
        }
    }

    private fun removeReaction(id: String, name: String) {
        viewModelScope.launch {
            setReactionLoading(id = id, name = name, loading = true)
            val success = announcementRepository.removeReaction(id = id, reaction = name)
            if (success) {
                refreshItem(id)
            } else {
                setReactionLoading(id = id, name = name, loading = false)
            }
        }
    }
}
