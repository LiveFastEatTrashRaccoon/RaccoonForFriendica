package com.livefast.eattrash.raccoonforfriendica.feature.explore

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection
import kotlin.time.Duration

interface ExploreMvi : Mvi<ExploreMvi.Intent, ExploreMvi.State, ExploreMvi.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(val section: ExploreSection) : Intent

        data class Follow(val userId: String) : Intent

        data class Unfollow(val userId: String) : Intent

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

        data class AddInstanceShortcut(val node: String) : Intent

        data class OpenInBrowser(val post: PostModel) : Intent

        data object ResetOtherInstance : Intent

        data class SetSelectForeignInstanceName(val name: String) : Intent

        data object SubmitSelectForeignInstanceName : Intent
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
        val maxBodyLines: Int = Int.MAX_VALUE,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val layout: TimelineLayout = TimelineLayout.Full,
        val lang: String? = null,
        val currentNode: String? = null,
        val selectForeignInstanceName: String = "",
        val selectForeignInstanceValidationInProgress: Boolean = false,
        val selectForeignInstanceNameError: ValidationError? = null,
        val otherInstance: String? = null,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object PollVoteFailure : Effect

        data class TriggerCopy(val text: String) : Effect

        data class OpenUrl(val url: String) : Effect
        data object SelectForeignInstanceSuccess : Effect
    }
}
