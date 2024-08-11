package com.livefast.eattrash.raccoonforfriendica.feature.hashtag

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

@Stable
interface HashtagMviModel :
    ScreenModel,
    MviModel<HashtagMviModel.Intent, HashtagMviModel.State, HashtagMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val entries: List<TimelineEntryModel> = emptyList(),
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
