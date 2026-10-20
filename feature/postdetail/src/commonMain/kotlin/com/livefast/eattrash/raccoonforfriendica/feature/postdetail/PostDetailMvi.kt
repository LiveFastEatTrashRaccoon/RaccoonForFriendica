package com.livefast.eattrash.raccoonforfriendica.feature.postdetail

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import kotlin.time.Duration

interface PostDetailMvi : Mvi<PostDetailMvi.Intent, PostDetailMvi.State, PostDetailMvi.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data class ToggleReblog(val post: PostModel) : Intent

        data class ToggleFavorite(val post: PostModel) : Intent

        data class ToggleDislike(val post: PostModel) : Intent

        data class ToggleBookmark(val post: PostModel) : Intent

        data class Delete(val postId: String) : Intent

        data class MuteUser(
            val userId: String,
            val postId: String,
            val duration: Duration = Duration.INFINITE,
            val disableNotifications: Boolean = true,
        ) : Intent

        data class BlockUser(val userId: String, val postId: String) : Intent

        data class TogglePin(val post: PostModel) : Intent

        data class SubmitPollVote(val post: PostModel, val choices: List<Int>) : Intent

        data class CopyToClipboard(val post: PostModel) : Intent

        data class ToggleTranslation(val post: PostModel) : Intent

        data class ChangeNavigationIndex(val index: Int) : Intent

        data class AddInstanceShortcut(val node: String) : Intent

        data class LoadMoreReplies(val post: PostModel) : Intent

        data class OpenInBrowser(val post: PostModel) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val initial: Boolean = true,
        val loading: Boolean = false,
        val mainPost: PostModel? = null,
        val posts: List<List<PostModel>> = emptyList(),
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
