package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AccountSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AccountRepository
import kotlinx.coroutines.launch
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository as IdentityAccountRepository

class MyAccountViewModel(
    private val accountRepository: AccountRepository,
    private val identityAccountRepository: IdentityAccountRepository,
    private val paginationManager: TimelinePaginationManager,
) : DefaultMviModel<MyAccountMviModel.Intent, MyAccountMviModel.State, MyAccountMviModel.Effect>(
        initialState = MyAccountMviModel.State(),
    ),
    MyAccountMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                loadUser()
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: MyAccountMviModel.Intent) {
        when (intent) {
            is MyAccountMviModel.Intent.ChangeSection ->
                screenModelScope.launch {
                    updateState { it.copy(section = intent.section) }
                    refresh(initial = true)
                }

            MyAccountMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            MyAccountMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }
        }
    }

    private suspend fun loadUser() {
        val handle = identityAccountRepository.getActive()?.handle.orEmpty()
        val currentAccount = accountRepository.getByHandle(handle)
        updateState { it.copy(account = currentAccount) }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val accountId = uiState.value.account?.id ?: ""
        paginationManager.reset(
            TimelinePaginationSpecification.Account(
                accountId = accountId,
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
