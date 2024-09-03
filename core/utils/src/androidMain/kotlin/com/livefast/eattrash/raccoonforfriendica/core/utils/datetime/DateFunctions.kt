package com.livefast.eattrash.raccoonforfriendica.core.utils.datetime

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.Period
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

actual fun epochMillis(): Long = System.currentTimeMillis()

private val formatter =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

actual fun Long.toIso8601Timestamp(): String? {
    val date = Date(this)
    return formatter.format(date)
}

actual fun getFormattedDate(
    iso8601Timestamp: String,
    format: String,
): String {
    val date = getDateFromIso8601Timestamp(iso8601Timestamp)
    val formatter = DateTimeFormatter.ofPattern(format)
    return date.format(formatter)
}

actual fun parseDate(
    value: String,
    format: String,
): String =
    SimpleDateFormat(format, Locale.US)
        .apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(value)
        ?.let { formatter.format(it) }.orEmpty()

actual fun getPrettyDate(
    iso8601Timestamp: String,
    yearLabel: String,
    monthLabel: String,
    dayLabel: String,
    hourLabel: String,
    minuteLabel: String,
    secondLabel: String,
): String {
    val now = GregorianCalendar().toZonedDateTime()
    val date = getDateFromIso8601Timestamp(iso8601Timestamp)
    val delta = Period.between(date.toLocalDate(), now.toLocalDate())
    val years = delta.years
    val months = delta.months
    val days = delta.days
    val nowSeconds = now.toEpochSecond()
    val dateSeconds = date.toEpochSecond()
    val diffSeconds = (nowSeconds - dateSeconds)
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

actual fun getDurationSinceDate(iso8601Timestamp: String): Duration? {
    val date = getDateFromIso8601Timestamp(iso8601Timestamp).toLocalDateTime()
    val now = LocalDateTime.now()
    val duration = JavaDuration.between(date, now)
    return duration.toKotlinDuration()
}

private fun getDateFromIso8601Timestamp(string: String): ZonedDateTime = ZonedDateTime.parse(string)
