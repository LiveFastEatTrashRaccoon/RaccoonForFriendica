package com.livefast.eattrash.raccoonforfriendica.core.utils.datetime

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAccessor
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

actual fun epochMillis(): Long = System.currentTimeMillis()

private fun getDateTimeFormatter(pattern: String) =
    DateTimeFormatter
        .ofPattern(pattern)
        .withLocale(Locale.US)
        .withZone(TimeZone.getTimeZone("UTC").toZoneId())

private val defaultFormatter = getDateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
private val backupFormatter = getDateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

private fun String.tryParse(): TemporalAccessor? {
    var res: TemporalAccessor? =
        runCatching { defaultFormatter.parse(this) }.getOrNull()
    if (res == null) {
        res = runCatching { backupFormatter.parse(this) }.getOrNull()
    }
    return res
}

private fun getDateFromIso8601Timestamp(string: String): ZonedDateTime = ZonedDateTime.parse(string)

actual fun Long.toIso8601Timestamp(withLocalTimezone: Boolean): String? {
    val offset =
        if (withLocalTimezone) {
            OffsetDateTime.now().offset
        } else {
            ZoneOffset.UTC
        }
    val date = LocalDateTime.ofEpochSecond(this / 1000, 0, offset)
    return defaultFormatter.format(date)
}

actual fun String.toEpochMillis(): Long {
    val accessor = tryParse() ?: return 0
    val instant = Instant.from(accessor)
    return Instant.EPOCH.until(instant, ChronoUnit.MILLIS)
}

actual fun Long.concatDateWithTime(
    hours: Int,
    minutes: Int,
    seconds: Int,
): Long {
    val calendar = GregorianCalendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.HOUR, hours)
    calendar.set(Calendar.MINUTE, minutes)
    calendar.set(Calendar.SECOND, seconds)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

actual fun getFormattedDate(
    iso8601Timestamp: String,
    format: String,
    withLocalTimezone: Boolean,
): String {
    val date = getDateFromIso8601Timestamp(iso8601Timestamp)
    val formatter =
        DateTimeFormatter.ofPattern(format).run {
            if (withLocalTimezone) {
                withZone(ZoneOffset.systemDefault())
            } else {
                this
            }
        }
    return date.format(formatter)
}

actual fun parseDate(
    value: String,
    format: String,
): String =
    getDateTimeFormatter(format)
        .runCatching {
            parse(value)
        }.getOrNull()
        ?.let { defaultFormatter.format(it) }
        .orEmpty()

actual fun getPrettyDate(
    iso8601Timestamp: String,
    yearLabel: String,
    monthLabel: String,
    dayLabel: String,
    hourLabel: String,
    minuteLabel: String,
    secondLabel: String,
): String {
    val now = ZonedDateTime.now()
    val date = getDateFromIso8601Timestamp(iso8601Timestamp)
    val delta = Period.between(date.toLocalDate(), now.toLocalDate())
    val years = delta.years
    val months = delta.months
    val days = delta.days
    val nowSeconds = now.toEpochSecond()
    val dateSeconds = date.toEpochSecond()
    val diffSeconds = abs(nowSeconds - dateSeconds)
    val hours = ((diffSeconds % 86400) / 3600) % 24
    val minutes = ((diffSeconds % 3600) / 60) % 60
    val seconds = diffSeconds % 60
    return when {
        years >= 1 ->
            buildString {
                append("${years}$yearLabel")
                if (months >= 1) {
                    append(" ${months}$monthLabel")
                }
                if (days >= 1) {
                    append(" ${days}$dayLabel")
                }
            }

        months >= 1 ->
            buildString {
                append("${months}$monthLabel")
                if (days >= 1) {
                    append(" ${days}$dayLabel")
                }
            }

        days >= 1 ->
            buildString {
                append("${days}$dayLabel")
            }

        hours >= 1 ->
            buildString {
                append(" ${hours}$hourLabel")
            }

        minutes >= 1 ->
            buildString {
                append(" ${minutes}$minuteLabel")
            }

        else ->
            buildString {
                append(" ${seconds}$secondLabel")
            }
    }
}

actual fun getDurationFromNowToDate(iso8601Timestamp: String): Duration? =
    runCatching {
        val date = getDateFromIso8601Timestamp(iso8601Timestamp).toOffsetDateTime()
        val now = ZonedDateTime.now()
        val duration = JavaDuration.between(now, date)
        duration.toKotlinDuration()
    }.getOrNull()

actual fun getDurationFromDateToNow(iso8601Timestamp: String): Duration? =
    runCatching {
        val date = getDateFromIso8601Timestamp(iso8601Timestamp).toOffsetDateTime()
        val now = ZonedDateTime.now()
        val duration = JavaDuration.between(date, now)
        duration.toKotlinDuration()
    }.getOrNull()

actual fun isToday(iso8601Timestamp: String): Boolean {
    val millis = iso8601Timestamp.toEpochMillis()
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    calendar.timeInMillis = epochMillis()
    val yearNow = calendar.get(Calendar.YEAR)
    val monthNow = calendar.get(Calendar.MONTH)
    val dayNow = calendar.get(Calendar.DAY_OF_MONTH)

    return year == yearNow && month == monthNow && day == dayNow
}
