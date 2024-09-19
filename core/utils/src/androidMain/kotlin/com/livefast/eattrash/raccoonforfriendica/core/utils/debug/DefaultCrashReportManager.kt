package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.core.utils.R
import io.sentry.kotlin.multiplatform.Sentry

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
}
