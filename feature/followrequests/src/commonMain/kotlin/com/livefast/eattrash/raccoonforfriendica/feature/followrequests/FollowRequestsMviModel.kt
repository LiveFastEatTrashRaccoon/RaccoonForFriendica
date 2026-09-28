package com.livefast.eattrash.raccoonforfriendica.feature.followrequests

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface FollowRequestsMviModel :
    MviModel<FollowRequestsMviModel.Intent, FollowRequestsMviModel.State, FollowRequestsMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class Accept(val id: String) : Intent

        data class Reject(val id: String) : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val items: List<UserModel> = emptyList(),
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object Failure : Effect
    }
}
