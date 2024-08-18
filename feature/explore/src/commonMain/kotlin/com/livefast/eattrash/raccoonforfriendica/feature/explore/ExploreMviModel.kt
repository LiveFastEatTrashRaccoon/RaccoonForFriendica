package com.livefast.eattrash.raccoonforfriendica.feature.explore

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection

@Stable
interface ExploreMviModel :
    ScreenModel,
    MviModel<ExploreMviModel.Intent, ExploreMviModel.State, ExploreMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(
            val section: ExploreSection,
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

        data class DeleteEntry(
            val entryId: String,
        ) : Intent

        data class MuteUser(
            val userId: String,
            val entryId: String,
        ) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val section: ExploreSection = ExploreSection.Hashtags,
        val availableSections: List<ExploreSection> = emptyList(),
        val items: List<ExploreItemModel> = emptyList(),
        val blurNsfw: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
