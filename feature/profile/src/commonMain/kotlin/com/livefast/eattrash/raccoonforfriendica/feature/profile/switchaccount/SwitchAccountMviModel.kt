package com.livefast.eattrash.raccoonforfriendica.feature.profile.switchaccount

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel

@Stable
interface SwitchAccountMviModel :
    MviModel<SwitchAccountMviModel.Intent, SwitchAccountMviModel.State, SwitchAccountMviModel.Effect> {

    sealed interface Intent {
        data class SwitchAccount(val account: AccountModel) : Intent

        data object AddAccount : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val availableAccounts: List<AccountModel> = emptyList(),
    )

    sealed interface Effect {
        data object AccountChangeSuccess : Effect
    }
}
