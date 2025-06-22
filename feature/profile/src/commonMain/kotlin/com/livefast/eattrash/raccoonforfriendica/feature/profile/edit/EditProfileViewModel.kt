package com.livefast.eattrash.raccoonforfriendica.feature.profile.edit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface EditProfilerFieldType {
    data object DisplayName : EditProfilerFieldType

    data object Bio : EditProfilerFieldType
}

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val emojiRepository: EmojiRepository,
    private val settingsRepository: SettingsRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviModelDelegate<EditProfileMviModel.Intent, EditProfileMviModel.State, EditProfileMviModel.Effect>
    by DefaultMviModelDelegate(initialState = EditProfileMviModel.State()),
    EditProfileMviModel {
    init {
        viewModelScope.launch {
            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            userRepository.getCurrent(refresh = true)?.also { currentUser ->
                updateState {
                    it.copy(
                        displayName = TextFieldValue(text = currentUser.displayName.orEmpty()),
                        avatar = currentUser.avatar,
                        bio = TextFieldValue(text = currentUser.bio.orEmpty()),
                        bot = currentUser.bot,
                        header = currentUser.header,
                        locked = currentUser.locked,
                        noIndex = currentUser.noIndex,
                        discoverable = currentUser.discoverable,
                        fields = currentUser.fields,
                        canAddFields = currentUser.fields.size < 4,
                    )
                }
            }

            val customEmojis = emojiRepository.getAll().orEmpty()
            updateState { it.copy(availableEmojis = customEmojis) }
        }
    }

    override fun reduce(intent: EditProfileMviModel.Intent) {
        when (intent) {
            is EditProfileMviModel.Intent.ChangeBio ->
                viewModelScope.launch {
                    updateState {
                        it.copy(
                            bio = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeBot ->
                viewModelScope.launch {
                    updateState {
                        it.copy(
                            bot = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeDiscoverable ->
                viewModelScope.launch {
                    updateState {
                        it.copy(
                            discoverable = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeDisplayName ->
                viewModelScope.launch {
                    updateState {
                        it.copy(
                            displayName = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeLocked ->
                viewModelScope.launch {
                    updateState {
                        it.copy(
                            locked = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeHideCollections ->
                viewModelScope.launch {
                    updateState {
                        it.copy(
                            hideCollections = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeNoIndex ->
                viewModelScope.launch {
                    updateState {
                        it.copy(
                            noIndex = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            EditProfileMviModel.Intent.AddField -> addField()
            is EditProfileMviModel.Intent.RemoveField -> removeField(index = intent.index)
            is EditProfileMviModel.Intent.EditField ->
                editField(
                    index = intent.index,
                    key = intent.key,
                    value = intent.value,
                )

            is EditProfileMviModel.Intent.AvatarSelected -> loadImageAvatar(intent.value)
            is EditProfileMviModel.Intent.HeaderSelected -> loadImageHeader(intent.value)
            is EditProfileMviModel.Intent.InsertCustomEmoji ->
                insertCustomEmoji(intent.fieldType, intent.emoji)

            EditProfileMviModel.Intent.DeleteAccount ->
                viewModelScope.launch {
                    val node = apiConfigurationRepository.node.value
                    val url = "https://$node"
                    emitEffect(EditProfileMviModel.Effect.OpenUrl(url))
                }

            EditProfileMviModel.Intent.Submit -> submit()
        }
    }

    private fun addField() {
        viewModelScope.launch {
            updateState {
                val newFields = it.fields + FieldModel("", "")
                it.copy(
                    fields = newFields,
                    hasUnsavedChanges = true,
                    canAddFields = newFields.size < 4,
                )
            }
        }
    }

    private fun editField(index: Int, key: String, value: String) {
        viewModelScope.launch {
            updateState {
                val newFields =
                    it.fields.mapIndexed { idx, field ->
                        if (idx == index) {
                            field.copy(key = key, value = value)
                        } else {
                            field
                        }
                    }
                it.copy(
                    fields = newFields,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun removeField(index: Int) {
        viewModelScope.launch {
            updateState {
                val newFields = it.fields.filterIndexed { idx, _ -> idx != index }
                it.copy(
                    fields = newFields,
                    hasUnsavedChanges = true,
                    canAddFields = newFields.size < 4,
                )
            }
        }
    }

    private fun loadImageAvatar(bytes: ByteArray) {
        check(bytes.isNotEmpty()) { return }
        viewModelScope.launch(Dispatchers.IO) {
            updateState {
                it.copy(
                    avatarBytes = bytes,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun loadImageHeader(bytes: ByteArray) {
        check(bytes.isNotEmpty()) { return }
        viewModelScope.launch(Dispatchers.IO) {
            updateState {
                it.copy(
                    headerBytes = bytes,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun getNewTextFieldValue(value: TextFieldValue, additionalPart: String, offsetAfter: Int): TextFieldValue {
        val (text, selection) = value.let { it.text to it.selection }
        val newText =
            buildString {
                append(text.substring(0, selection.start))
                append(additionalPart)
                append(
                    text.substring(
                        selection.end,
                        text.length,
                    ),
                )
            }

        val newSelection =
            if (selection.collapsed) {
                TextRange(index = selection.start + offsetAfter)
            } else {
                TextRange(
                    start = selection.start + offsetAfter,
                    end = selection.end + offsetAfter,
                )
            }
        return value.copy(text = newText, selection = newSelection)
    }

    private fun insertCustomEmoji(fieldType: EditProfilerFieldType, emoji: EmojiModel) {
        viewModelScope.launch {
            val value =
                when (fieldType) {
                    EditProfilerFieldType.DisplayName ->
                        uiState.value.displayName

                    EditProfilerFieldType.Bio ->
                        uiState.value.bio
                }
            val additionalPart =
                buildString {
                    append(":")
                    append(emoji.code)
                    append(":")
                }
            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart = additionalPart,
                    offsetAfter = additionalPart.length,
                )
            updateState {
                when (fieldType) {
                    EditProfilerFieldType.DisplayName -> it.copy(displayName = newValue)
                    EditProfilerFieldType.Bio -> it.copy(bio = newValue)
                }
            }
        }
    }

    private fun submit() {
        val currentState = uiState.value
        check(!currentState.loading) { return }

        val fieldMap =
            currentState.fields.fold(mutableMapOf<String, String>()) { res, item ->
                res[item.key] = item.value
                res
            }

        viewModelScope.launch {
            updateState {
                it.copy(loading = true)
            }

            val res =
                userRepository.updateProfile(
                    displayName = currentState.displayName.text,
                    note = currentState.bio.text,
                    bot = currentState.bot,
                    locked = currentState.locked,
                    indexable = !currentState.noIndex,
                    hideCollections = currentState.hideCollections,
                    discoverable = currentState.discoverable,
                    fields = fieldMap,
                    avatar = currentState.avatarBytes,
                    header = currentState.headerBytes,
                )

            if (res != null) {
                updateState {
                    it.copy(
                        hasUnsavedChanges = false,
                        loading = false,
                    )
                }
                emitEffect(EditProfileMviModel.Effect.Success)
            } else {
                updateState { it.copy(loading = false) }
                emitEffect(EditProfileMviModel.Effect.Failure)
            }
        }
    }
}
