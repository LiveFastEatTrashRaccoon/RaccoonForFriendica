package com.livefast.eattrash.raccoonforfriendica.feature.unpublished

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface UnpublishedMviModel :
    ScreenModel,
    MviModel<UnpublishedMviModel.Intent, UnpublishedMviModel.State, UnpublishedMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(
            val section: UnpublishedType,
        ) : Intent

        data class ToggleSpoilerActive(
            val entry: TimelineEntryModel,
        ) : Intent

        data class DeleteEntry(
            val entryId: String,
        ) : Intent
    }

    data class State(
        val currentUser: UserModel? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val section: UnpublishedType = UnpublishedType.Scheduled,
        val entries: List<TimelineEntryModel> = emptyList(),
        val blurNsfw: Boolean = true,
        val maxBodyLines: Int = Int.MAX_VALUE,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
