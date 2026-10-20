package com.livefast.eattrash.raccoonforfriendica.feature.userdetail.forum

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import kotlin.time.Duration

interface ForumListMvi : Mvi<ForumListMvi.Intent, ForumListMvi.State, ForumListMvi.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data object Follow : Intent

        data object Unfollow : Intent

        data object EnableNotifications : Intent

        data object DisableNotifications : Intent

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

        data class SubmitPollVote(val post: PostModel, val choices: List<Int>) : Intent

        data class CopyToClipboard(val post: PostModel) : Intent

        data class ToggleTranslation(val post: PostModel) : Intent

        data class WillOpenDetail(val post: PostModel) : Intent

        data class AddInstanceShortcut(val node: String) : Intent

        data class OpenInBrowser(val post: PostModel) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val user: UserModel? = null,
        val posts: List<PostModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val maxBodyLines: Int = Int.MAX_VALUE,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val layout: TimelineLayout = TimelineLayout.Full,
        val lang: String? = null,
        val currentNode: String? = null,
    )

    sealed interface Effect {
        data object PollVoteFailure : Effect

        data class TriggerCopy(val text: String) : Effect

        data class OpenDetail(val post: PostModel) : Effect

        data class OpenUrl(val url: String) : Effect
    }
}
