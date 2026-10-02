package com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportTag
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.isValidEmail
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class UserFeedbackViewModel(private val crashReportManager: CrashReportManager) :
    ViewModel(),
    MviDelegate<UserFeedbackMvi.Intent, UserFeedbackMvi.State, UserFeedbackMvi.Effect>
    by DefaultMviDelegate(initialState = UserFeedbackMvi.State()),
    UserFeedbackMvi {
    override fun reduce(intent: UserFeedbackMvi.Intent) {
        when (intent) {
            is UserFeedbackMvi.Intent.SetComment ->
                viewModelScope.launch {
                    updateState { it.copy(comment = intent.comment) }
                }

            is UserFeedbackMvi.Intent.SetEmail ->
                viewModelScope.launch {
                    updateState { it.copy(email = intent.email) }
                }

            UserFeedbackMvi.Intent.Submit -> submit()
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
                emitEffect(UserFeedbackMvi.Effect.Success)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                updateState { it.copy(loading = false) }
                emitEffect(UserFeedbackMvi.Effect.Failure(e.message))
            }
        }
    }
}
