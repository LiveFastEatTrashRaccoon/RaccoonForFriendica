package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType

interface ShortcutTimelineMviModel :
    MviModel<ShortcutTimelineMviModel.Intent, ShortcutTimelineMviModel.State, ShortcutTimelineMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ToggleReblog(val entry: TimelineEntryModel) : Intent

        data class ToggleFavorite(val entry: TimelineEntryModel) : Intent

        data class ToggleDislike(val entry: TimelineEntryModel) : Intent

        data class ToggleBookmark(val entry: TimelineEntryModel) : Intent

        data class SubmitPollVote(val entry: TimelineEntryModel, val choices: List<Int>) : Intent

        data class CopyToClipboard(val entry: TimelineEntryModel) : Intent

        data class ToggleTranslation(val entry: TimelineEntryModel) : Intent

        data class WillOpenDetail(val entry: TimelineEntryModel) : Intent

        data class OpenInBrowser(val entry: TimelineEntryModel) : Intent
    }

    data class State(
        val nodeName: String? = null,
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val availableTimelineTypes: List<TimelineType> = emptyList(),
        val entries: List<TimelineEntryModel> = emptyList(),
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

        data class OpenDetail(val entry: TimelineEntryModel) : Effect

        data class OpenUrl(val url: String) : Effect
    }
}
