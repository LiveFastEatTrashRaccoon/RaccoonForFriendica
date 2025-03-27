package com.livefast.eattrash.raccoonforfriendica.feature.login.legacy

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LegacyLoginViewModel(
    private val credentialsRepository: CredentialsRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val loginUseCase: LoginUseCase,
) : DefaultMviModel<LegacyLoginMviModel.Intent, LegacyLoginMviModel.State, LegacyLoginMviModel.Effect>(
        initialState = LegacyLoginMviModel.State(),
    ),
    LegacyLoginMviModel {
    init {
        screenModelScope.launch {
            val currentNode = apiConfigurationRepository.node.value
            updateState { it.copy(nodeName = currentNode) }
        }
    }

    override fun reduce(intent: LegacyLoginMviModel.Intent) {
        when (intent) {
            is LegacyLoginMviModel.Intent.SetNodeName ->
                screenModelScope.launch {
                    updateState { it.copy(nodeName = intent.name) }
                }

            is LegacyLoginMviModel.Intent.SetUsername ->
                screenModelScope.launch {
                    updateState { it.copy(username = intent.username) }
                }

            is LegacyLoginMviModel.Intent.SetPassword ->
                screenModelScope.launch {
                    updateState { it.copy(password = intent.password) }
                }

            LegacyLoginMviModel.Intent.Submit -> submit()
        }
    }

    private fun submit() {
        check(!uiState.value.loading) { return }

        screenModelScope.launch {
            val node = uiState.value.nodeName
            val user = uiState.value.username
            val pass = uiState.value.password

            // validate fields
            val nodeNameError =
                if (node.isBlank()) {
                    ValidationError.MissingField
                } else {
                    val isNodeValid = credentialsRepository.validateNode(node)
                    if (!isNodeValid) {
                        ValidationError.InvalidField
                    } else {
                        null
                    }
                }
            val usernameError =
                if (user.isBlank()) {
                    ValidationError.MissingField
                } else {
                    null
                }
            val passwordError =
                if (pass.isBlank()) {
                    ValidationError.MissingField
                } else {
                    null
                }
            updateState {
                it.copy(
                    nodeNameError = nodeNameError,
                    usernameError = usernameError,
                    passwordError = passwordError,
                )
            }

            val isValid = listOfNotNull(nodeNameError, usernameError, passwordError).isEmpty()
            check(isValid) { return@launch }

            // submit data
            updateState { it.copy(loading = true) }

            try {
                val credentials = ApiCredentials.HttpBasic(user = user, pass = pass)
                loginUseCase(
                    node = node,
                    credentials = credentials,
                )
                updateState { it.copy(loading = false) }
                emitEffect(LegacyLoginMviModel.Effect.Success)
            } catch (e: Throwable) {
                updateState { it.copy(loading = false) }
                emitEffect(LegacyLoginMviModel.Effect.Failure(e.message))
            }
        }
    }
}
