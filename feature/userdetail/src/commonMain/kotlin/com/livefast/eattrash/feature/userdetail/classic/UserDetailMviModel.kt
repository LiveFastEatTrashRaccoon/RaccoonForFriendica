package com.livefast.eattrash.feature.userdetail.classic

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Stable
interface UserDetailMviModel :
    ScreenModel,
    MviModel<UserDetailMviModel.Intent, UserDetailMviModel.State, UserDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(
            val section: UserSection,
        ) : Intent

        data object AcceptFollowRequest : Intent

        data object Follow : Intent

        data object Unfollow : Intent

        data class ToggleReblog(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleFavorite(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleBookmark(
            val entry: TimelineEntryModel,
        ) : Intent

        data object EnableNotifications : Intent

        data object DisableNotifications : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val user: UserModel? = null,
        val section: UserSection = UserSection.Posts,
        val entries: List<TimelineEntryModel> = emptyList(),
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
