package com.livefast.eattrash.raccoonforfriendica.feature.drawer.switchinstance

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError

@Stable
interface SwitchInstanceMviModel :
    MviModel<SwitchInstanceMviModel.Intent, SwitchInstanceMviModel.State, SwitchInstanceMviModel.Effect> {

    sealed interface Intent {
        data class SetInstanceName(val name: String) : Intent

        data object Submit : Intent

        data object Reset : Intent
    }

    data class State(
        val node: String = "",
        val validationInProgress: Boolean = false,
        val nodeError: ValidationError? = null,
    )

    sealed interface Effect {
        data object ChangeInstanceSuccess : Effect
    }
}
