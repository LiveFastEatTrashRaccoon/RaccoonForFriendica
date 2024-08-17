package com.livefast.eattrash.raccoonforfriendica.feature.thread

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

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
    }

    data class State(
        val isLogged: Boolean = false,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val entry: TimelineEntryModel? = null,
        val replies: List<TimelineEntryModel> = emptyList(),
        val blurNsfw: Boolean = true,
    )

    sealed interface Effect
}
