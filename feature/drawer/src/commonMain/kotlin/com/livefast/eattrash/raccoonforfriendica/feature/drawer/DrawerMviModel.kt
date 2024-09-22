package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel

@Stable
interface DrawerMviModel :
    ScreenModel,
    MviModel<DrawerMviModel.Intent, DrawerMviModel.State, DrawerMviModel.Effect> {
    sealed interface Intent {
        data class SetAnonymousChangeNode(
            val nodeName: String,
        ) : Intent

        data object SubmitAnonymousChangeNode : Intent

        data class SwitchAccount(
            val account: AccountModel,
        ) : Intent
    }

    data class State(
        val user: UserModel? = null,
        val node: String? = null,
        val hasDirectMessages: Boolean = false,
        val hasGallery: Boolean = false,
        val anonymousChangeNodeName: String = "",
        val anonymousChangeNodeNameError: ValidationError? = null,
        val availableAccounts: List<AccountModel> = emptyList(),
    )

    sealed interface Effect {
        data object AnonymousChangeNodeSuccess : Effect
    }
}
