package com.livefast.eattrash.raccoonforfriendica.feature.inbox

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType

interface InboxMviModel :
    ScreenModel,
    MviModel<InboxMviModel.Intent, InboxMviModel.State, InboxMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class AcceptFollowRequest(
            val userId: String,
        ) : Intent

        data class Follow(
            val userId: String,
        ) : Intent

        data class Unfollow(
            val userId: String,
        ) : Intent

        data class ChangeSelectedNotificationTypes(
            val types: List<NotificationType>,
        ) : Intent
    }

    data class State(
        val isLogged: Boolean = false,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val notifications: List<NotificationModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val selectedNotificationTypes: List<NotificationType> = NotificationType.ALL,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
