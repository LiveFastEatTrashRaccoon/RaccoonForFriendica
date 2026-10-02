package com.livefast.eattrash.raccoonforfriendica.feature.circles.adduser

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface CircleAddUserMvi : Mvi<CircleAddUserMvi.Intent, CircleAddUserMvi.State, CircleAddUserMvi.Effect> {
    sealed interface Intent {
        data class SetQuery(val text: String) : Intent
        data object LoadNextPage : Intent
    }

    data class State(
        val query: String = "",
        val users: List<UserModel> = emptyList(),
        val loading: Boolean = false,
        val canFetchMore: Boolean = true,
    )

    sealed interface Effect
}
