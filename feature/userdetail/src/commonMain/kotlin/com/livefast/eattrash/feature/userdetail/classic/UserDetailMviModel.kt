package com.livefast.eattrash.feature.userdetail.classic

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserRateLimitModel
import kotlin.time.Duration

@Stable
interface UserDetailMviModel :
    ScreenModel,
    MviModel<UserDetailMviModel.Intent, UserDetailMviModel.State, UserDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(
            val section: UserSection,
        ) : Intent

        data object Follow : Intent

        data object Unfollow : Intent

        data class ToggleReblog(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleFavorite(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleDislike(
            val entry: TimelineEntryModel,
        ) : Intent

        data class ToggleBookmark(
            val entry: TimelineEntryModel,
        ) : Intent

        data object EnableNotifications : Intent

        data object DisableNotifications : Intent

        data class SubmitPollVote(
            val entry: TimelineEntryModel,
            val choices: List<Int>,
        ) : Intent

        data class ToggleMute(
            val muted: Boolean,
            val duration: Duration = Duration.INFINITE,
            val disableNotifications: Boolean = true,
        ) : Intent

        data class ToggleBlock(
            val blocked: Boolean,
        ) : Intent

        data object TogglePersonalNoteEditMode : Intent

        data class SetPersonalNote(
            val note: String,
        ) : Intent

        data object SubmitPersonalNote : Intent

        data class CopyToClipboard(
            val entry: TimelineEntryModel,
        ) : Intent

        data class SetRateLimit(
            val value: Double,
        ) : Intent

        data class ToggleTranslation(
            val entry: TimelineEntryModel,
        ) : Intent

        data class WillOpenDetail(
            val entry: TimelineEntryModel,
        ) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val user: UserModel? = null,
        val section: UserSection = UserSection.Posts,
        val entries: List<TimelineEntryModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val personalNote: String? = null,
        val personalNoteEditEnabled: Boolean = false,
        val maxBodyLines: Int = Int.MAX_VALUE,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val layout: TimelineLayout = TimelineLayout.Full,
        val rateLimit: UserRateLimitModel? = null,
        val lang: String? = null,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object PollVoteFailure : Effect

        data object Failure : Effect

        data class TriggerCopy(
            val text: String,
        ) : Effect

        data class OpenDetail(
            val entry: TimelineEntryModel,
        ) : Effect
    }
}
