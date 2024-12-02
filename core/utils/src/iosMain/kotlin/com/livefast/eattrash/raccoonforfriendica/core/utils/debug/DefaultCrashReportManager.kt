package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import com.livefast.eattrash.raccoonforfriendica.core.preferences.TemporaryKeyStore
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single
import platform.Foundation.NSBundle

@Single
internal class DefaultCrashReportManager(
    private val keyStore: TemporaryKeyStore,
) : CrashReportManager {
    override val enabled = MutableStateFlow(false)
    override val restartRequired = MutableStateFlow(false)

    init {
        enabled.update {
            keyStore[KEY_CRASH_REPORT_ENABLED, false]
        }
    }

    override fun enable() {
        keyStore.save(KEY_CRASH_REPORT_ENABLED, true)
        enabled.update { true }
        restartRequired.update { true }
    }

    override fun disable() {
        keyStore.save(KEY_CRASH_REPORT_ENABLED, false)
        enabled.update { false }
        restartRequired.update { true }
    }

    override fun initialize() {
        if (!enabled.value) {
            return
        }
        val dict = NSBundle.mainBundle.infoDictionary
        Sentry.init { options ->
            options.dsn = dict?.get("SENTRY_DSN") as? String ?: ""
        }
    }

    override fun collectUserFeedback(
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
