package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlin.time.Duration

@Stable
interface EntryDetailMviModel :
    ScreenModel,
    MviModel<EntryDetailMviModel.Intent, EntryDetailMviModel.State, EntryDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

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

        data class ChangeNavigationIndex(val index: Int) : Intent

        data class AddInstanceShortcut(val node: String) : Intent

        data class LoadMoreReplies(val entry: TimelineEntryModel) : Intent

        data class OpenInBrowser(val entry: TimelineEntryModel) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val initial: Boolean = true,
        val loading: Boolean = false,
        val mainEntry: TimelineEntryModel? = null,
        val entries: List<List<TimelineEntryModel>> = emptyList(),
        val currentIndex: Int = -1,
        val blurNsfw: Boolean = true,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val layout: TimelineLayout = TimelineLayout.Full,
        val lang: String? = null,
        val currentNode: String? = null,
    )

    sealed interface Effect {
        data class ScrollToItem(val index: Int) : Effect

        data object PollVoteFailure : Effect

        data class TriggerCopy(val text: String) : Effect

        data class OpenUrl(val url: String) : Effect
    }
}
