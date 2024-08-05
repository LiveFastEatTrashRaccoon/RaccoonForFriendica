package accountdetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AccountRepository
import kotlinx.coroutines.launch

class AccountDetailViewModel(
    private val id: String,
    private val accountRepository: AccountRepository,
) : DefaultMviModel<AccountDetailMviModel.Intent, AccountDetailMviModel.State, AccountDetailMviModel.Effect>(
        initialState = AccountDetailMviModel.State(),
    ),
    AccountDetailMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                loadUser()
            }
        }
    }

    private suspend fun loadUser() {
        val account = accountRepository.getById(id)
        updateState {
            it.copy(account = account)
        }
    }
}
