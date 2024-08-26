package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DrawerViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val identityRepository: IdentityRepository,
) : DefaultMviModel<DrawerMviModel.Intent, DrawerMviModel.State, DrawerMviModel.Effect>(
        initialState = DrawerMviModel.State(),
    ),
    DrawerMviModel {
    init {
        screenModelScope.launch {
            apiConfigurationRepository.node
                .onEach { node ->
                    updateState {
                        it.copy(
                            node = node,
                        )
                    }
                }.launchIn(this)
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(user = currentUser) }
                }.launchIn(this)
        }
    }
}
