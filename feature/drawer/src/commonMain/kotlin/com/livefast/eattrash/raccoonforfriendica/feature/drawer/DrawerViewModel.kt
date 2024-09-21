package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DrawerViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val identityRepository: IdentityRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
) : DefaultMviModel<DrawerMviModel.Intent, DrawerMviModel.State, DrawerMviModel.Effect>(
        initialState = DrawerMviModel.State(),
    ),
    DrawerMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    val node = apiConfigurationRepository.node.value
                    updateState {
                        it.copy(
                            user = currentUser,
                            node = node,
                        )
                    }
                }.launchIn(this)
            supportedFeatureRepository.features
                .onEach { features ->
                    updateState {
                        it.copy(
                            hasDirectMessages = features.supportsDirectMessages,
                            hasGallery = features.supportsPhotoGallery,
                        )
                    }
                }.launchIn(this)
        }
    }
}
