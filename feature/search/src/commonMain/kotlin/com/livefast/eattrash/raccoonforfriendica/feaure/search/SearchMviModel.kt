package com.livefast.eattrash.raccoonforfriendica.feaure.search

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.SearchSection
import kotlin.time.Duration

@Stable
interface SearchMviModel :
    ScreenModel,
    MviModel<SearchMviModel.Intent, SearchMviModel.State, SearchMviModel.Effect> {
    sealed interface Intent {
        data class SetSearch(
            val query: String,
        ) : Intent

        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(
            val section: SearchSection,
        ) : Intent

        data class Follow(
            val userId: String,
        ) : Intent

        data class Unfollow(
            val userId: String,
        ) : Intent

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

        data class CopyToClipboard(
            val entry: TimelineEntryModel,
        ) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val query: String = "",
        val refreshing: Boolean = false,
        val earlyLoading: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val section: SearchSection = SearchSection.Posts,
        val items: List<ExploreItemModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val maxBodyLines: Int = Int.MAX_VALUE,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object PollVoteFailure : Effect

        data class TriggerCopy(
            val text: String,
        ) : Effect
    }
}
