package com.livefast.eattrash.raccoonforfriendica.feature.drawer.switchinstance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
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
    MviModelDelegate<SwitchInstanceMviModel.Intent, SwitchInstanceMviModel.State, SwitchInstanceMviModel.Effect>
    by DefaultMviModelDelegate(initialState = SwitchInstanceMviModel.State()),
    SwitchInstanceMviModel {

    override fun reduce(intent: SwitchInstanceMviModel.Intent) {
        when (intent) {
            is SwitchInstanceMviModel.Intent.SetInstanceName ->
                viewModelScope.launch {
                    updateState { it.copy(node = intent.name) }
                }

            SwitchInstanceMviModel.Intent.Submit -> submitChangeNode()
            SwitchInstanceMviModel.Intent.Reset -> reset()
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
            emitEffect(SwitchInstanceMviModel.Effect.ChangeInstanceSuccess)
        }
    }
}
