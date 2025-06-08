package com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel

interface EventDetailMviModel :
    ScreenModel,
    MviModel<EventDetailMviModel.Intent, EventDetailMviModel.State, EventDetailMviModel.Effect> {
    sealed interface Intent

    data class State(val event: EventModel? = null, val hideNavigationBarWhileScrolling: Boolean = true)

    sealed interface Effect
}
