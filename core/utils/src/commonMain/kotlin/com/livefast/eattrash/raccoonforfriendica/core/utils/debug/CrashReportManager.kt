package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface CrashReportManager {
    val enabled: StateFlow<Boolean>
    val restartRequired: StateFlow<Boolean>

    fun enable()

    fun disable()

    fun initialize()

    fun collectUserFeedback(tag: CrashReportTag, comment: String, email: String? = null)
}
