package com.livefast.eattrash.raccoonforfriendica.core.utils.calendar

interface CalendarHelper {
    val supportsExport: Boolean

    fun export(
        title: String,
        startDate: Long,
        endDate: Long? = null,
        location: String? = null,
    )
}
