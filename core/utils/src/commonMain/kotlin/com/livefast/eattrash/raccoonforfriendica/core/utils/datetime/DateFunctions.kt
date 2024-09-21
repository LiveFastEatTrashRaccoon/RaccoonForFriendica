package com.livefast.eattrash.raccoonforfriendica.core.utils.datetime

import kotlin.time.Duration

/**
 * Get the timestamp (in millis) for the current moment from the beginning of the epoch.
 */
expect fun epochMillis(): Long

/**
 * Convert from a timestamp (in milliseconds) to an ISO-8601 string date.
 */
expect fun Long.toIso8601Timestamp(withLocalTimezone: Boolean = true): String?

/**
 * Convert from an ISO-8601 string date to a timestamp (in milliseconds).
 */
expect fun String.toEpochMillis(): Long

/**
 * Updates the time part of a timestamp (in milliseconds).
 */
expect fun Long.concatDateWithTime(
    hours: Int,
    minutes: Int,
    seconds: Int,
): Long

/**
 * Extract the time (hours, minutes) from a timestamp (in milliseconds).
 */
expect fun Long.extractTimePart(): Pair<Int, Int>

/**
 * Converts a date (in the ISO 8601 format) to another given format.
 */
expect fun getFormattedDate(
    iso8601Timestamp: String,
    format: String,
    withLocalTimezone: Boolean = true,
): String

/**
 * Parse a date (as String) given its format to an ISO-8601 string date.
 */
expect fun parseDate(
    value: String,
    format: String,
): String

/**
 * Format the amount of type elapsed from a date until now.
 */
expect fun getPrettyDate(
    iso8601Timestamp: String,
    yearLabel: String,
    monthLabel: String,
    dayLabel: String,
    hourLabel: String,
    minuteLabel: String,
    secondLabel: String,
): String

/**
 * Get the time from now to a future date.
 */
expect fun getDurationFromNowToDate(iso8601Timestamp: String): Duration?

/**
 * Get the time from now a past date to now.
 */
expect fun getDurationFromDateToNow(iso8601Timestamp: String): Duration?

/**
 * Determines whether the date represented in the ISO-8601 string belongs to the current day
 */
expect fun isToday(iso8601Timestamp: String): Boolean
