package com.livefast.eattrash.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

enum class TimelineLayout {
    Full,
    DistractionFree,
    Compact,
    Card,
}

fun TimelineLayout.toInt(): Int = when (this) {
    TimelineLayout.DistractionFree -> 1
    TimelineLayout.Compact -> 2
    TimelineLayout.Card -> 3
    else -> 0
}

fun Int.toTimelineLayout(): TimelineLayout = when (this) {
    1 -> TimelineLayout.DistractionFree
    2 -> TimelineLayout.Compact
    3 -> TimelineLayout.Card
    else -> TimelineLayout.Full
}

@Composable
fun TimelineLayout.toReadableName(): String = when (this) {
    TimelineLayout.Full -> LocalStrings.current.timelineLayoutFull
    TimelineLayout.DistractionFree -> LocalStrings.current.timelineLayoutDistractionFree
    TimelineLayout.Compact -> LocalStrings.current.timelineLayoutCompact
    TimelineLayout.Card -> LocalStrings.current.timelineLayoutCard
}
