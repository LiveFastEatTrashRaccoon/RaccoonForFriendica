package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlin.time.Duration

@Stable
interface HashtagMviModel : MviModel<HashtagMviModel.Intent, HashtagMviModel.State, HashtagMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ToggleTagFollow(val newValue: Boolean) : Intent

        data class ToggleReblog(val entry: TimelineEntryModel) : Intent

        data class ToggleFavorite(val entry: TimelineEntryModel) : Intent

        data class ToggleDislike(val entry: TimelineEntryModel) : Intent

        data class ToggleBookmark(val entry: TimelineEntryModel) : Intent

        data class DeleteEntry(val entryId: String) : Intent

        data class MuteUser(
            val userId: String,
            val entryId: String,
            val duration: Duration = Duration.INFINITE,
            val disableNotifications: Boolean = true,
        ) : Intent

        data class BlockUser(val userId: String, val entryId: String) : Intent

        data class TogglePin(val entry: TimelineEntryModel) : Intent

        data class SubmitPollVote(val entry: TimelineEntryModel, val choices: List<Int>) : Intent

        data class CopyToClipboard(val entry: TimelineEntryModel) : Intent

        data class ToggleTranslation(val entry: TimelineEntryModel) : Intent

        data class WillOpenDetail(val entry: TimelineEntryModel) : Intent

        data class AddInstanceShortcut(val node: String) : Intent

        data class OpenInBrowser(val entry: TimelineEntryModel) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val following: Boolean? = null,
        val followingPending: Boolean = false,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val entries: List<TimelineEntryModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val maxBodyLines: Int = Int.MAX_VALUE,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val layout: TimelineLayout = TimelineLayout.Full,
        val lang: String? = null,
        val currentNode: String? = null,
    )

    sealed interface Effect {
        data object PollVoteFailure : Effect

        data class TriggerCopy(val text: String) : Effect

        data class OpenDetail(val entry: TimelineEntryModel) : Effect

        data class OpenUrl(val url: String) : Effect
    }
}
