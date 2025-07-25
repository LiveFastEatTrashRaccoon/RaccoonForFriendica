package com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel

@Stable
interface FollowedHashtagsMviModel :
    MviModel<FollowedHashtagsMviModel.Intent, FollowedHashtagsMviModel.State, FollowedHashtagsMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ToggleTagFollow(val name: String, val newValue: Boolean) : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val items: List<TagModel> = emptyList(),
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect
}
