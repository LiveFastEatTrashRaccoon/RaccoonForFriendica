package com.livefast.eattrash.raccoonforfriendica.feature.profile.delete

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel

interface DeleteAccountMvi : Mvi<DeleteAccountMvi.Intent, DeleteAccountMvi.State, DeleteAccountMvi.Effect> {

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
