package com.livefast.eattrash.raccoonforfriendica.feaure.search

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.SearchSection

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

        data class AcceptFollowRequest(
            val userId: String,
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

        data class ToggleTagFollow(
            val name: String,
            val newValue: Boolean,
        ) : Intent

        data class DeleteEntry(
            val entryId: String,
        ) : Intent

        data class MuteUser(
            val userId: String,
            val entryId: String,
        ) : Intent

        data class BlockUser(
            val userId: String,
            val entryId: String,
        ) : Intent

        data class TogglePin(
            val entry: TimelineEntryModel,
        ) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val query: String = "",
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val section: SearchSection = SearchSection.Hashtags,
        val items: List<ExploreItemModel> = emptyList(),
        val blurNsfw: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
