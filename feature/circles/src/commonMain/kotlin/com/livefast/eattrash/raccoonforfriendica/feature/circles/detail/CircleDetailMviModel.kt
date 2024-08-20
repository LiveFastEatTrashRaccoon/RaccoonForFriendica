package com.livefast.eattrash.raccoonforfriendica.feature.circles.detail

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel

interface CircleDetailMviModel :
    ScreenModel,
    MviModel<CircleDetailMviModel.Intent, CircleDetailMviModel.State, CircleDetailMviModel.Effect> {
    sealed interface Intent

    data class State(
        val initial: Boolean = true,
    )

    sealed interface Effect
}
