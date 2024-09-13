package com.livefast.eattrash.raccoonforfriendica.core.utils.datetime

import kotlin.time.Duration

/**
 * Get the timestamp (in millis) for the current moment from the beginning of the epoch.
 */
expect fun epochMillis(): Long

/**
 * Convert from a timestamp (in milliseconds) to an ISO-8601 string date.
 */
expect fun Long.toIso8601Timestamp(): String?

/**
 * Converts a date (in the ISO 8601 format) to another given format.
 */
expect fun getFormattedDate(
    iso8601Timestamp: String,
    format: String,
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
