package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

sealed interface CircleListItem {
    data class Header(val type: CircleType) : CircleListItem

    data class Circle(val circle: CircleModel) : CircleListItem
}

interface CirclesMviModel : MviModel<CirclesMviModel.Intent, CirclesMviModel.State, CirclesMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data class Delete(val circleId: String) : Intent

        data class OpenDetail(val circle: CircleModel) : Intent

        data class Upsert(val circle: CircleModel) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val items: List<CircleListItem> = emptyList(),
        val hideNavigationBarWhileScrolling: Boolean = true,
        val operationInProgress: Boolean = false,
    )

    sealed interface Effect {
        data object Failure : Effect

        data class OpenUser(val user: UserModel) : Effect

        data class OpenCircle(val circle: CircleModel) : Effect
    }
}
