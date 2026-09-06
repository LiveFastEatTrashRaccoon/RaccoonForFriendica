package com.livefast.eattrash.raccoonforfriendica.feature.login.oauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DefaultFriendicaInstances
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LoginUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginViewModel.Companion.KEY_ARGS
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(FlowPreview::class)
@AssistedInject
class LoginViewModel(
    @Assisted args: LoginViewModelArgs,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val credentialsRepository: CredentialsRepository,
    private val authManager: AuthManager,
    private val loginUseCase: LoginUseCase,
) : ViewModel(),
    MviModelDelegate<LoginMviModel.Intent, LoginMviModel.State, LoginMviModel.Effect>
    by DefaultMviModelDelegate(initialState = LoginMviModel.State()),
    LoginMviModel {

    private val type = args.type

    init {
        viewModelScope.launch {
            authManager.credentialFlow
                .debounce(250)
                .onEach { credentials ->
                    finalizeLogin(credentials)
                }.launchIn(this)
            val currentNode = apiConfigurationRepository.node.value
            val shouldUseDropDown = type == LoginType.Friendica
            val isCurrentNodeInDropDown = DefaultFriendicaInstances.any { it.value == currentNode }
            updateState {
                it.copy(
                    useDropDown = shouldUseDropDown,
                    nodeName = currentNode.takeIf { shouldUseDropDown && isCurrentNodeInDropDown }.orEmpty(),
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

    private fun finalizeLogin(credentials: ApiCredentials) {
        val node = uiState.value.nodeName
        viewModelScope.launch {
            try {
                loginUseCase(
                    node = node,
                    credentials = credentials,
                )
                emitEffect(LoginMviModel.Effect.Success)
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                updateState { it.copy(loading = false) }
                emitEffect(LoginMviModel.Effect.Failure(e.message))
            }
        }
    }

    companion object {
        val KEY_ARGS = CreationExtras.Key<LoginViewModelArgs>()
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

@Serializable
data class LoginViewModelArgs(val type: LoginType)

@AssistedFactory
@ViewModelAssistedFactoryKey(LoginViewModel::class)
@ContributesIntoMap(AppScope::class)
interface LoginViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): LoginViewModel =
        create(extras[KEY_ARGS] ?: error("ViewModel creation args not found"))

    fun create(@Assisted args: LoginViewModelArgs): LoginViewModel
}
