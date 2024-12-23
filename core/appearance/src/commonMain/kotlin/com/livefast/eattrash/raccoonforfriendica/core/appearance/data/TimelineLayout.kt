package com.livefast.eattrash.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

enum class TimelineLayout {
    Full,
    DistractionFree,
}

fun TimelineLayout.toInt(): Int =
    when (this) {
        TimelineLayout.DistractionFree -> 1
        else -> 0
    }

fun Int.toTimelineLayout(): TimelineLayout =
    when (this) {
        1 -> TimelineLayout.DistractionFree
        else -> TimelineLayout.Full
}

@Composable
fun TimelineLayout.toReadableName(): String =
    when (this) {
        TimelineLayout.Full -> LocalStrings.current.timelineLayoutFull
        TimelineLayout.DistractionFree -> LocalStrings.current.timelineLayoutDistractionFree
}
