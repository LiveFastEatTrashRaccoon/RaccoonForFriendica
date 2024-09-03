package com.livefast.eattrash.raccoonforfriendica.feature.profile

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DeleteAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SwitchAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.profile.domain.MyAccountCache
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val identityRepository: IdentityRepository,
    private val accountRepository: AccountRepository,
    private val logoutUseCase: LogoutUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val myAccountCache: MyAccountCache,
    private val authManager: AuthManager,
) : DefaultMviModel<ProfileMviModel.Intent, ProfileMviModel.State, ProfileMviModel.Effect>(
        initialState = ProfileMviModel.State(),
    ),
    ProfileMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState {
                        it.copy(currentUserId = currentUser?.id)
                    }
                    if (currentUser == null) {
                        myAccountCache.clear()
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

            is ProfileMviModel.Intent.SwitchAccount -> switchAccount(intent.account)
            is ProfileMviModel.Intent.DeleteAccount -> deleteAccount(intent.account)
            ProfileMviModel.Intent.AddAccount -> authManager.openLogin()
        }
    }

    private fun switchAccount(account: AccountModel) {
        if (account.remoteId == uiState.value.currentUserId) {
            return
        }
        screenModelScope.launch {
            switchAccountUseCase(account)
        }
    }

    private fun deleteAccount(account: AccountModel) {
        if (account.remoteId == uiState.value.currentUserId) {
            return
        }
        screenModelScope.launch {
            deleteAccountUseCase(account)
        }
    }
}
