package com.livefast.eattrash.raccoonforfriendica.feature.favorites

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

@Stable
interface FavoritesMviModel :
    ScreenModel,
    MviModel<FavoritesMviModel.Intent, FavoritesMviModel.State, FavoritesMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ToggleReblog(
            val entryId: String,
        ) : Intent

        data class ToggleFavorite(
            val entryId: String,
        ) : Intent

        data class ToggleBookmark(
            val entryId: String,
        ) : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val entries: List<TimelineEntryModel> = emptyList(),
    )

    sealed interface Effect
}
