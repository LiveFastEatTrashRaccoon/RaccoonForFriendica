package com.livefast.eattrash.raccoonforfriendica.feature.profile

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val logoutUseCase: LogoutUseCase,
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
                }.launchIn(this)
        }
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
