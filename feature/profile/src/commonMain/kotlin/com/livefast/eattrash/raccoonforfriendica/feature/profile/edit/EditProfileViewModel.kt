package com.livefast.eattrash.raccoonforfriendica.feature.profile.edit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.QuotePolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface EditProfilerFieldType {
    data object DisplayName : EditProfilerFieldType

    data object Bio : EditProfilerFieldType
}

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val emojiRepository: EmojiRepository,
    private val settingsRepository: SettingsRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviDelegate<EditProfileMvi.Intent, EditProfileMvi.State, EditProfileMvi.Effect>
    by DefaultMviDelegate(initialState = EditProfileMvi.State()),
    EditProfileMvi {

    init {
        viewModelScope.launch {
            imageAutoloadObserver.enabled.onEach { autoloadImages ->
                updateState { it.copy(autoloadImages = autoloadImages) }
            }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(hideNavigationBarWhileScrolling = settings?.hideNavigationBarWhileScrolling ?: true)
                    }
                }.launchIn(this)

            supportedFeatureRepository.features.onEach { features ->
                updateState { it.copy(canSetQuotePolicy = features.supportsQuotePolicies) }
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
                        quotePolicy = currentUser.quotePolicy,
                        fields = currentUser.fields,
                        canAddFields = currentUser.fields.size < 4,
                    )
                }
            }

            val customEmojis = emojiRepository.getAll().orEmpty()
            updateState { it.copy(availableEmojis = customEmojis) }
        }
    }

    override fun reduce(intent: EditProfileMvi.Intent) {
        when (intent) {
            is EditProfileMvi.Intent.ChangeBio -> handleChangeBio(intent.value)
            is EditProfileMvi.Intent.ChangeBot -> handleChangeBot(intent.bot)
            is EditProfileMvi.Intent.ChangeDiscoverable -> handleChangeDiscoverable(intent.discoverable)
            is EditProfileMvi.Intent.ChangeDisplayName -> handleChangeDisplayName(intent.value)
            is EditProfileMvi.Intent.ChangeLocked -> handleChangeLocked(intent.locked)
            is EditProfileMvi.Intent.ChangeHideCollections -> handleChangeHideCollections(intent.hideCollections)
            is EditProfileMvi.Intent.ChangeNoIndex -> handleChangeNoIndex(intent.noIndex)
            EditProfileMvi.Intent.AddField -> handleAddField()
            is EditProfileMvi.Intent.RemoveField -> handleRemoveField(intent.index)
            is EditProfileMvi.Intent.EditField -> handleEditField(
                index = intent.index,
                key = intent.key,
                value = intent.value,
            )
            is EditProfileMvi.Intent.AvatarSelected -> handleAvatarSelected(intent.bytes)
            is EditProfileMvi.Intent.HeaderSelected -> handleHeaderSelected(intent.bytes)
            is EditProfileMvi.Intent.InsertCustomEmoji -> handleInsertCustomEmoji(
                fieldType = intent.fieldType,
                emoji = intent.emoji
            )
            EditProfileMvi.Intent.DeleteAccount -> handleDeleteAccount()
            is EditProfileMvi.Intent.ChangeQuotePolicy -> handleChangeQuotePolicy(intent.policy)
            EditProfileMvi.Intent.Submit -> handleSubmit()
        }
    }

    private fun handleChangeBio(value: TextFieldValue) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    bio = value,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleChangeBot(bot: Boolean) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    bot = bot,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleChangeDiscoverable(discoverable: Boolean) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    discoverable = discoverable,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleChangeDisplayName(value: TextFieldValue) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    displayName = value,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleChangeLocked(locked: Boolean) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    locked = locked,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleChangeHideCollections(hideCollections: Boolean) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    hideCollections = hideCollections,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleChangeNoIndex(noIndex: Boolean) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    noIndex = noIndex,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleDeleteAccount() {
        viewModelScope.launch {
            val node = apiConfigurationRepository.node.value
            val url = "https://$node"
            emitEffect(EditProfileMvi.Effect.OpenUrl(url))
        }
    }

    private fun handleChangeQuotePolicy(policy: QuotePolicy) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    quotePolicy = policy,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleAddField() {
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

    private fun handleEditField(index: Int, key: String, value: String) {
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

    private fun handleRemoveField(index: Int) {
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

    private fun handleAvatarSelected(bytes: ByteArray) {
        if (bytes.isEmpty()) { return }
        viewModelScope.launch(Dispatchers.IO) {
            updateState {
                it.copy(
                    avatarBytes = bytes,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleHeaderSelected(bytes: ByteArray) {
        if (bytes.isEmpty()) { return }
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

    private fun handleInsertCustomEmoji(fieldType: EditProfilerFieldType, emoji: EmojiModel) {
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

    private fun handleSubmit() {
        val currentState = uiState.value
        if (currentState.loading) { return }

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
                    quotePolicy = currentState.quotePolicy,
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
                emitEffect(EditProfileMvi.Effect.Success)
            } else {
                updateState { it.copy(loading = false) }
                emitEffect(EditProfileMvi.Effect.Failure)
            }
        }
    }
}
