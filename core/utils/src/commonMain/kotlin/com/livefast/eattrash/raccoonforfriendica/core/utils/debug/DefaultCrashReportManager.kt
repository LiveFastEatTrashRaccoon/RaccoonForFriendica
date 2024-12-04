package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
internal expect class DefaultCrashReportManager : CrashReportManager {
    override val enabled: StateFlow<Boolean>
    override val restartRequired: StateFlow<Boolean>

    override fun enable()

    override fun disable()

    override fun initialize()

    override fun collectUserFeedback(
        tag: CrashReportTag,
        comment: String,
        email: String?,
    )
}
