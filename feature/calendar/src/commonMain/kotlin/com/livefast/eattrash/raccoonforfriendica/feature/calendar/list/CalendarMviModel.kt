package com.livefast.eattrash.raccoonforfriendica.feature.calendar.list

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel

sealed interface CalendarItem {
    data class Header(val month: Int, val year: Int) : CalendarItem

    data class EventItem(val event: EventModel) : CalendarItem
}

interface CalendarMviModel : MviModel<CalendarMviModel.Intent, CalendarMviModel.State, CalendarMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val canFetchMore: Boolean = true,
        val items: List<CalendarItem> = emptyList(),
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
