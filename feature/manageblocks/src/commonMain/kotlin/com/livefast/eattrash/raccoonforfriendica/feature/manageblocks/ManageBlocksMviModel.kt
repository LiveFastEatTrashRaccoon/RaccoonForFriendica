package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.ManageBlocksSection

interface ManageBlocksMviModel :
    ScreenModel,
    MviModel<ManageBlocksMviModel.Intent, ManageBlocksMviModel.State, ManageBlocksMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(
            val section: ManageBlocksSection,
        ) : Intent

        data class ToggleMute(
            val userId: String,
        ) : Intent

        data class ToggleBlock(
            val userId: String,
        ) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val loading: Boolean = false,
        val canFetchMore: Boolean = true,
        val refreshing: Boolean = false,
        val section: ManageBlocksSection = ManageBlocksSection.Muted,
        val items: List<UserModel> = emptyList(),
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
