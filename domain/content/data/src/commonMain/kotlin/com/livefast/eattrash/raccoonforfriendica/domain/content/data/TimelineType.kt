package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources

sealed interface TimelineType {
    data object All : TimelineType

    data object Subscriptions : TimelineType

    data object Local : TimelineType

    data class Circle(val circle: CircleModel? = null) : TimelineType

    data class Foreign(val otherInstance: String) : TimelineType
}

@Composable
fun TimelineType.toReadableName(): String = when (this) {
    TimelineType.All -> LocalStrings.current.timelineAll
    TimelineType.Subscriptions -> LocalStrings.current.timelineSubscriptions
    TimelineType.Local -> LocalStrings.current.timelineLocal
    is TimelineType.Circle -> circle?.name.orEmpty()
    is TimelineType.Foreign -> otherInstance
}

fun TimelineType.toInt(): Int = when (this) {
    TimelineType.All -> 1
    TimelineType.Subscriptions -> 2
    is TimelineType.Circle -> 3
    is TimelineType.Foreign -> 4
    else -> 0
}

fun Int.toTimelineType(): TimelineType = when (this) {
    1 -> TimelineType.All
    2 -> TimelineType.Subscriptions
    3 -> TimelineType.Circle()
    4 -> TimelineType.Foreign("")
    else -> TimelineType.Local
}

@Composable
fun TimelineType.toIcon(): ImageVector = when (this) {
    TimelineType.All -> LocalResources.current.public
    TimelineType.Local -> LocalResources.current.cottage
    TimelineType.Subscriptions -> LocalResources.current.book
    is TimelineType.Circle -> (circle?.type ?: CircleType.Other).toIcon()
    is TimelineType.Foreign -> LocalResources.current.exploreFill
}
