package com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel

interface AlbumDetailMviModel :
    MviModel<AlbumDetailMviModel.Intent, AlbumDetailMviModel.State, AlbumDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class Delete(val id: String) : Intent

        data class Create(val byteArray: ByteArray) : Intent {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Create

                return byteArray.contentEquals(other.byteArray)
            }

            override fun hashCode(): Int = byteArray.contentHashCode()
        }

        data class EditDescription(val attachment: AttachmentModel, val description: String) : Intent

        data class Move(val attachment: AttachmentModel, val album: String) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val loading: Boolean = false,
        val operationInProgress: Boolean = false,
        val refreshing: Boolean = false,
        val items: List<AttachmentModel> = emptyList(),
        val albums: List<MediaAlbumModel> = emptyList(),
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object Failure : Effect
    }
}
