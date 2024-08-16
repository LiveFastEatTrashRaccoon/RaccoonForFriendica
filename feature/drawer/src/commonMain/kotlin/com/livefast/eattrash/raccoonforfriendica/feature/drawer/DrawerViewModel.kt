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
            accountRepository
                .getActiveAsFlow()
                .onEach { activeAccount ->
                    if (activeAccount == null) {
                        updateState { it.copy(user = null) }
                    } else {
                        val currentUser = userRepository.getByHandle(activeAccount.handle)
                        updateState { it.copy(user = currentUser) }
                    }
                }.launchIn(this)
        }
    }
}
