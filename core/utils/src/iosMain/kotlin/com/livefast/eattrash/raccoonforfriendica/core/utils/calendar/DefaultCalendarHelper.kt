package com.livefast.eattrash.raccoonforfriendica.core.utils.calendar

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultCalendarHelper : CalendarHelper {
    override val supportsExport: Boolean = false

    override fun export(title: String, startDate: Long, endDate: Long?, location: String?) {
        // no-op
    }
}
