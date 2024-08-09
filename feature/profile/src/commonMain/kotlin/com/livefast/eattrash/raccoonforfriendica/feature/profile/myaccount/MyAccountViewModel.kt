package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import kotlinx.coroutines.launch
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository as IdentityAccountRepository

class MyAccountViewModel(
    private val userRepository: UserRepository,
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
                    if (uiState.value.loading) {
                        return@launch
                    }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(MyAccountMviModel.Effect.BackToTop)
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
        val currentAccount = userRepository.getByHandle(handle)
        updateState { it.copy(user = currentAccount) }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val accountId = uiState.value.user?.id ?: ""
        paginationManager.reset(
            TimelinePaginationSpecification.User(
                userId = accountId,
                excludeReblogs = uiState.value.section == UserSection.Posts,
                excludeReplies = uiState.value.section == UserSection.Posts,
                onlyMedia = uiState.value.section == UserSection.Media,
                pinned = uiState.value.section == UserSection.Pinned,
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
