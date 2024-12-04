package com.livefast.eattrash.raccoonforfriendica.core.utils.calendar

import org.koin.core.annotation.Single

@Single
internal actual class DefaultCalendarHelper : CalendarHelper {
    actual override val supportsExport: Boolean = false

    actual override fun export(
        title: String,
        startDate: Long,
        endDate: Long?,
        location: String?,
    ) {
        // no-op
    }
}
