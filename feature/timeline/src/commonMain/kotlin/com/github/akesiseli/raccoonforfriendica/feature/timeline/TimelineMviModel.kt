package com.github.akesiseli.raccoonforfriendica.feature.timeline

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.akesiseli.raccoonforfriendica.core.architecture.MviModel
import com.github.akesiseli.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.github.akesiseli.raccoonforfriendica.domain.content.data.TimelineType

interface TimelineMviModel :
    ScreenModel,
    MviModel<TimelineMviModel.Intent, TimelineMviModel.State, TimelineMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val nodeName: String = "",
        val timelineType: TimelineType = TimelineType.All,
        val entries: List<TimelineEntryModel> = emptyList(),
    )

    sealed interface Effect
}
