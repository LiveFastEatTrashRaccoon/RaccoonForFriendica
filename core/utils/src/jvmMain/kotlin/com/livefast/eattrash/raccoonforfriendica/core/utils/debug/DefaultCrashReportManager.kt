package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class DefaultCrashReportManager : CrashReportManager {

    private val _enabled = MutableStateFlow(false)
    override val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _restartRequired = MutableStateFlow(false)
    override val restartRequired: StateFlow<Boolean> = _restartRequired.asStateFlow()

    override fun enable() {
        // TODO(jvm): implement
        // no-op
    }

    override fun disable() {
        // TODO(jvm): implement
        // no-op
    }

    override fun initialize() {
        // TODO(jvm): implement
        // no-op
    }

    override fun collectUserFeedback(
        tag: CrashReportTag,
        comment: String,
        email: String?,
    ) {
        // TODO(jvm): implement
        // no-op
    }
}
