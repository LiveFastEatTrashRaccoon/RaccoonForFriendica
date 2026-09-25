package com.livefast.eattrash.raccoonforfriendica.feature.composer.gallerypicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull
import kotlin.collections.orEmpty

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class GalleryPickerViewModel(
    private val albumRepository: PhotoAlbumRepository,
    private val albumPhotoPaginationManager: AlbumPhotoPaginationManager,
) : ViewModel(),
    MviModelDelegate<GalleryPickerMviModel.Intent, GalleryPickerMviModel.State, GalleryPickerMviModel.Effect>
    by DefaultMviModelDelegate(initialState = GalleryPickerMviModel.State()),
    GalleryPickerMviModel {

    override fun reduce(intent: GalleryPickerMviModel.Intent) {
        when (intent) {
            is GalleryPickerMviModel.Intent.SelectAlbum ->
                viewModelScope.launch {
                    updateState { it.copy(currentAlbum = intent.album) }
                    refreshGalleryPhotos()
                }

            GalleryPickerMviModel.Intent.InitialLoad ->
                viewModelScope.launch {
                    val albums = albumRepository.getAll().orEmpty()
                    val currentAlbum = albums.firstOrNull()
                    updateState {
                        it.copy(
                            albums = albums,
                            currentAlbum = currentAlbum?.name,
                        )
                    }
                    refreshGalleryPhotos()
                }

            GalleryPickerMviModel.Intent.LoadMorePhotos ->
                viewModelScope.launch {
                    loadNextPageGalleryPhotos()
                }
        }
    }

    private suspend fun refreshGalleryPhotos() {
        val albumName = uiState.value.currentAlbum ?: return
        albumPhotoPaginationManager.reset(
            AlbumPhotoPaginationSpecification.Default(albumName),
        )
        updateState { it.copy(canFetchMore = albumPhotoPaginationManager.canFetchMore) }
        loadNextPageGalleryPhotos()
    }

    private suspend fun loadNextPageGalleryPhotos() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val photos = albumPhotoPaginationManager.loadNextPage()
        updateState {
            it.copy(
                currentAlbumPhotos = photos,
                canFetchMore = albumPhotoPaginationManager.canFetchMore,
                loading = false,
            )
        }
    }
}
