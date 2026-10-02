package com.livefast.eattrash.raccoonforfriendica.feature.drawer.switchaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
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
) : ViewModel(),
    MviDelegate<SwitchAccountMvi.Intent, SwitchAccountMvi.State, SwitchAccountMvi.Effect>
    by DefaultMviDelegate(initialState = SwitchAccountMvi.State()),
    SwitchAccountMvi {

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

    override fun reduce(intent: SwitchAccountMvi.Intent) {
        when (intent) {
            is SwitchAccountMvi.Intent.SwitchAccount -> switchAccount(intent.account)
        }
    }

    private fun switchAccount(account: AccountModel) {
        val currentUserId = uiState.value.currentUserId
        if (currentUserId != null && account.remoteId == currentUserId) {
            return
        }
        viewModelScope.launch {
            switchAccountUseCase(account)
            emitEffect(SwitchAccountMvi.Effect.AccountChangeSuccess)
        }
    }
}
