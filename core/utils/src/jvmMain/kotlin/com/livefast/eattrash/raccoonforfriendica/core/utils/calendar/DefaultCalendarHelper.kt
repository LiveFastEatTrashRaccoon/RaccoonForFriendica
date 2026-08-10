package com.livefast.eattrash.raccoonforfriendica.core.utils.calendar

import org.koin.core.annotation.Single

@Single
internal class DefaultCalendarHelper : CalendarHelper {
    override val supportsExport = false

    override fun export(title: String, startDate: Long, endDate: Long?, location: String?) {
        // TODO(jvm): implement
    }
}
