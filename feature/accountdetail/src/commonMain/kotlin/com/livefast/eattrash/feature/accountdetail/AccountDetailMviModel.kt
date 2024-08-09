package com.livefast.eattrash.feature.accountdetail

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AccountSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface AccountDetailMviModel :
    ScreenModel,
    MviModel<AccountDetailMviModel.Intent, AccountDetailMviModel.State, AccountDetailMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class ChangeSection(
            val section: AccountSection,
        ) : Intent
    }

    data class State(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val account: AccountModel? = null,
        val section: AccountSection = AccountSection.Posts,
        val entries: List<TimelineEntryModel> = emptyList(),
    )

    sealed interface Effect
}
