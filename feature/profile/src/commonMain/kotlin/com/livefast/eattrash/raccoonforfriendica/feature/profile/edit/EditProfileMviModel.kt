package com.livefast.eattrash.raccoonforfriendica.feature.profile.edit

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel

@Stable
interface EditProfileMviModel :
    ScreenModel,
    MviModel<EditProfileMviModel.Intent, EditProfileMviModel.State, EditProfileMviModel.Effect> {
    sealed interface Intent {
        data class ChangeDisplayName(
            val value: TextFieldValue,
        ) : Intent

        data class ChangeBio(
            val value: TextFieldValue,
        ) : Intent

        data class ChangeBot(
            val value: Boolean,
        ) : Intent

        data class ChangeLocked(
            val value: Boolean,
        ) : Intent

        data class ChangeDiscoverable(
            val value: Boolean,
        ) : Intent

        data class ChangeHideCollections(
            val value: Boolean,
        ) : Intent

        data class ChangeNoIndex(
            val value: Boolean,
        ) : Intent

        data object AddField : Intent

        data class EditField(
            val index: Int,
            val key: String,
            val value: String,
        ) : Intent

        data class RemoveField(
            val index: Int,
        ) : Intent

        data class AvatarSelected(
            val value: ByteArray,
        ) : Intent {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as AvatarSelected

                return value.contentEquals(other.value)
            }

            override fun hashCode(): Int = value.contentHashCode()
        }

        data class HeaderSelected(
            val value: ByteArray,
        ) : Intent {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as HeaderSelected

                return value.contentEquals(other.value)
            }

            override fun hashCode(): Int = value.contentHashCode()
        }

        data class InsertCustomEmoji(
            val fieldType: EditProfilerFieldType,
            val emoji: EmojiModel,
        ) : Intent

        data object DeleteAccount : Intent

        data object Submit : Intent
    }

    data class State(
        val loading: Boolean = false,
        val hasUnsavedChanges: Boolean = false,
        val displayName: TextFieldValue = TextFieldValue(""),
        val bio: TextFieldValue = TextFieldValue(""),
        val avatar: String? = null,
        val avatarBytes: ByteArray? = null,
        val header: String? = null,
        val headerBytes: ByteArray? = null,
        val bot: Boolean = false,
        val locked: Boolean = false,
        val discoverable: Boolean = false,
        val hideCollections: Boolean = false,
        val noIndex: Boolean = false,
        val fields: List<FieldModel> = emptyList(),
        val canAddFields: Boolean = false,
        val availableEmojis: List<EmojiModel> = emptyList(),
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as State

            if (loading != other.loading) return false
            if (hasUnsavedChanges != other.hasUnsavedChanges) return false
            if (displayName != other.displayName) return false
            if (bio != other.bio) return false
            if (avatar != other.avatar) return false
            if (avatarBytes != null) {
                if (other.avatarBytes == null) return false
                if (!avatarBytes.contentEquals(other.avatarBytes)) return false
            } else if (other.avatarBytes != null) {
                return false
            }
            if (header != other.header) return false
            if (headerBytes != null) {
                if (other.headerBytes == null) return false
                if (!headerBytes.contentEquals(other.headerBytes)) return false
            } else if (other.headerBytes != null) {
                return false
            }
            if (bot != other.bot) return false
            if (locked != other.locked) return false
            if (discoverable != other.discoverable) return false
            if (hideCollections != other.hideCollections) return false
            if (noIndex != other.noIndex) return false
            if (fields != other.fields) return false
            if (canAddFields != other.canAddFields) return false
            if (availableEmojis != other.availableEmojis) return false
            if (autoloadImages != other.autoloadImages) return false
            if (hideNavigationBarWhileScrolling != other.hideNavigationBarWhileScrolling) return false

            return true
        }

        override fun hashCode(): Int {
            var result = loading.hashCode()
            result = 31 * result + hasUnsavedChanges.hashCode()
            result = 31 * result + displayName.hashCode()
            result = 31 * result + bio.hashCode()
            result = 31 * result + (avatar?.hashCode() ?: 0)
            result = 31 * result + (avatarBytes?.contentHashCode() ?: 0)
            result = 31 * result + (header?.hashCode() ?: 0)
            result = 31 * result + (headerBytes?.contentHashCode() ?: 0)
            result = 31 * result + bot.hashCode()
            result = 31 * result + locked.hashCode()
            result = 31 * result + discoverable.hashCode()
            result = 31 * result + hideCollections.hashCode()
            result = 31 * result + noIndex.hashCode()
            result = 31 * result + fields.hashCode()
            result = 31 * result + canAddFields.hashCode()
            result = 31 * result + availableEmojis.hashCode()
            return result
        }
    }

    sealed interface Effect {
        data object Success : Effect

        data object Failure : Effect

        data class OpenUrl(
            val url: String,
        ) : Effect
    }
}
