package com.livefast.eattrash.raccoonforfriendica.feature.composer

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility

sealed interface ComposerFieldType {
    data object Spoiler : ComposerFieldType

    data object Title : ComposerFieldType

    data object Body : ComposerFieldType
}

interface ComposerMviModel :
    ScreenModel,
    MviModel<ComposerMviModel.Intent, ComposerMviModel.State, ComposerMviModel.Effect> {
    sealed interface Intent {
        data class LoadEditedPost(
            val id: String,
        ) : Intent

        data class SetFieldValue(
            val value: TextFieldValue,
            val fieldType: ComposerFieldType,
        ) : Intent

        data class SetVisibility(
            val visibility: Visibility,
        ) : Intent

        data class SetSensitive(
            val sensitive: Boolean,
        ) : Intent

        data class AddAttachment(
            val byteArray: ByteArray,
        ) : Intent {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as AddAttachment

                return byteArray.contentEquals(other.byteArray)
            }

            override fun hashCode(): Int = byteArray.contentHashCode()
        }

        data class AddAttachmentsFromGallery(
            val attachments: List<AttachmentModel>,
        ) : Intent

        data class EditAttachmentDescription(
            val attachment: AttachmentModel,
            val description: String,
        ) : Intent

        data class RemoveAttachment(
            val attachment: AttachmentModel,
        ) : Intent

        data class AddLink(
            val link: Pair<String, String>,
        ) : Intent

        data object UserSearchLoadNextPage : Intent

        data class UserSearchSetQuery(
            val query: String,
        ) : Intent

        data object UserSearchClear : Intent

        data class AddMention(
            val handle: String,
        ) : Intent

        data class AddGroupReference(
            val handle: String,
        ) : Intent

        data class AddBoldFormat(
            val fieldType: ComposerFieldType,
        ) : Intent

        data class AddItalicFormat(
            val fieldType: ComposerFieldType,
        ) : Intent

        data class AddUnderlineFormat(
            val fieldType: ComposerFieldType,
        ) : Intent

        data object ToggleHasSpoiler : Intent

        data object ToggleHasTitle : Intent

        data object Submit : Intent

        data object GalleryInitialLoad : Intent

        data object GalleryLoadMorePhotos : Intent

        data class GalleryAlbumSelected(
            val album: String,
        ) : Intent
    }

    data class State(
        val author: UserModel? = null,
        val lang: String? = null,
        val titleValue: TextFieldValue = TextFieldValue(),
        val bodyValue: TextFieldValue = TextFieldValue(),
        val spoilerValue: TextFieldValue = TextFieldValue(),
        val visibility: Visibility = Visibility.Public,
        val availableVisibilities: List<Visibility> =
            listOf(
                Visibility.Public,
                Visibility.Unlisted,
                Visibility.Private,
                Visibility.Direct,
                Visibility.Circle(),
            ),
        val sensitive: Boolean = false,
        val attachments: List<AttachmentModel> = emptyList(),
        val loading: Boolean = false,
        val userSearchUsers: List<UserModel> = emptyList(),
        val userSearchLoading: Boolean = false,
        val userSearchCanFetchMore: Boolean = true,
        val userSearchQuery: String = "",
        val availableCircles: List<CircleModel> = emptyList(),
        val hasSpoiler: Boolean = false,
        val hasTitle: Boolean = false,
        val hasGallery: Boolean = false,
        val galleryCurrentAlbum: String? = null,
        val galleryAlbums: List<MediaAlbumModel> = emptyList(),
        val galleryCanFetchMore: Boolean = true,
        val galleryLoading: Boolean = false,
        val galleryCurrentAlbumPhotos: List<AttachmentModel> = emptyList(),
        val characterLimit: Int? = null,
        val attachmentLimit: Int? = null,
    )

    sealed interface Effect {
        sealed interface ValidationError : Effect {
            data object TextOrImagesMandatory : ValidationError

            data object InvalidVisibility : ValidationError

            data object CharacterLimitExceeded : ValidationError
        }

        data object Success : Effect

        data class Failure(
            val message: String? = null,
        ) : Effect
    }
}
