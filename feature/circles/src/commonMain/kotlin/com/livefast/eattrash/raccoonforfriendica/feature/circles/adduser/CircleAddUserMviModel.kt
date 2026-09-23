package com.livefast.eattrash.raccoonforfriendica.feature.circles.adduser

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface CircleAddUserMviModel :
    MviModel<CircleAddUserMviModel.Intent, CircleAddUserMviModel.State, CircleAddUserMviModel.Effect> {
    sealed interface Intent {
        data class SetSearchUserQuery(val text: String) : Intent
        data object UserSearchLoadNextPage : Intent
    }

    data class State(
        val searchUsersQuery: String = "",
        val searchUsers: List<UserModel> = emptyList(),
        val userSearchLoading: Boolean = false,
        val userSearchCanFetchMore: Boolean = true,
    )

    sealed interface Effect
}
