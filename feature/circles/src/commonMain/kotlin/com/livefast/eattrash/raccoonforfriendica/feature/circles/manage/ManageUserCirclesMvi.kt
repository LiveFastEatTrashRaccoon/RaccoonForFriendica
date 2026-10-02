package com.livefast.eattrash.raccoonforfriendica.feature.circles.manage

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

data class CircleBelongingUiModel(val circle: CircleModel, val belonging: Boolean, val pending: Boolean = false)

interface ManageUserCirclesMvi :
    Mvi<ManageUserCirclesMvi.Intent, ManageUserCirclesMvi.State, ManageUserCirclesMvi.Effect> {
    sealed interface Intent {
        data object Refresh : Intent
        data class Add(val circleId: String) : Intent
        data class Remove(val circleId: String) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val items: List<CircleBelongingUiModel> = emptyList(),
        val refreshing: Boolean = true,
        val user: UserModel? = null,
    )

    sealed interface Effect {
        data object Error : Effect
    }
}
