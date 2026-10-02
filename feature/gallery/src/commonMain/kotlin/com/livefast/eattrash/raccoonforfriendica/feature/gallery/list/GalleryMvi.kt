package com.livefast.eattrash.raccoonforfriendica.feature.gallery.list

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel

interface GalleryMvi : Mvi<GalleryMvi.Intent, GalleryMvi.State, GalleryMvi.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class UpdateAlbum(val oldName: String, val newName: String) : Intent

        data class DeleteAlbum(val name: String) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val loading: Boolean = false,
        val operationInProgress: Boolean = false,
        val refreshing: Boolean = false,
        val items: List<MediaAlbumModel> = emptyList(),
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object Failure : Effect
    }
}
