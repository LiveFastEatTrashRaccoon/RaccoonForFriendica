package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import kotlinx.coroutines.flow.StateFlow

interface CrashReportManager {
    val enabled: StateFlow<Boolean>
    val restartRequired: StateFlow<Boolean>

    fun enable()

    fun disable()

    fun initialize()

    fun collectUserFeedback(tag: CrashReportTag, comment: String, email: String? = null)
}
