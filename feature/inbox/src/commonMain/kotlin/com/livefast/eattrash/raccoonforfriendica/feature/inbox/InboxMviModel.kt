package com.livefast.eattrash.raccoonforfriendica.feature.inbox

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface InboxMviModel :
    ScreenModel,
    MviModel<InboxMviModel.Intent, InboxMviModel.State, InboxMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class Follow(
            val userId: String,
        ) : Intent

        data class Unfollow(
            val userId: String,
        ) : Intent

        data class ChangeSelectedNotificationTypes(
            val types: List<NotificationType>,
        ) : Intent

        data class MarkAsRead(
            val notification: NotificationModel,
        ) : Intent

        data object MarkAllAsRead : Intent

        data class ToggleSpoilerActive(
            val entry: TimelineEntryModel,
        ) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val notifications: List<NotificationModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val selectedNotificationTypes: List<NotificationType> = NotificationType.ALL,
        val markAllAsReadLoading: Boolean = false,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
