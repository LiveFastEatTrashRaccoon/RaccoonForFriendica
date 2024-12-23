package com.livefast.eattrash.raccoonforfriendica.feature.thread

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlin.time.Duration

@Stable
interface ThreadMviModel :
    ScreenModel,
    MviModel<ThreadMviModel.Intent, ThreadMviModel.State, ThreadMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data class ToggleReblog(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleFavorite(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleBookmark(
            val entry: TimelineEntryModel,
        ) : Intent

        data class LoadMoreReplies(
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

        data class SubmitPollVote(
            val entry: TimelineEntryModel,
            val choices: List<Int>,
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
        val entry: TimelineEntryModel? = null,
        val replies: List<TimelineEntryModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val layout: TimelineLayout = TimelineLayout.Full,
    )

    sealed interface Effect {
        data object PollVoteFailure : Effect

        data class TriggerCopy(
            val text: String,
        ) : Effect
    }
}
