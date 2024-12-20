package com.livefast.eattrash.raccoonforfriendica.core.utils.calendar

internal class DefaultCalendarHelper : CalendarHelper {
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
