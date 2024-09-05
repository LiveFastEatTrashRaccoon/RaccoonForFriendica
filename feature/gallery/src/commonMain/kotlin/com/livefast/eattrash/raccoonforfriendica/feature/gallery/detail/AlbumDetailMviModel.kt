package com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel

interface AlbumDetailMviModel :
    ScreenModel,
    MviModel<AlbumDetailMviModel.Intent, AlbumDetailMviModel.State, AlbumDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class Delete(
            val id: String,
        ) : Intent

        data class Create(
            val byteArray: ByteArray,
        ) : Intent {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Create

                return byteArray.contentEquals(other.byteArray)
            }

            override fun hashCode(): Int = byteArray.contentHashCode()
        }

        data class EditDescription(
            val attachment: AttachmentModel,
            val description: String,
        ) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val loading: Boolean = false,
        val operationInProgress: Boolean = false,
        val refreshing: Boolean = false,
        val items: List<AttachmentModel> = emptyList(),
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object Failure : Effect
    }
}
