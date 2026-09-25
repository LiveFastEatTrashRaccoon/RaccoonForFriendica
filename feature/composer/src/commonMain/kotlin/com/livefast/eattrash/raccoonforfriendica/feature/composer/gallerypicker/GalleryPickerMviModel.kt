package com.livefast.eattrash.raccoonforfriendica.feature.composer.gallerypicker

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel

interface GalleryPickerMviModel :
    MviModel<GalleryPickerMviModel.Intent, GalleryPickerMviModel.State, GalleryPickerMviModel.Effect> {

    sealed interface Intent {
        data object InitialLoad : Intent

        data object LoadMorePhotos : Intent

        data class SelectAlbum(val album: String) : Intent
    }

    data class State(
        val currentAlbum: String? = null,
        val albums: List<MediaAlbumModel> = emptyList(),
        val canFetchMore: Boolean = true,
        val loading: Boolean = false,
        val currentAlbumPhotos: List<AttachmentModel> = emptyList(),
    )

    sealed interface Effect
}
