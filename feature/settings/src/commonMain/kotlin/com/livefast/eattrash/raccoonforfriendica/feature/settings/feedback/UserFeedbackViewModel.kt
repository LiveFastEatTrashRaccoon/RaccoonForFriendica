package com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportTag
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.isValidEmail
import kotlinx.coroutines.launch

class UserFeedbackViewModel(private val crashReportManager: CrashReportManager) :
    ViewModel(),
    MviModelDelegate<UserFeedbackMviModel.Intent, UserFeedbackMviModel.State, UserFeedbackMviModel.Effect>
    by DefaultMviModelDelegate(initialState = UserFeedbackMviModel.State()),
    UserFeedbackMviModel {
    override fun reduce(intent: UserFeedbackMviModel.Intent) {
        when (intent) {
            is UserFeedbackMviModel.Intent.SetComment ->
                viewModelScope.launch {
                    updateState { it.copy(comment = intent.comment) }
                }

            is UserFeedbackMviModel.Intent.SetEmail ->
                viewModelScope.launch {
                    updateState { it.copy(email = intent.email) }
                }

            UserFeedbackMviModel.Intent.Submit -> submit()
        }
    }

    private fun submit() {
        viewModelScope.launch {
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
            check(isValid) { return@launch }

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
