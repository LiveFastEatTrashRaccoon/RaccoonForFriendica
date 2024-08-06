package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface EntryDetailMviModel :
    ScreenModel,
    MviModel<EntryDetailMviModel.Intent, EntryDetailMviModel.State, EntryDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val initial: Boolean = true,
        val creator: AccountModel? = null,
        val entries: List<TimelineEntryModel> = emptyList(),
    )

    sealed interface Effect {
        data class ScrollToItem(
            val index: Int,
        ) : Effect
    }
}
