package com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface CircleMembersMviModel :
    MviModel<CircleMembersMviModel.Intent, CircleMembersMviModel.State, CircleMembersMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data class Remove(val userId: String) : Intent

        data class Add(val users: List<UserModel>) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val circle: CircleModel? = null,
        val users: List<UserModel> = emptyList(),
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object Failure : Effect
    }
}
