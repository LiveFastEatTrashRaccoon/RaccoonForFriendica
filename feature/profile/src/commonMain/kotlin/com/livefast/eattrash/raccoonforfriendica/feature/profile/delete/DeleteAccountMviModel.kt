package com.livefast.eattrash.raccoonforfriendica.feature.profile.delete

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel

interface DeleteAccountMviModel :
    MviModel<DeleteAccountMviModel.Intent, DeleteAccountMviModel.State, DeleteAccountMviModel.Effect> {

    sealed interface Intent {
        data class Submit(val account: AccountModel) : Intent
    }

    data class State(
        val currentUserId: String? = null,
    )

    sealed interface Effect {
        data object Success : Effect
    }
}
