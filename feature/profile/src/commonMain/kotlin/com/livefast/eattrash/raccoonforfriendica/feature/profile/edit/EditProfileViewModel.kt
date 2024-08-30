package com.livefast.eattrash.raccoonforfriendica.feature.profile.edit

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val identityRepository: IdentityRepository,
    private val userRepository: UserRepository,
) : DefaultMviModel<EditProfileMviModel.Intent, EditProfileMviModel.State, EditProfileMviModel.Effect>(
        initialState = EditProfileMviModel.State(),
    ),
    EditProfileMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .mapNotNull { it?.let { u -> userRepository.getById(u.id) } }
                .onEach { currentUser ->
                    updateState {
                        it.copy(
                            displayName = TextFieldValue(text = currentUser.displayName.orEmpty()),
                            bio = TextFieldValue(text = currentUser.bio.orEmpty()),
                            bot = currentUser.bot,
                            locked = currentUser.locked,
                            noIndex = currentUser.noIndex,
                            discoverable = currentUser.discoverable,
                        )
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: EditProfileMviModel.Intent) {
        when (intent) {
            is EditProfileMviModel.Intent.ChangeBio ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            bio = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeBot ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            bot = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeDiscoverable ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            discoverable = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeDisplayName ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            displayName = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeLocked ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            locked = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is EditProfileMviModel.Intent.ChangeNoIndex ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            noIndex = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            EditProfileMviModel.Intent.AddField -> Unit
            is EditProfileMviModel.Intent.EditField -> Unit
            is EditProfileMviModel.Intent.RemoveField -> Unit
            EditProfileMviModel.Intent.Submit -> submit()
        }
    }

    private fun submit() {
        val currentState = uiState.value
        if (currentState.loading) {
            return
        }

        screenModelScope.launch {
            updateState {
                it.copy(
                    loading = true,
                )
            }

            val res =
                userRepository.updateProfile(
                    displayName = currentState.displayName.text,
                    note = currentState.bio.text,
                    bot = currentState.bot,
                    locked = currentState.locked,
                    indexable = !currentState.noIndex,
                    discoverable = currentState.discoverable,
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
