package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType

interface ShortcutTimelineMvi : Mvi<ShortcutTimelineMvi.Intent, ShortcutTimelineMvi.State, ShortcutTimelineMvi.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ToggleReblog(val post: PostModel) : Intent

        data class ToggleFavorite(val post: PostModel) : Intent

        data class ToggleDislike(val post: PostModel) : Intent

        data class ToggleBookmark(val post: PostModel) : Intent

        data class SubmitPollVote(val post: PostModel, val choices: List<Int>) : Intent

        data class CopyToClipboard(val post: PostModel) : Intent

        data class ToggleTranslation(val post: PostModel) : Intent

        data class WillOpenDetail(val post: PostModel) : Intent

        data class OpenInBrowser(val post: PostModel) : Intent
    }

    data class State(
        val nodeName: String? = null,
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val availableTimelineTypes: List<TimelineType> = emptyList(),
        val posts: List<PostModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val maxBodyLines: Int = Int.MAX_VALUE,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val layout: TimelineLayout = TimelineLayout.Full,
        val lang: String? = null,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object Failure : Effect

        data object PollVoteFailure : Effect

        data class TriggerCopy(val text: String) : Effect

        data class OpenDetail(val post: PostModel) : Effect

        data class OpenUrl(val url: String) : Effect
    }
}
