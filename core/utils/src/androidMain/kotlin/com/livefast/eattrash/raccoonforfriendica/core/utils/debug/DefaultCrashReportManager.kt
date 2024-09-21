package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.core.utils.R
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal class DefaultCrashReportManager(
    private val context: Context,
) : CrashReportManager {
    override fun initialize() {
        Sentry.init { options ->
            options.dsn = context.getString(R.string.sentry_dsn)
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
        val eventId = Sentry.captureMessage(tag.toMessageTag())
        val feedback =
            UserFeedback(eventId).apply {
                comments = comment
                this.email = email
            }
        Sentry.captureUserFeedback(feedback)
    }
}
