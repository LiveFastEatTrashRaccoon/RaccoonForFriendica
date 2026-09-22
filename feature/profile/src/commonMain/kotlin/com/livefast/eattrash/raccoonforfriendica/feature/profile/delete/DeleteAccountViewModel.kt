package com.livefast.eattrash.raccoonforfriendica.feature.profile.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
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
    MviModelDelegate<DeleteAccountMviModel.Intent, DeleteAccountMviModel.State, DeleteAccountMviModel.Effect>
    by DefaultMviModelDelegate(initialState = DeleteAccountMviModel.State()),
    DeleteAccountMviModel {

    override fun reduce(intent: DeleteAccountMviModel.Intent) {
        when (intent) {
            is DeleteAccountMviModel.Intent.Submit -> deleteAccount(intent.account)
        }
    }

    private fun deleteAccount(account: AccountModel) {
        if (account.active) {
            return
        }
        viewModelScope.launch {
            deleteAccountUseCase(account)
        }
    }
}
