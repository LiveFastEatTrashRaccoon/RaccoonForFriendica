package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import com.livefast.eattrash.raccoonforfriendica.SentryConfigurationValues
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import io.sentry.Sentry
import io.sentry.UserFeedback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultCrashReportManager(
    private val keyStore: TemporaryKeyStore,
) : CrashReportManager {

    override val enabled: StateFlow<Boolean> field = MutableStateFlow(false)

    override val restartRequired: StateFlow<Boolean> field = MutableStateFlow(false)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        scope.launch {
            enabled.update {
                keyStore.get(KEY_CRASH_REPORT_ENABLED, false)
            }
        }
    }

    override fun enable() {
        scope.launch {
            keyStore.save(KEY_CRASH_REPORT_ENABLED, true)
            enabled.update { true }
            restartRequired.update { true }
        }
    }

    override fun disable() {
        scope.launch {
            keyStore.save(KEY_CRASH_REPORT_ENABLED, false)
            enabled.update { false }
            restartRequired.update { true }
        }
    }

    override fun initialize() {
        check(enabled.value) { return }
        Sentry.init { options ->
            options.dsn = SentryConfigurationValues.DSN
        }
        Thread.currentThread().apply {
            val originalHandler = uncaughtExceptionHandler
            setUncaughtExceptionHandler { t, exc ->
                Sentry.captureException(exc)
                originalHandler?.uncaughtException(t, exc)
            }
        }
    }

    override fun collectUserFeedback(
        tag: CrashReportTag,
        comment: String,
        email: String?,
    ) {
        check(enabled.value) { return }
        val eventId = Sentry.captureMessage(tag.toMessageTag())
        val feedback =
            UserFeedback(eventId).apply {
                comments = comment
                this.email = email
            }
        Sentry.captureUserFeedback(feedback)
    }

    companion object {
        private const val KEY_CRASH_REPORT_ENABLED = "CrashReportEnabled"
    }
}
