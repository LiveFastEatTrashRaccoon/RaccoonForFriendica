package com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.AlbumsUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val albumName: String,
    private val paginationManager: AlbumPhotoPaginationManager,
    private val photoRepository: PhotoRepository,
    private val albumRepository: PhotoAlbumRepository,
    private val settingsRepository: SettingsRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<AlbumDetailMviModel.Intent, AlbumDetailMviModel.State, AlbumDetailMviModel.Effect>(
        initialState = AlbumDetailMviModel.State(),
    ),
    AlbumDetailMviModel {
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
                val albums =
                    albumRepository
                        .getAll()
                        ?.distinctBy { it.name }
                        ?.filter { it.name != albumName }
                        .orEmpty()
                updateState { it.copy(albums = albums) }

                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: AlbumDetailMviModel.Intent) {
        when (intent) {
            AlbumDetailMviModel.Intent.Refresh -> screenModelScope.launch { refresh() }
            AlbumDetailMviModel.Intent.LoadNextPage -> screenModelScope.launch { loadNextPage() }
            is AlbumDetailMviModel.Intent.Create -> upload(intent.byteArray)
            is AlbumDetailMviModel.Intent.EditDescription ->
                updateDescription(attachment = intent.attachment, description = intent.description)

            is AlbumDetailMviModel.Intent.Move ->
                moveToOtherAlbum(attachment = intent.attachment, otherAlbum = intent.album)

            is AlbumDetailMviModel.Intent.Delete -> delete(intent.id)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(AlbumPhotoPaginationSpecification.Default(album = albumName))
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        check(!uiState.value.loading) { return }
        updateState { it.copy(loading = true) }

        val wasRefreshing = uiState.value.refreshing
        val items = paginationManager.loadNextPage().filter { it.thumbnail != null }
        updateState {
            it.copy(
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
                items = items,
            )
        }
        if (wasRefreshing) {
            emitEffect(AlbumDetailMviModel.Effect.BackToTop)
        }
    }

    private suspend fun updateItemInState(
        id: String,
        block: (AttachmentModel) -> AttachmentModel,
    ) {
        updateState {
            it.copy(
                items =
                    it.items.map { attachment ->
                        if (attachment.id == id) {
                            attachment.let(block)
                        } else {
                            attachment
                        }
                    },
            )
        }
    }

    private suspend fun removeItemFromState(id: String) {
        updateState {
            it.copy(items = it.items.filter { attachment -> attachment.id != id })
        }
    }

    private fun upload(byteArray: ByteArray) {
        screenModelScope.launch {
            val localId = getUuid()
            val originalItems = uiState.value.items
            val newItems =
                buildList {
                    this +=
                        AttachmentModel(
                            id = localId,
                            url = "",
                            loading = true,
                            album = albumName,
                        )
                    addAll(originalItems)
                }
            updateState { it.copy(items = newItems) }
            val attachment =
                photoRepository.create(
                    bytes = byteArray,
                    album = albumName,
                )
            if (attachment != null) {
                updateItemInState(localId) { attachment }
            } else {
                removeItemFromState(localId)
                emitEffect(AlbumDetailMviModel.Effect.Failure)
            }
        }
    }

    private fun updateDescription(
        attachment: AttachmentModel,
        description: String,
    ) {
        screenModelScope.launch {
            updateState { it.copy(operationInProgress = true) }
            val successful =
                photoRepository.update(
                    id = attachment.id,
                    album = attachment.album.orEmpty(),
                    alt = description,
                )
            updateState { it.copy(operationInProgress = false) }
            if (successful) {
                updateItemInState(attachment.id) {
                    it.copy(description = description)
                }
            } else {
                emitEffect(AlbumDetailMviModel.Effect.Failure)
            }
        }
    }

    private fun moveToOtherAlbum(
        attachment: AttachmentModel,
        otherAlbum: String,
    ) {
        check(otherAlbum != albumName) { return }
        screenModelScope.launch {
            updateState { it.copy(operationInProgress = true) }
            val successful =
                photoRepository.update(
                    id = attachment.id,
                    album = attachment.album.orEmpty(),
                    newAlbum = otherAlbum,
                    alt = attachment.description.orEmpty(),
                )
            updateState { it.copy(operationInProgress = false) }
            if (successful) {
                removeItemFromState(attachment.id)
                notificationCenter.send(AlbumsUpdatedEvent)
            } else {
                emitEffect(AlbumDetailMviModel.Effect.Failure)
            }
        }
    }

    private fun delete(id: String) {
        screenModelScope.launch {
            updateState { it.copy(operationInProgress = true) }
            val success = photoRepository.delete(id)
            updateState { it.copy(operationInProgress = false) }
            if (success) {
                removeItemFromState(id)
            } else {
                emitEffect(AlbumDetailMviModel.Effect.Failure)
            }
        }
    }
}
