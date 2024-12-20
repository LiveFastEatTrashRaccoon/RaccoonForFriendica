package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SwitchAccountUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DrawerViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val identityRepository: IdentityRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val credentialsRepository: CredentialsRepository,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val emojiHelper: EmojiHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    accountRepository: AccountRepository,
) : DefaultMviModel<DrawerMviModel.Intent, DrawerMviModel.State, DrawerMviModel.Effect>(
        initialState = DrawerMviModel.State(),
    ),
    DrawerMviModel {
    init {
        screenModelScope.launch {
            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)
            identityRepository.currentUser
                .onEach { currentUser ->
                    val user =
                        with(emojiHelper) {
                            currentUser?.withEmojisIfMissing()
                        }
                    updateState {
                        it.copy(user = user)
                    }
                }.launchIn(this)
            apiConfigurationRepository.node
                .onEach { node ->
                    updateState {
                        it.copy(node = node)
                    }
                }.launchIn(this)
            supportedFeatureRepository.features
                .onEach { features ->
                    updateState {
                        it.copy(
                            hasDirectMessages = features.supportsDirectMessages,
                            hasGallery = features.supportsPhotoGallery,
                            hasCalendar = features.supportsCalendar,
                            hasAnnouncements = features.supportsAnnouncements,
                        )
                    }
                }.launchIn(this)
            accountRepository
                .getAllAsFlow()
                .onEach { accounts ->
                    val nonAnonymousAccounts = accounts.filter { it.remoteId != null }
                    updateState {
                        it.copy(availableAccounts = nonAnonymousAccounts)
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: DrawerMviModel.Intent) {
        when (intent) {
            is DrawerMviModel.Intent.SetAnonymousChangeNode ->
                screenModelScope.launch {
                    updateState { it.copy(anonymousChangeNodeName = intent.nodeName) }
                }

            DrawerMviModel.Intent.SubmitAnonymousChangeNode -> submitChangeNode()
            is DrawerMviModel.Intent.SwitchAccount -> switchAccount(intent.account)
        }
    }

    private fun switchAccount(account: AccountModel) {
        if (account.remoteId == uiState.value.user?.id) {
            return
        }
        screenModelScope.launch {
            switchAccountUseCase(account)
        }
    }

    private fun submitChangeNode() {
        val isLogged = apiConfigurationRepository.isLogged.value
        if (isLogged) {
            return
        }

        screenModelScope.launch {
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
            if (!isValid) {
                return@launch
            }

            apiConfigurationRepository.changeNode(newNode)
            supportedFeatureRepository.refresh()
            emitEffect(DrawerMviModel.Effect.AnonymousChangeNodeSuccess)
        }
    }
}
