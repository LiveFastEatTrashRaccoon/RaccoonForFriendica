package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.selectuser

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface SelectUserMviModel :
    MviModel<SelectUserMviModel.Intent, SelectUserMviModel.State, SelectUserMviModel.Effect> {

    sealed interface Intent {
        data object UserSearchLoadNextPage : Intent

        data class UserSearchSetQuery(val query: String) : Intent

        data object UserSearchClear : Intent
    }

    data class State(
        val userSearchUsers: List<UserModel> = emptyList(),
        val userSearchLoading: Boolean = false,
        val userSearchCanFetchMore: Boolean = true,
        val userSearchQuery: String = "",
    )

    sealed interface Effect
}
