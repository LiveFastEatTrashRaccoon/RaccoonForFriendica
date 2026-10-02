package com.livefast.eattrash.raccoonforfriendica.feature.login.legacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LoginUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class LegacyLoginViewModel(
    private val credentialsRepository: CredentialsRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val loginUseCase: LoginUseCase,
) : ViewModel(),
    MviDelegate<LegacyLoginMvi.Intent, LegacyLoginMvi.State, LegacyLoginMvi.Effect>
    by DefaultMviDelegate(initialState = LegacyLoginMvi.State()),
    LegacyLoginMvi {
    init {
        viewModelScope.launch {
            val currentNode = apiConfigurationRepository.node.value
            updateState { it.copy(nodeName = currentNode) }
        }
    }

    override fun reduce(intent: LegacyLoginMvi.Intent) {
        when (intent) {
            is LegacyLoginMvi.Intent.SetNodeName ->
                viewModelScope.launch {
                    updateState { it.copy(nodeName = intent.name) }
                }

            is LegacyLoginMvi.Intent.SetUsername ->
                viewModelScope.launch {
                    updateState { it.copy(username = intent.username) }
                }

            is LegacyLoginMvi.Intent.SetPassword ->
                viewModelScope.launch {
                    updateState { it.copy(password = intent.password) }
                }

            LegacyLoginMvi.Intent.Submit -> submit()
        }
    }

    private fun submit() {
        if (uiState.value.loading) {
            return
        }

        viewModelScope.launch {
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
                val credentials = ApiCredentials.HttpBasic(user = user, pass = pass)
                loginUseCase(
                    node = node,
                    credentials = credentials,
                )
                updateState { it.copy(loading = false) }
                emitEffect(LegacyLoginMvi.Effect.Success)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                updateState { it.copy(loading = false) }
                emitEffect(LegacyLoginMvi.Effect.Failure(e.message))
            }
        }
    }
}
