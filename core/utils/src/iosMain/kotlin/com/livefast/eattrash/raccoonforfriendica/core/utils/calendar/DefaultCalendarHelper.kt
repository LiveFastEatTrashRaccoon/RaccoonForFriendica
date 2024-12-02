package com.livefast.eattrash.raccoonforfriendica.core.utils.calendar

import org.koin.core.annotation.Single

@Single
class DefaultCalendarHelper : CalendarHelper {
    override val supportsExport: Boolean = false

    override fun export(
        title: String,
        startDate: Long,
        endDate: Long?,
        location: String?,
    ) {
        // no-op
    }
}
