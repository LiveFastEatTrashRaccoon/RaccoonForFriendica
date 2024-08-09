package com.livefast.eattrash.raccoonforfriendica.feature.userlist

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface UserListMviModel :
    ScreenModel,
    MviModel<UserListMviModel.Intent, UserListMviModel.State, UserListMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val user: UserModel? = null,
        val users: List<UserModel> = emptyList(),
    )

    sealed interface Effect
}
