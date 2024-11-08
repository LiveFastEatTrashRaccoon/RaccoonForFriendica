package com.livefast.eattrash.raccoonforfriendica.feature.login.oauth

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
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
) : DefaultMviModel<LoginMviModel.Intent, LoginMviModel.State, LoginMviModel.Effect>(
        initialState = LoginMviModel.State(),
    ),
    LoginMviModel {
    init {
        screenModelScope.launch {
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
                screenModelScope.launch {
                    updateState { it.copy(nodeName = intent.name) }
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
            if (!isValid) {
                return@launch
            }

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
