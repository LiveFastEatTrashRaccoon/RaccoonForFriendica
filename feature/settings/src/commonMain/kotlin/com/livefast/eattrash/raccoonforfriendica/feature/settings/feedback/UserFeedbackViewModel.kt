package com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportTag
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.isValidEmail
import kotlinx.coroutines.launch

class UserFeedbackViewModel(
    private val crashReportManager: CrashReportManager,
) : DefaultMviModel<UserFeedbackMviModel.Intent, UserFeedbackMviModel.State, UserFeedbackMviModel.Effect>(
        initialState = UserFeedbackMviModel.State(),
    ),
    UserFeedbackMviModel {
    override fun reduce(intent: UserFeedbackMviModel.Intent) {
        when (intent) {
            is UserFeedbackMviModel.Intent.SetComment ->
                screenModelScope.launch {
                    updateState { it.copy(comment = intent.comment) }
                }

            is UserFeedbackMviModel.Intent.SetEmail ->
                screenModelScope.launch {
                    updateState { it.copy(email = intent.email) }
                }

            UserFeedbackMviModel.Intent.Submit -> submit()
        }
    }

    private fun submit() {
        screenModelScope.launch {
            val currentState = uiState.value
            val email = currentState.email
            val comment = currentState.comment

            // validate fields
            val commentError =
                if (comment.isBlank()) {
                    ValidationError.MissingField
                } else {
                    null
                }
            val emailError =
                if (email.isValidEmail()) {
                    null
                } else {
                    ValidationError.InvalidField
                }
            updateState {
                it.copy(
                    commentError = commentError,
                    emailError = emailError,
                )
            }
            val isValid = commentError == null && emailError == null
            if (!isValid) {
                return@launch
            }

            updateState { it.copy(loading = true) }
            try {
                crashReportManager.collectUserFeedback(
                    tag = CrashReportTag.ReportFromAbout,
                    email = email.takeIf { it.isNotBlank() },
                    comment = comment,
                )
                emitEffect(UserFeedbackMviModel.Effect.Success)
            } catch (e: Throwable) {
                updateState { it.copy(loading = false) }
                emitEffect(UserFeedbackMviModel.Effect.Failure(e.message))
            }
        }
    }
}