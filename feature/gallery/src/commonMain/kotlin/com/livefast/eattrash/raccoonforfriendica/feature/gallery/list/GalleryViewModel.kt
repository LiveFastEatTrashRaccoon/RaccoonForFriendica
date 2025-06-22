package com.livefast.eattrash.raccoonforfriendica.feature.gallery.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.AlbumsUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val albumRepository: PhotoAlbumRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : ViewModel(),
    MviModelDelegate<GalleryMviModel.Intent, GalleryMviModel.State, GalleryMviModel.Effect>
    by DefaultMviModelDelegate(initialState = GalleryMviModel.State()),
    GalleryMviModel {
    init {
        viewModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            notificationCenter
                .subscribe(AlbumsUpdatedEvent::class)
                .onEach {
                    refresh()
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: GalleryMviModel.Intent) {
        when (intent) {
            GalleryMviModel.Intent.Refresh -> viewModelScope.launch { refresh() }
            GalleryMviModel.Intent.LoadNextPage -> viewModelScope.launch { loadNextPage() }
            is GalleryMviModel.Intent.UpdateAlbum -> updateAlbum(intent.oldName, intent.newName)
            is GalleryMviModel.Intent.DeleteAlbum -> deleteAlbum(intent.name)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        check(!uiState.value.loading) { return }
        updateState { it.copy(loading = true) }
        val items = albumRepository.getAll().orEmpty()
        val wasRefreshing = uiState.value.refreshing
        updateState {
            it.copy(
                items = items,
                canFetchMore = false,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
        if (wasRefreshing) {
            emitEffect(GalleryMviModel.Effect.BackToTop)
        }
    }

    private suspend fun removeItemFromState(name: String) {
        updateState { it.copy(items = it.items.filter { e -> e.name != name }) }
    }

    private suspend fun updateItemInState(name: String, block: (MediaAlbumModel) -> MediaAlbumModel) {
        updateState {
            it.copy(
                items =
                it.items.map { album ->
                    if (album.name == name) {
                        album.let(block)
                    } else {
                        album
                    }
                },
            )
        }
    }

    private fun updateAlbum(oldName: String, newName: String) {
        viewModelScope.launch {
            updateState { it.copy(operationInProgress = true) }
            val res =
                albumRepository.update(
                    oldName = oldName,
                    newName = newName,
                )
            updateState { it.copy(operationInProgress = false) }
            if (res) {
                updateItemInState(oldName) { it.copy(name = newName) }
            } else {
                emitEffect(GalleryMviModel.Effect.Failure)
            }
        }
    }

    private fun deleteAlbum(name: String) {
        viewModelScope.launch {
            updateState { it.copy(operationInProgress = true) }
            val res = albumRepository.delete(name)
            updateState { it.copy(operationInProgress = false) }
            if (res) {
                removeItemFromState(name)
            } else {
                emitEffect(GalleryMviModel.Effect.Failure)
            }
        }
    }
}
