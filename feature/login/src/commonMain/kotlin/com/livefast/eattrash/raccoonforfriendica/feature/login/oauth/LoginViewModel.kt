package com.livefast.eattrash.raccoonforfriendica.feature.login.oauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LoginUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class LoginViewModel(
    private val type: LoginType,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val credentialsRepository: CredentialsRepository,
    private val authManager: AuthManager,
    private val loginUseCase: LoginUseCase,
) : ViewModel(),
    MviModelDelegate<LoginMviModel.Intent, LoginMviModel.State, LoginMviModel.Effect>
    by DefaultMviModelDelegate(initialState = LoginMviModel.State()),
    LoginMviModel {
    init {
        viewModelScope.launch {
            authManager.credentialFlow
                .debounce(250)
                .onEach { credentials ->
                    finalizeLogin(credentials)
                }.launchIn(this)
            val currentNode = apiConfigurationRepository.node.value
            val shouldUseDropDown = type == LoginType.Friendica
            updateState {
                it.copy(
                    useDropDown = shouldUseDropDown,
                    nodeName = currentNode.takeIf { shouldUseDropDown }.orEmpty(),
                )
            }
        }
    }

    override fun reduce(intent: LoginMviModel.Intent) {
        when (intent) {
            is LoginMviModel.Intent.SetNodeName ->
                viewModelScope.launch {
                    updateState { it.copy(nodeName = intent.name) }
                }

            LoginMviModel.Intent.SignUp -> triggerSignup()

            LoginMviModel.Intent.Submit -> submit()
        }
    }

    private fun triggerSignup() {
        viewModelScope.launch {
            val node = uiState.value.nodeName
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
            updateState {
                it.copy(
                    nodeNameError = nodeNameError,
                )
            }
            val isValid = nodeNameError == null
            check(isValid) { return@launch }

            val url = getSignupUrl(node = node, type = type)
            if (url.isEmpty()) {
                emitEffect(LoginMviModel.Effect.Failure())
            } else {
                emitEffect(LoginMviModel.Effect.OpenUrl(url))
            }
        }
    }

    private fun submit() {
        check(!uiState.value.loading) { return }

        viewModelScope.launch {
            val node = uiState.value.nodeName

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
            updateState {
                it.copy(
                    nodeNameError = nodeNameError,
                )
            }

            val isValid = nodeNameError == null
            check(isValid) { return@launch }

            updateState { it.copy(loading = true) }
            try {
                val url = authManager.startOAuthFlow(node)
                emitEffect(LoginMviModel.Effect.OpenUrl(url))
            } catch (e: Throwable) {
                emitEffect(LoginMviModel.Effect.Failure(e.message))
                updateState { it.copy(loading = false) }
            }
        }
    }

    private suspend fun finalizeLogin(credentials: ApiCredentials) {
        val node = uiState.value.nodeName
        try {
            loginUseCase(
                node = node,
                credentials = credentials,
            )
            emitEffect(LoginMviModel.Effect.Success)
        } catch (e: Throwable) {
            updateState { it.copy(loading = false) }
            emitEffect(LoginMviModel.Effect.Failure(e.message))
        }
    }
}

private fun getSignupUrl(node: String, type: LoginType) = buildString {
    when (type) {
        LoginType.Friendica -> {
            append("https://")
            append(node)
            append("/register")
        }

        LoginType.Mastodon -> {
            append("https://")
            append(node)
            append("/auth/sign_up")
        }

        else -> Unit
    }
}
