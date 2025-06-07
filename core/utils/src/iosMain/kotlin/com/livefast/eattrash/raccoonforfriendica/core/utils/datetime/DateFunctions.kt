package com.livefast.eattrash.raccoonforfriendica.core.utils.datetime

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSISO8601DateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.autoupdatingCurrentLocale
import platform.Foundation.defaultTimeZone
import platform.Foundation.localTimeZone
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.timeIntervalSinceDate
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

actual fun epochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

private fun getDateFormatter(format: String, withLocalTimezone: Boolean = false) = NSDateFormatter().apply {
    locale = NSLocale.autoupdatingCurrentLocale
    timeZone =
        if (withLocalTimezone) {
            NSTimeZone.localTimeZone
        } else {
            NSTimeZone.defaultTimeZone
        }
    dateFormat = format
    calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian)
}

private val defaultFormatter = getDateFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
private val backupFormatter = getDateFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

private fun String.tryParse(): NSDate? {
    var res = runCatching { defaultFormatter.dateFromString(this) }.getOrNull()
    if (res == null) {
        res = runCatching { backupFormatter.dateFromString(this) }.getOrNull()
    }
    return res
}

private fun getDateFromIso8601Timestamp(string: String): NSDate? = NSISO8601DateFormatter().dateFromString(string)

actual fun Long.toIso8601Timestamp(withLocalTimezone: Boolean): String? {
    val date = NSDate(timeIntervalSinceReferenceDate = (this.toDouble() / 1000))
    return defaultFormatter
        .apply {
            if (withLocalTimezone) {
                timeZone = NSTimeZone.localTimeZone
            }
        }.stringFromDate(date)
}

actual fun String.toEpochMillis(): Long {
    val date = tryParse() ?: return 0
    return (date.timeIntervalSince1970 * 1000).toLong()
}

actual fun Long.concatDateWithTime(hours: Int, minutes: Int, seconds: Int): Long {
    val date = NSDate(timeIntervalSinceReferenceDate = (this.toDouble() / 1000))
    val calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian)
    val dateComponents =
        calendar.components(
            unitFlags =
            NSCalendarUnitSecond
                .or(NSCalendarUnitMinute)
                .or(NSCalendarUnitHour)
                .or(NSCalendarUnitDay)
                .or(NSCalendarUnitMonth)
                .or(NSCalendarUnitYear),
            fromDate = date,
        )
    dateComponents.hour = hours.toLong()
    dateComponents.minute = minutes.toLong()
    dateComponents.second = seconds.toLong()

    val result = calendar.dateFromComponents(dateComponents) ?: return 0
    return (result.timeIntervalSince1970 * 1000).toLong()
}

actual fun Long.extractTimePart(): Pair<Int, Int> {
    val date = NSDate(timeIntervalSinceReferenceDate = (this.toDouble() / 1000))
    val calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian)
    val dateComponents =
        calendar.components(
            unitFlags =
            NSCalendarUnitSecond
                .or(NSCalendarUnitMinute)
                .or(NSCalendarUnitHour),
            fromDate = date,
        )
    val hours = dateComponents.hour.toInt()
    val minutes = dateComponents.minute.toInt()
    return hours to minutes
}

actual fun Long.extractDatePart(): Pair<Int, Int> {
    val date = NSDate(timeIntervalSinceReferenceDate = (this.toDouble() / 1000))
    val calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian)
    val dateComponents =
        calendar.components(
            unitFlags =
            NSCalendarUnitSecond
                .or(NSCalendarUnitMonth)
                .or(NSCalendarUnitYear),
            fromDate = date,
        )
    val year = dateComponents.year.toInt()
    val month = dateComponents.month.toInt()
    return year to month
}

actual fun getFormattedDate(iso8601Timestamp: String, format: String, withLocalTimezone: Boolean): String {
    val date = getDateFromIso8601Timestamp(iso8601Timestamp) ?: return ""
    val dateFormatter =
        getDateFormatter(format).run {
            if (withLocalTimezone) {
                timeZone = NSTimeZone.localTimeZone
            }
            this
        }
    return dateFormatter.stringFromDate(date)
}

actual fun parseDate(value: String, format: String, withLocalTimezone: Boolean): String {
    val dateFormatter =
        getDateFormatter(
            format = format,
            withLocalTimezone = withLocalTimezone,
        )
    val date =
        runCatching {
            dateFormatter.dateFromString(value)
        }.getOrNull() ?: return ""
    return NSISO8601DateFormatter().stringFromDate(date)
}

actual fun getPrettyDate(
    iso8601Timestamp: String,
    yearLabel: String,
    monthLabel: String,
    dayLabel: String,
    hourLabel: String,
    minuteLabel: String,
    secondLabel: String,
    finePrecision: Boolean,
): String {
    val date = getDateFromIso8601Timestamp(iso8601Timestamp) ?: return ""
    val now = NSDate()
    val calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian)
    val delta =
        calendar.components(
            unitFlags =
            NSCalendarUnitSecond
                .or(NSCalendarUnitMinute)
                .or(NSCalendarUnitHour)
                .or(NSCalendarUnitDay)
                .or(NSCalendarUnitMonth)
                .or(NSCalendarUnitYear),
            fromDate = date,
            toDate = now,
            options = 0u,
        )
    return when {
        delta.year >= 1 ->
            buildString {
                append("${delta.year}$yearLabel")
                if (finePrecision) {
                    if (delta.month > 0) {
                        append(" ${delta.month}$monthLabel")
                    }
                    if (delta.day > 0) {
                        append(" ${delta.day}$dayLabel")
                    }
                }
            }

        delta.month >= 1 ->
            buildString {
                append("${delta.month}$monthLabel")
                if (finePrecision) {
                    if (delta.day > 0) {
                        append(" ${delta.day}$dayLabel")
                    }
                }
            }

        delta.day >= 1 ->
            buildString {
                append("${delta.day}$dayLabel")
                if (finePrecision) {
                    if (delta.hour > 0 || delta.minute > 0) {
                        append(" ${delta.hour}$hourLabel")
                    }
                    // minutes and seconds are intentionally omitted
                }
            }

        delta.hour >= 1 ->
            buildString {
                append(" ${delta.hour}$hourLabel")
                // minutes and seconds are intentionally omitted
            }

        delta.minute >= 1 ->
            buildString {
                append(" ${delta.minute}$minuteLabel")
                // seconds are intentionally omitted
            }

        else ->
            buildString {
                append(" ${delta.second}$secondLabel")
            }
    }
}

actual fun getDurationFromNowToDate(iso8601Timestamp: String): Duration? {
    val date = getDateFromIso8601Timestamp(iso8601Timestamp) ?: return null
    val interval = date.timeIntervalSinceDate(NSDate())
    return interval.toDuration(DurationUnit.SECONDS)
}

actual fun getDurationFromDateToNow(iso8601Timestamp: String): Duration? {
    val date = getDateFromIso8601Timestamp(iso8601Timestamp) ?: return null
    val interval = NSDate().timeIntervalSinceDate(date)
    return interval.toDuration(DurationUnit.SECONDS)
}

actual fun isToday(iso8601Timestamp: String): Boolean {
    val date = iso8601Timestamp.tryParse() ?: return false
    val calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian)
    val dateComponents =
        calendar.components(
            unitFlags =
            NSCalendarUnitDay
                .or(NSCalendarUnitMonth)
                .or(NSCalendarUnitYear),
            fromDate = date,
        )
    val year = dateComponents.year
    val month = dateComponents.month
    val day = dateComponents.day

    val componentsNow =
        calendar.components(
            unitFlags =
            NSCalendarUnitDay
                .or(NSCalendarUnitMonth)
                .or(NSCalendarUnitYear),
            fromDate = NSDate(),
        )
    val yearNow = componentsNow.year
    val monthNow = componentsNow.month
    val dayNow = componentsNow.day

    return year == yearNow && month == monthNow && day == dayNow
}
