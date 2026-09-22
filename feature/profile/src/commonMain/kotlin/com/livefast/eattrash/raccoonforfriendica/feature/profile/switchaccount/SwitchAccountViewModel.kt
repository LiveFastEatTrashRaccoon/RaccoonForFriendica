package com.livefast.eattrash.raccoonforfriendica.feature.profile.switchaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SwitchAccountUseCase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class SwitchAccountViewModel(
    identityRepository: IdentityRepository,
    accountRepository: AccountRepository,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val authManager: AuthManager,
) : ViewModel(),
    MviModelDelegate<SwitchAccountMviModel.Intent, SwitchAccountMviModel.State, SwitchAccountMviModel.Effect>
    by DefaultMviModelDelegate(initialState = SwitchAccountMviModel.State()), SwitchAccountMviModel {

    init {
        identityRepository.currentUser
            .onEach { currentUser ->
                updateState {
                    it.copy(currentUserId = currentUser?.id)
                }
            }.launchIn(viewModelScope)
        accountRepository
            .getAllAsFlow()
            .onEach { accounts ->
                val nonAnonymousAccounts = accounts.filter { it.remoteId != null }
                updateState {
                    it.copy(availableAccounts = nonAnonymousAccounts)
                }
            }.launchIn(viewModelScope)
    }

    override fun reduce(intent: SwitchAccountMviModel.Intent) {
        when (intent) {
            is SwitchAccountMviModel.Intent.SwitchAccount -> switchAccount(intent.account)
            SwitchAccountMviModel.Intent.AddAccount -> authManager.openNewAccount()
        }
    }

    private fun switchAccount(account: AccountModel) {
        val currentUserId = uiState.value.currentUserId
        if (currentUserId != null && account.remoteId == currentUserId) {
            return
        }
        viewModelScope.launch {
            switchAccountUseCase(account)
            emitEffect(SwitchAccountMviModel.Effect.AccountChangeSuccess)
        }
    }
}
