package com.livefast.eattrash.raccoonforfriendica.feature.settings.timelinetypes

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType

interface ConfigureTimelineTypesMvi :
    Mvi<ConfigureTimelineTypesMvi.Intent, ConfigureTimelineTypesMvi.State, ConfigureTimelineTypesMvi.Effect> {
    sealed interface Intent {
        data object EnterReorderMode : Intent
        data object ExitReorderMode : Intent
        data class Add(val type: TimelineType) : Intent
        data class Delete(val type: TimelineType) : Intent
        data class Reorder(val fromIndex: Int, val toIndex: Int) : Intent
    }

    data class State(val types: List<TimelineType> = emptyList(), val reorderMode: Boolean = false)

    sealed interface Effect
}
