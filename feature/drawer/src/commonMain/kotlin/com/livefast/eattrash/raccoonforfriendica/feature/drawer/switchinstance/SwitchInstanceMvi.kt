package com.livefast.eattrash.raccoonforfriendica.feature.drawer.switchinstance

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError

interface SwitchInstanceMvi : Mvi<SwitchInstanceMvi.Intent, SwitchInstanceMvi.State, SwitchInstanceMvi.Effect> {

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
