package com.livefast.eattrash.raccoonforfriendica.feature.userlist

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface UserListMvi : Mvi<UserListMvi.Intent, UserListMvi.State, UserListMvi.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class Follow(val userId: String) : Intent

        data class Unfollow(val userId: String) : Intent

        data object Export : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val autoloadImages: Boolean = true,
        val user: UserModel? = null,
        val users: List<UserModel> = emptyList(),
        val hideNavigationBarWhileScrolling: Boolean = true,
        val operationInProgress: Boolean = false,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data class SaveList(val content: String) : Effect
    }
}
