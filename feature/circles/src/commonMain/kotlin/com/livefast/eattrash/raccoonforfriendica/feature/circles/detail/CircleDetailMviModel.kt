package com.livefast.eattrash.raccoonforfriendica.feature.circles.detail

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel

interface CircleDetailMviModel :
    ScreenModel,
    MviModel<CircleDetailMviModel.Intent, CircleDetailMviModel.State, CircleDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val circle: CircleModel? = null,
    )

    sealed interface Effect
}
