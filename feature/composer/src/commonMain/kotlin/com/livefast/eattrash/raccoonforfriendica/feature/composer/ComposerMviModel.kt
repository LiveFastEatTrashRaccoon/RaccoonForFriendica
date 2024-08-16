package com.livefast.eattrash.raccoonforfriendica.feature.composer

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility

interface ComposerMviModel :
    ScreenModel,
    MviModel<ComposerMviModel.Intent, ComposerMviModel.State, ComposerMviModel.Effect> {
    sealed interface Intent {
        data class SetText(
            val text: String,
        ) : Intent

        data class SetSpoilerText(
            val spoiler: String?,
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

        data class EditAttachmentDescription(
            val attachment: AttachmentModel,
            val description: String,
        ) : Intent

        data class RemoveAttachment(
            val attachmentId: String,
        ) : Intent

        data class AddLink(
            val link: Pair<String, String>,
        ) : Intent

        data object UserSearchLoadNextPage : Intent

        data class UserSearchSetQuery(
            val query: String,
        ) : Intent

        data class AddMention(
            val handle: String,
        ) : Intent

        data class AddGroupReference(
            val handle: String,
        ) : Intent

        data object Submit : Intent
    }

    data class State(
        val author: UserModel? = null,
        val lang: String? = null,
        val text: String = "",
        val spoilerText: String? = null,
        val visibility: Visibility = Visibility.Public,
        val availableVisibilities: List<Visibility> =
            listOf(
                Visibility.Public,
                Visibility.Unlisted,
                Visibility.Private,
            ),
        val sensitive: Boolean = false,
        val attachments: List<AttachmentModel> = emptyList(),
        val loading: Boolean = false,
        val userSearchUsers: List<UserModel> = emptyList(),
        val userSearchLoading: Boolean = false,
        val userSearchCanFetchMore: Boolean = true,
        val userSearchQuery: String = "",
    )

    sealed interface Effect {
        sealed interface ValidationError : Effect {
            data object TextOrImagesMandatory : ValidationError
        }

        data object Success : Effect

        data class Failure(
            val message: String?,
        ) : Effect
    }
}
