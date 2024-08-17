package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface TimelineType {
    data object All : TimelineType

    data object Subscriptions : TimelineType

    data object Local : TimelineType
}

@Composable
fun TimelineType.toReadableName(): String =
    when (this) {
        TimelineType.All -> LocalStrings.current.timelineAll
        TimelineType.Subscriptions -> LocalStrings.current.timelineSubscriptions
        TimelineType.Local -> LocalStrings.current.timelineLocal
    }

fun TimelineType.toInt(): Int =
    when (this) {
        TimelineType.All -> 1
        TimelineType.Local -> 0
        TimelineType.Subscriptions -> 2
    }

fun Int.toTimelineType(): TimelineType =
    when (this) {
        1 -> TimelineType.All
        2 -> TimelineType.Subscriptions
        else -> TimelineType.Local
    }
