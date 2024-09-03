package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ConversationModel

interface DirectMessageListMviModel :
    ScreenModel,
    MviModel<DirectMessageListMviModel.Intent, DirectMessageListMviModel.State, DirectMessageListMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val items: List<ConversationModel> = emptyList(),
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
