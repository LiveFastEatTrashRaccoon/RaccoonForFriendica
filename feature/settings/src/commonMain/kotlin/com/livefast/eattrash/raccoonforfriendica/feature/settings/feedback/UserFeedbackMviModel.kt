package com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError

@Stable
interface UserFeedbackMviModel :
    ScreenModel,
    MviModel<UserFeedbackMviModel.Intent, UserFeedbackMviModel.State, UserFeedbackMviModel.Effect> {
    sealed interface Intent {
        data class SetComment(
            val comment: String,
        ) : Intent

        data class SetEmail(
            val email: String,
        ) : Intent

        data object Submit : Intent
    }

    data class State(
        val loading: Boolean = false,
        val comment: String = "",
        val commentError: ValidationError? = null,
        val email: String = "",
        val emailError: ValidationError? = null,
    )

    sealed interface Effect {
        data object Success : Effect

        data class Failure(
            val message: String?,
        ) : Effect
    }
}
