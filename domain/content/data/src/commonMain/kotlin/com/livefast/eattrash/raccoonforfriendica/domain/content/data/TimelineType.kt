package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

sealed interface TimelineType {
    data object All : TimelineType

    data object Subscriptions : TimelineType

    data object Local : TimelineType

    data class Circle(val circle: CircleModel? = null) : TimelineType

    data class Foreign(val node: String) : TimelineType
}

@Composable
fun TimelineType.toReadableName(): String = when (this) {
    TimelineType.All -> LocalStrings.current.timelineAll
    TimelineType.Subscriptions -> LocalStrings.current.timelineSubscriptions
    TimelineType.Local -> LocalStrings.current.timelineLocal
    is TimelineType.Circle -> circle?.name.orEmpty()
    is TimelineType.Foreign -> node
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
fun TimelineType.toIcon(coreResources: CoreResources): ImageVector = when (this) {
    TimelineType.All -> coreResources.public
    TimelineType.Local -> coreResources.cottage
    TimelineType.Subscriptions -> coreResources.book
    is TimelineType.Circle -> (circle?.type ?: CircleType.Other).toIcon(coreResources)
    is TimelineType.Foreign -> coreResources.exploreFill
}
