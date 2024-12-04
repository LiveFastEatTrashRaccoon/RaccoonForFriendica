package com.livefast.eattrash.raccoonforfriendica.core.utils.calendar

import org.koin.core.annotation.Single

@Single
internal expect class DefaultCalendarHelper : CalendarHelper {
    override val supportsExport: Boolean

    override fun export(
        title: String,
        startDate: Long,
        endDate: Long?,
        location: String?,
    )
}
