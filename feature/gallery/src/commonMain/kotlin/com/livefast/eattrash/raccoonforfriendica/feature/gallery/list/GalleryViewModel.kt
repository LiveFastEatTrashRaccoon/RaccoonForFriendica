package com.livefast.eattrash.raccoonforfriendica.feature.gallery.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.AlbumsUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class GalleryViewModel(
    private val albumRepository: PhotoAlbumRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<GalleryMvi.Intent, GalleryMvi.State, GalleryMvi.Effect>
    by DefaultMviDelegate(initialState = GalleryMvi.State()),
    GalleryMvi {
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
                    viewModelScope.launch {
                        refresh()
                    }
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: GalleryMvi.Intent) {
        when (intent) {
            GalleryMvi.Intent.Refresh -> viewModelScope.launch { refresh() }
            GalleryMvi.Intent.LoadNextPage -> viewModelScope.launch { loadNextPage() }
            is GalleryMvi.Intent.UpdateAlbum -> updateAlbum(intent.oldName, intent.newName)
            is GalleryMvi.Intent.DeleteAlbum -> deleteAlbum(intent.name)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
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
                emitEffect(GalleryMvi.Effect.BackToTop)
            }
        } catch (e: Exception) {
            updateState { it.copy(loading = false, refreshing = false) }
            if (e is CancellationException) throw e
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
                emitEffect(GalleryMvi.Effect.Failure)
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
                emitEffect(GalleryMvi.Effect.Failure)
            }
        }
    }
}
