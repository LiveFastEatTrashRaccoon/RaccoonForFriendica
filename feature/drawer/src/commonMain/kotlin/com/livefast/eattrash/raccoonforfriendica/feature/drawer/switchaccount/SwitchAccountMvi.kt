package com.livefast.eattrash.raccoonforfriendica.feature.drawer.switchaccount

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel

interface SwitchAccountMvi : Mvi<SwitchAccountMvi.Intent, SwitchAccountMvi.State, SwitchAccountMvi.Effect> {

    sealed interface Intent {
        data class SwitchAccount(val account: AccountModel) : Intent
    }

    data class State(val currentUserId: String? = null, val availableAccounts: List<AccountModel> = emptyList())

    sealed interface Effect {
        data object AccountChangeSuccess : Effect
    }
}
