package com.livefast.eattrash.raccoonforfriendica.feature.login

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val credentialsRepository: CredentialsRepository,
    private val loginUseCase: LoginUseCase,
) : DefaultMviModel<LoginMviModel.Intent, LoginMviModel.State, LoginMviModel.Effect>(
        initialState = LoginMviModel.State(),
    ),
    LoginMviModel {
    override fun reduce(intent: LoginMviModel.Intent) {
        when (intent) {
            is LoginMviModel.Intent.SetNodeName ->
                screenModelScope.launch {
                    updateState { it.copy(nodeName = intent.name) }
                }

            is LoginMviModel.Intent.SetUsername ->
                screenModelScope.launch {
                    updateState { it.copy(username = intent.username) }
                }

            is LoginMviModel.Intent.SetPassword ->
                screenModelScope.launch {
                    updateState { it.copy(password = intent.password) }
                }

            LoginMviModel.Intent.Submit -> submit()
        }
    }

    private fun submit() {
        if (uiState.value.loading) {
            return
        }

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
            if (!isValid) {
                return@launch
            }

            // submit data
            updateState { it.copy(loading = true) }
            try {
                loginUseCase(
                    node = node,
                    user = user,
                    pass = pass,
                )
                updateState { it.copy(loading = false) }
                emitEffect(LoginMviModel.Effect.Success)
            } catch (e: Throwable) {
                updateState { it.copy(loading = false) }
                val message = e.message
                emitEffect(LoginMviModel.Effect.Failure(message))
            }
        }
    }
}
