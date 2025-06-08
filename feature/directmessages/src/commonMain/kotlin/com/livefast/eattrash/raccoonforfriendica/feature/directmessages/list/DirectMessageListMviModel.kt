package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ConversationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface DirectMessageListMviModel :
    ScreenModel,
    MviModel<DirectMessageListMviModel.Intent, DirectMessageListMviModel.State, DirectMessageListMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data object UserSearchLoadNextPage : Intent

        data class UserSearchSetQuery(val query: String) : Intent

        data object UserSearchClear : Intent

        data class MarkConversationAsRead(val index: Int) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val items: List<ConversationModel> = emptyList(),
        val userSearchUsers: List<UserModel> = emptyList(),
        val userSearchLoading: Boolean = false,
        val userSearchCanFetchMore: Boolean = true,
        val userSearchQuery: String = "",
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
