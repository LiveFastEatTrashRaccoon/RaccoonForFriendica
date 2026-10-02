package com.livefast.eattrash.raccoonforfriendica.feature.profile.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DeleteAccountUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class DeleteAccountViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel(),
    MviDelegate<DeleteAccountMvi.Intent, DeleteAccountMvi.State, DeleteAccountMvi.Effect>
    by DefaultMviDelegate(initialState = DeleteAccountMvi.State()),
    DeleteAccountMvi {

    override fun reduce(intent: DeleteAccountMvi.Intent) {
        when (intent) {
            is DeleteAccountMvi.Intent.Submit -> deleteAccount(intent.account)
        }
    }

    private fun deleteAccount(account: AccountModel) {
        if (account.active) {
            return
        }
        viewModelScope.launch {
            deleteAccountUseCase(account)
            emitEffect(DeleteAccountMvi.Effect.Success)
        }
    }
}
