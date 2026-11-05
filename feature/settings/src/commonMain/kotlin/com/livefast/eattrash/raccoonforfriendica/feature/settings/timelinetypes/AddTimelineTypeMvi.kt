package com.livefast.eattrash.raccoonforfriendica.feature.settings.timelinetypes

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel

interface AddTimelineTypeMvi : Mvi<AddTimelineTypeMvi.Intent, AddTimelineTypeMvi.State, AddTimelineTypeMvi.Effect> {

    sealed interface Intent {
        data class SetQuery(val query: String) : Intent
    }

    data class State(
        val query: String = "",
        val loading: Boolean = false,
        val circles: List<CircleModel> = emptyList(),
    )

    sealed interface Effect
}
