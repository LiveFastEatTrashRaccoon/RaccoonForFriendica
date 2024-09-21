package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

sealed interface CrashReportTag {
    data object ReportFromAbout : CrashReportTag
}

internal fun CrashReportTag.toMessageTag(): String =
    when (this) {
        CrashReportTag.ReportFromAbout -> "AppFeedback"
}
