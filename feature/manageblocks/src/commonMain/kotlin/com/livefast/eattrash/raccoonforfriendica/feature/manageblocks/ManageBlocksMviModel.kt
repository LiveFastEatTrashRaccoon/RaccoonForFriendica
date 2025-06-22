package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.ManageBlocksItem
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.ManageBlocksSection

interface ManageBlocksMviModel :
    MviModel<ManageBlocksMviModel.Intent, ManageBlocksMviModel.State, ManageBlocksMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(val section: ManageBlocksSection) : Intent

        data class ToggleMute(val userId: String) : Intent

        data class ToggleBlock(val userId: String) : Intent

        data class SetRateLimit(val handle: String, val rate: Double) : Intent

        data class AddStopWord(val word: String) : Intent

        data class RemoveStopWord(val word: String) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val loading: Boolean = false,
        val canFetchMore: Boolean = true,
        val refreshing: Boolean = false,
        val section: ManageBlocksSection = ManageBlocksSection.Muted,
        val items: List<ManageBlocksItem> = emptyList(),
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
