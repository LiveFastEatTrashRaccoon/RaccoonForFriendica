package com.livefast.eattrash.raccoonforfriendica.feature.composer.gallerypicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
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
    MviDelegate<GalleryPickerMvi.Intent, GalleryPickerMvi.State, GalleryPickerMvi.Effect>
    by DefaultMviDelegate(initialState = GalleryPickerMvi.State()),
    GalleryPickerMvi {

    override fun reduce(intent: GalleryPickerMvi.Intent) {
        when (intent) {
            is GalleryPickerMvi.Intent.SelectAlbum ->
                viewModelScope.launch {
                    updateState { it.copy(currentAlbum = intent.album) }
                    refreshGalleryPhotos()
                }

            GalleryPickerMvi.Intent.InitialLoad ->
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

            GalleryPickerMvi.Intent.LoadMorePhotos ->
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
