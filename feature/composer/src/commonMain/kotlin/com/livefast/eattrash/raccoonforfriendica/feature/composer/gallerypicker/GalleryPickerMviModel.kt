package com.livefast.eattrash.raccoonforfriendica.feature.composer.gallerypicker

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel

interface GalleryPickerMviModel :
    MviModel<GalleryPickerMviModel.Intent, GalleryPickerMviModel.State, GalleryPickerMviModel.Effect> {

    sealed interface Intent {
        data object GalleryInitialLoad : Intent

        data object GalleryLoadMorePhotos : Intent

        data class GalleryAlbumSelected(val album: String) : Intent
    }

    data class State(
        val galleryCurrentAlbum: String? = null,
        val galleryAlbums: List<MediaAlbumModel> = emptyList(),
        val galleryCanFetchMore: Boolean = true,
        val galleryLoading: Boolean = false,
        val galleryCurrentAlbumPhotos: List<AttachmentModel> = emptyList(),
    )

    sealed interface Effect
}
