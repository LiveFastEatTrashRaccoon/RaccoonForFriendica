package com.livefast.eattrash.raccoonforfriendica.feature.profile

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel

@Stable
interface ProfileMviModel :
    ScreenModel,
    MviModel<ProfileMviModel.Intent, ProfileMviModel.State, ProfileMviModel.Effect> {
    sealed interface Intent {
        data object Logout : Intent

        data class SwitchAccount(
            val account: AccountModel,
        ) : Intent

        data class DeleteAccount(
            val account: AccountModel,
        ) : Intent

        data object AddAccount : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val availableAccounts: List<AccountModel> = emptyList(),
    )

    sealed interface Effect
}
