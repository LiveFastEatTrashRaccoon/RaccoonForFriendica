package accountdetail

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel

interface AccountDetailMviModel :
    ScreenModel,
    MviModel<AccountDetailMviModel.Intent, AccountDetailMviModel.State, AccountDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val account: AccountModel? = null,
    )

    sealed interface Effect
}
