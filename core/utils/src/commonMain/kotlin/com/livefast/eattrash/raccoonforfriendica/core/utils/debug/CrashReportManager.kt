package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import androidx.compose.runtime.Stable

@Stable
interface CrashReportManager {
    fun initialize()

    fun collectUserFeedback(
        tag: CrashReportTag,
        comment: String,
        email: String? = null,
    )
}
