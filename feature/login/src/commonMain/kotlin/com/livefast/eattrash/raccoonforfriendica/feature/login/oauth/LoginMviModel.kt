package com.livefast.eattrash.raccoonforfriendica.feature.login.oauth

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError

interface LoginMviModel :
    ScreenModel,
    MviModel<LoginMviModel.Intent, LoginMviModel.State, LoginMviModel.Effect> {
    sealed interface Intent {
        data class SetNodeName(val name: String) : Intent

        data object SignUp : Intent

        data object Submit : Intent
    }

    data class State(
        val useDropDown: Boolean = true,
        val nodeName: String = "",
        val nodeNameError: ValidationError? = null,
        val loading: Boolean = false,
    )

    sealed interface Effect {
        data class OpenUrl(val url: String) : Effect

        data object Success : Effect

        data class Failure(val message: String? = null) : Effect

        data class OpenWebRegistration(val url: String) : Effect
    }
}
