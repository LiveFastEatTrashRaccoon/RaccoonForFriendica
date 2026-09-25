package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.selectuser

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface SelectUserMviModel :
    MviModel<SelectUserMviModel.Intent, SelectUserMviModel.State, SelectUserMviModel.Effect> {

    sealed interface Intent {
        data object LoadNextPage : Intent

        data class SetQuery(val query: String) : Intent

        data object Clear : Intent
    }

    data class State(
        val users: List<UserModel> = emptyList(),
        val loading: Boolean = false,
        val canFetchMore: Boolean = true,
        val query: String = "",
    )

    sealed interface Effect
}
