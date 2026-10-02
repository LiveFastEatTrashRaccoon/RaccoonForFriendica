package com.livefast.eattrash.raccoonforfriendica.feature.drawer.switchinstance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class SwitchInstanceViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val credentialsRepository: CredentialsRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
) : ViewModel(),
    MviDelegate<SwitchInstanceMvi.Intent, SwitchInstanceMvi.State, SwitchInstanceMvi.Effect>
    by DefaultMviDelegate(initialState = SwitchInstanceMvi.State()),
    SwitchInstanceMvi {

    override fun reduce(intent: SwitchInstanceMvi.Intent) {
        when (intent) {
            is SwitchInstanceMvi.Intent.SetInstanceName ->
                viewModelScope.launch {
                    updateState { it.copy(node = intent.name) }
                }

            SwitchInstanceMvi.Intent.Submit -> submitChangeNode()

            SwitchInstanceMvi.Intent.Reset -> reset()
        }
    }

    private fun reset() {
        viewModelScope.launch {
            updateState { it.copy(node = "", validationInProgress = false, nodeError = null) }
        }
    }

    private fun submitChangeNode() {
        val isLogged = apiConfigurationRepository.isLogged.value
        if (isLogged) {
            return
        }

        viewModelScope.launch {
            val newNode = uiState.value.node

            // validate fields
            val nodeNameError =
                if (newNode.isBlank()) {
                    ValidationError.MissingField
                } else {
                    updateState { it.copy(validationInProgress = true) }
                    val isNodeValid = credentialsRepository.validateNode(newNode)
                    if (!isNodeValid) {
                        ValidationError.InvalidField
                    } else {
                        null
                    }
                }
            updateState {
                it.copy(
                    nodeError = nodeNameError,
                    validationInProgress = false,
                )
            }

            val isValid = nodeNameError == null
            if (!isValid) {
                return@launch
            }

            apiConfigurationRepository.changeNode(newNode)
            supportedFeatureRepository.refresh()
            updateState { it.copy(node = "") }
            emitEffect(SwitchInstanceMvi.Effect.ChangeInstanceSuccess)
        }
    }
}
