package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import platform.Foundation.NSBundle

internal class DefaultCrashReportManager : CrashReportManager {
    override fun initialize() {
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
        val eventId = Sentry.captureMessage(tag.toMessageTag())
        val feedback =
            UserFeedback(eventId).apply {
                comments = comment
                this.email = email
            }
        Sentry.captureUserFeedback(feedback)
    }
}
