package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isFriendica
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DrawerViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val identityRepository: IdentityRepository,
    private val nodeInfoRepository: NodeInfoRepository,
) : DefaultMviModel<DrawerMviModel.Intent, DrawerMviModel.State, DrawerMviModel.Effect>(
        initialState = DrawerMviModel.State(),
    ),
    DrawerMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    val node = apiConfigurationRepository.node.value
                    val isFriendica = nodeInfoRepository.getInfo()?.isFriendica == true
                    updateState {
                        it.copy(
                            user = currentUser,
                            node = node,
                            hasDirectMessages = isFriendica,
                            hasGallery = isFriendica,
                        )
                    }
                }.launchIn(this)
        }
    }
}
