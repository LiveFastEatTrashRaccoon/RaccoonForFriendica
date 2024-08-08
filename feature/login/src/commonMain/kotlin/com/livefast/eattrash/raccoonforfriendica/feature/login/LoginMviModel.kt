package com.livefast.eattrash.raccoonforfriendica.feature.login

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError

interface LoginMviModel :
    ScreenModel,
    MviModel<LoginMviModel.Intent, LoginMviModel.State, LoginMviModel.Effect> {
    sealed interface Intent {
        data class SetNodeName(
            val name: String,
        ) : Intent

        data class SetUsername(
            val username: String,
        ) : Intent

        data class SetPassword(
            val password: String,
        ) : Intent

        data object Submit : Intent
    }

    data class State(
        val loading: Boolean = false,
        val nodeName: String = "",
        val nodeNameError: ValidationError? = null,
        val username: String = "",
        val usernameError: ValidationError? = null,
        val password: String = "",
        val passwordError: ValidationError? = null,
    )

    sealed interface Effect {
        data object Success : Effect

        data class Failure(
            val message: String? = null,
        ) : Effect
    }
}
