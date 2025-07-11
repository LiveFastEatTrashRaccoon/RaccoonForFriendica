package com.livefast.eattrash.raccoonforfriendica.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DeleteAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SwitchAccountUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val identityRepository: IdentityRepository,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
    private val logoutUseCase: LogoutUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val authManager: AuthManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviModelDelegate<ProfileMviModel.Intent, ProfileMviModel.State, ProfileMviModel.Effect>
    by DefaultMviModelDelegate(initialState = ProfileMviModel.State()),
    ProfileMviModel {
    init {
        viewModelScope.launch {
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
                    updateState {
                        it.copy(
                            currentUserId = currentUser?.id,
                            loading = false,
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

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: ProfileMviModel.Intent) {
        when (intent) {
            ProfileMviModel.Intent.Logout ->
                viewModelScope.launch {
                    logoutUseCase()
                }

            is ProfileMviModel.Intent.SwitchAccount -> switchAccount(intent.account)
            is ProfileMviModel.Intent.DeleteAccount -> deleteAccount(intent.account)
            ProfileMviModel.Intent.AddAccount -> authManager.openNewAccount()
        }
    }

    private fun switchAccount(account: AccountModel) {
        check(account.remoteId != uiState.value.currentUserId) { return }
        viewModelScope.launch {
            updateState {
                it.copy(loading = true)
            }
            switchAccountUseCase(account)
            emitEffect(ProfileMviModel.Effect.AccountChangeSuccess)
        }
    }

    private fun deleteAccount(account: AccountModel) {
        check(account.remoteId != uiState.value.currentUserId) { return }
        viewModelScope.launch {
            deleteAccountUseCase(account)
        }
    }
}
