package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DrawerViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository,
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
            apiConfigurationRepository.isLogged
                .onEach { isLogged ->
                    if (isLogged) {
                        refreshUser()
                    } else {
                        updateState { it.copy(user = null) }
                    }
                }.launchIn(this)
        }
    }

    private suspend fun refreshUser() {
        val handle = accountRepository.getActive()?.handle.orEmpty()
        val currentAccount = userRepository.getByHandle(handle)
        updateState { it.copy(user = currentAccount) }
    }
}
