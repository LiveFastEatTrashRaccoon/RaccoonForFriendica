package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single
import platform.Foundation.NSBundle

@Single
internal actual class DefaultCrashReportManager(
    private val keyStore: TemporaryKeyStore,
) : CrashReportManager {
    private val _enabled = MutableStateFlow(false)
    actual override val enabled: StateFlow<Boolean> = _enabled
    private val _restartRequired = MutableStateFlow(false)
    actual override val restartRequired: StateFlow<Boolean> = _restartRequired

    init {
        _enabled.update {
            keyStore[KEY_CRASH_REPORT_ENABLED, false]
        }
    }

    actual override fun enable() {
        keyStore.save(KEY_CRASH_REPORT_ENABLED, true)
        _enabled.update { true }
        _restartRequired.update { true }
    }

    actual override fun disable() {
        keyStore.save(KEY_CRASH_REPORT_ENABLED, false)
        _enabled.update { false }
        _restartRequired.update { true }
    }

    actual override fun initialize() {
        if (!enabled.value) {
            return
        }
        val dict = NSBundle.mainBundle.infoDictionary
        Sentry.init { options ->
            options.dsn = dict?.get("SENTRY_DSN") as? String ?: ""
        }
    }

    actual override fun collectUserFeedback(
        tag: CrashReportTag,
        comment: String,
        email: String?,
    ) {
        if (!enabled.value) {
            return
        }
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
