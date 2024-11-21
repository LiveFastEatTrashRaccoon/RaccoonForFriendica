package com.livefast.eattrash.raccoonforfriendica.feature.announcements

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AnnouncementModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementsManager
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
    private val announcementsManager: AnnouncementsManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : DefaultMviModel<AnnouncementsMviModel.Intent, AnnouncementsMviModel.State, AnnouncementsMviModel.Effect>(
        initialState = AnnouncementsMviModel.State(),
    ),
    AnnouncementsMviModel {
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

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: AnnouncementsMviModel.Intent) {
        when (intent) {
            AnnouncementsMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }
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

    private suspend fun updateItemInState(
        id: String,
        block: (AnnouncementModel) -> AnnouncementModel,
    ) {
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
}
