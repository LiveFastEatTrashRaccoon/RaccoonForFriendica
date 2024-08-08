package com.livefast.eattrash.feature.accountdetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AccountSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AccountRepository
import kotlinx.coroutines.launch

class AccountDetailViewModel(
    private val id: String,
    private val accountRepository: AccountRepository,
    private val paginationManager: TimelinePaginationManager,
) : DefaultMviModel<AccountDetailMviModel.Intent, AccountDetailMviModel.State, AccountDetailMviModel.Effect>(
        initialState = AccountDetailMviModel.State(),
    ),
    AccountDetailMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                loadUser()
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: AccountDetailMviModel.Intent) {
        when (intent) {
            is AccountDetailMviModel.Intent.ChangeSection ->
                screenModelScope.launch {
                    updateState { it.copy(section = intent.section) }
                    refresh(initial = true)
                }

            AccountDetailMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            AccountDetailMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }
        }
    }

    private suspend fun loadUser() {
        val account = accountRepository.getById(id)
        updateState {
            it.copy(account = account)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            TimelinePaginationSpecification.Account(
                accountId = id,
                excludeReblogs = uiState.value.section == AccountSection.Posts,
                excludeReplies = uiState.value.section == AccountSection.Posts,
                onlyMedia = uiState.value.section == AccountSection.Media,
                pinned = uiState.value.section == AccountSection.Pinned,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val entries = paginationManager.loadNextPage()
        updateState {
            it.copy(
                entries = entries,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }
}
