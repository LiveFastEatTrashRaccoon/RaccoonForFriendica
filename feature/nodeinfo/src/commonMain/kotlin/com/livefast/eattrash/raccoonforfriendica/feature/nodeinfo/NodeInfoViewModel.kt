package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NodeInfoViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val identityRepository: IdentityRepository,
    private val nodeInfoRepository: NodeInfoRepository,
    private val credentialsRepository: CredentialsRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val settingsRepository: SettingsRepository,
    private val emojiHelper: EmojiHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviModelDelegate<NodeInfoMviModel.Intent, NodeInfoMviModel.State, NodeInfoMviModel.Effect>
    by DefaultMviModelDelegate(initialState = NodeInfoMviModel.State()),
    NodeInfoMviModel {

    init {
        viewModelScope.launch {
            identityRepository.currentUser.onEach { user ->
                updateState {
                    it.copy(isLogged = user != null)
                }
            }.launchIn(this)
            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            loadInfo()
        }
    }

    private suspend fun loadInfo() {
        val nodeInfo =
            nodeInfoRepository.getInfo()?.let { info ->
                info.copy(
                    contact =
                    with(emojiHelper) {
                        info.contact?.withEmojisIfMissing()
                    },
                )
            }
        updateState { it.copy(info = nodeInfo) }
    }

    override fun reduce(intent: NodeInfoMviModel.Intent) {
        when (intent) {
            is NodeInfoMviModel.Intent.SetAnonymousChangeNode ->
                viewModelScope.launch {
                    updateState { it.copy(anonymousChangeNodeName = intent.nodeName) }
                }

            NodeInfoMviModel.Intent.SubmitAnonymousChangeNode -> submitChangeNode()
        }
    }

    private fun submitChangeNode() {
        val isLogged = apiConfigurationRepository.isLogged.value
        check(!isLogged) { return }

        viewModelScope.launch {
            val newNode = uiState.value.anonymousChangeNodeName

            // validate fields
            val nodeNameError =
                if (newNode.isBlank()) {
                    ValidationError.MissingField
                } else {
                    updateState { it.copy(anonymousChangeNodeValidationInProgress = true) }
                    val isNodeValid = credentialsRepository.validateNode(newNode)
                    if (!isNodeValid) {
                        ValidationError.InvalidField
                    } else {
                        null
                    }
                }
            updateState {
                it.copy(
                    anonymousChangeNodeNameError = nodeNameError,
                    anonymousChangeNodeValidationInProgress = false,
                )
            }

            val isValid = nodeNameError == null
            check(isValid) { return@launch }

            apiConfigurationRepository.changeNode(newNode)
            supportedFeatureRepository.refresh()
            loadInfo()
            emitEffect(NodeInfoMviModel.Effect.AnonymousChangeNodeSuccess)
        }
    }
}
