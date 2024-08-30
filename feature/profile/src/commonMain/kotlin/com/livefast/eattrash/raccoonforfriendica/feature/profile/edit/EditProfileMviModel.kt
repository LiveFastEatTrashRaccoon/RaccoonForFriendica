package com.livefast.eattrash.raccoonforfriendica.feature.profile.edit

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
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

        data object Submit : Intent
    }

    data class State(
        val loading: Boolean = false,
        val hasUnsavedChanges: Boolean = false,
        val bio: TextFieldValue = TextFieldValue(""),
        val displayName: TextFieldValue = TextFieldValue(""),
        val bot: Boolean = false,
        val locked: Boolean = false,
        val discoverable: Boolean = false,
        val noIndex: Boolean = false,
        val fields: List<FieldModel> = emptyList(),
        val canAddFields: Boolean = false,
    )

    sealed interface Effect {
        data object Success : Effect

        data object Failure : Effect
    }
}
