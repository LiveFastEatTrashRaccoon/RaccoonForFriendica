package com.livefast.eattrash.raccoonforfriendica.core.utils.calendar

import android.content.Intent
import android.provider.CalendarContract
import io.sentry.kotlin.multiplatform.Context

internal class DefaultCalendarHelper(private val context: Context) : CalendarHelper {
    override val supportsExport: Boolean = true

    override fun export(title: String, startDate: Long, endDate: Long?, location: String?) {
        val intent =
            Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(CalendarContract.Events.TITLE, title)
                if (location != null) {
                    putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                }
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate)
                if (endDate != null) {
                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate)
                } else {
                    putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                }
            }
        runCatching {
            context.startActivity(intent)
        }
    }
}
