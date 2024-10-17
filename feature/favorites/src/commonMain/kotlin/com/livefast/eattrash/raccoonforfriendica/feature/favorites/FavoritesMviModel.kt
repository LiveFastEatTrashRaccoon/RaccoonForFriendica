package com.livefast.eattrash.raccoonforfriendica.feature.favorites

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlin.time.Duration

@Stable
interface FavoritesMviModel :
    ScreenModel,
    MviModel<FavoritesMviModel.Intent, FavoritesMviModel.State, FavoritesMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ToggleReblog(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleFavorite(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleBookmark(
            val entry: TimelineEntryModel,
        ) : Intent

        data class DeleteEntry(
            val entryId: String,
        ) : Intent

        data class MuteUser(
            val userId: String,
            val entryId: String,
            val duration: Duration = Duration.INFINITE,
            val disableNotifications: Boolean = true,
        ) : Intent

        data class BlockUser(
            val userId: String,
            val entryId: String,
        ) : Intent

        data class TogglePin(
            val entry: TimelineEntryModel,
        ) : Intent

        data class SubmitPollVote(
            val entry: TimelineEntryModel,
            val choices: List<Int>,
        ) : Intent

        data class ToggleSpoilerActive(
            val entry: TimelineEntryModel,
        ) : Intent

        data class CopyToClipboard(
            val entry: TimelineEntryModel,
        ) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val entries: List<TimelineEntryModel> = emptyList(),
        val blurNsfw: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object PollVoteFailure : Effect

        data class TriggerCopy(
            val text: String,
        ) : Effect
    }
}
