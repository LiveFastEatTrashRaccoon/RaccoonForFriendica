package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType

interface TimelineMviModel :
    ScreenModel,
    MviModel<TimelineMviModel.Intent, TimelineMviModel.State, TimelineMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeType(
            val type: TimelineType,
        ) : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val nodeName: String = "",
        val timelineType: TimelineType = TimelineType.Local,
        val availableTimelineTypes: List<TimelineType> = emptyList(),
        val entries: List<TimelineEntryModel> = emptyList(),
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
