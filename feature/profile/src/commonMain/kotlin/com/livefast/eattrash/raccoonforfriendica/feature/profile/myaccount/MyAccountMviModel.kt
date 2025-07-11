package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Stable
interface MyAccountMviModel :
    MviModel<MyAccountMviModel.Intent, MyAccountMviModel.State, MyAccountMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(val section: UserSection) : Intent

        data class ToggleReblog(val entry: TimelineEntryModel) : Intent

        data class ToggleFavorite(val entry: TimelineEntryModel) : Intent

        data class ToggleDislike(val entry: TimelineEntryModel) : Intent

        data class ToggleBookmark(val entry: TimelineEntryModel) : Intent

        data class DeleteEntry(val entryId: String) : Intent

        data class TogglePin(val entry: TimelineEntryModel) : Intent

        data class CopyToClipboard(val entry: TimelineEntryModel) : Intent

        data object Logout : Intent

        data class OpenInBrowser(val entry: TimelineEntryModel) : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val user: UserModel? = null,
        val section: UserSection = UserSection.Posts,
        val entries: List<TimelineEntryModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val maxBodyLines: Int = Int.MAX_VALUE,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val layout: TimelineLayout = TimelineLayout.Full,
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object Failure : Effect

        data class TriggerCopy(val text: String) : Effect

        data class OpenUrl(val url: String) : Effect
    }
}
