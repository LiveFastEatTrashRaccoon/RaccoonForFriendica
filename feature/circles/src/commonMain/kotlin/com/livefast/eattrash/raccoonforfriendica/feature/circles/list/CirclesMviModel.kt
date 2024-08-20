package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel

interface CirclesMviModel :
    ScreenModel,
    MviModel<CirclesMviModel.Intent, CirclesMviModel.State, CirclesMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val items: List<CircleModel> = emptyList(),
    )

    sealed interface Effect
}
