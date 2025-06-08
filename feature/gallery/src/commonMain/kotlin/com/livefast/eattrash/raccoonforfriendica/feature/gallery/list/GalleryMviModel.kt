package com.livefast.eattrash.raccoonforfriendica.feature.gallery.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel

interface GalleryMviModel :
    ScreenModel,
    MviModel<GalleryMviModel.Intent, GalleryMviModel.State, GalleryMviModel.Effect> {
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
