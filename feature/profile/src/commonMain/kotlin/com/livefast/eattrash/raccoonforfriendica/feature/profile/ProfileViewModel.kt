package com.livefast.eattrash.raccoonforfriendica.feature.profile

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.profile.domain.MyAccountCache
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val logoutUseCase: LogoutUseCase,
    private val myAccountCache: MyAccountCache,
) : DefaultMviModel<ProfileMviModel.Intent, ProfileMviModel.State, ProfileMviModel.Effect>(
        initialState = ProfileMviModel.State(),
    ),
    ProfileMviModel {
    init {
        screenModelScope.launch {
            apiConfigurationRepository.isLogged
                .onEach { isLogged ->
                    updateState {
                        it.copy(isLogged = isLogged)
                    }
                    if (!isLogged) {
                        myAccountCache.clear()
                    }
                }.launchIn(this)
        }
    }

    override fun onDispose() {
        super.onDispose()
        myAccountCache.clear()
    }

    override fun reduce(intent: ProfileMviModel.Intent) {
        when (intent) {
            ProfileMviModel.Intent.Logout ->
                screenModelScope.launch {
                    logoutUseCase()
                }
        }
    }
}
