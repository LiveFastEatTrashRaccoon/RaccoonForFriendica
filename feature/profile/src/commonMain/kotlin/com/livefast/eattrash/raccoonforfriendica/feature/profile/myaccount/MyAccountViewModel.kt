package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import kotlinx.coroutines.launch

class MyAccountViewModel(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val paginationManager: TimelinePaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
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

            is MyAccountMviModel.Intent.ToggleReblog -> toggleReblog(intent.entryId)
            is MyAccountMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entryId)
            is MyAccountMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entryId)
        }
    }

    private suspend fun loadUser() {
        val handle = accountRepository.getActive()?.handle.orEmpty()
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

    private suspend fun updateEntryInState(
        entryId: String,
        block: (TimelineEntryModel) -> TimelineEntryModel,
    ) {
        updateState {
            it.copy(
                entries =
                    it.entries.map { entry ->
                        if (entry.id == entryId) {
                            entry.let(block)
                        } else {
                            entry
                        }
                    },
            )
        }
    }

    private fun toggleReblog(entryId: String) {
        val entry = uiState.value.entries.firstOrNull { it.id == entryId } ?: return
        screenModelScope.launch {
            val newEntry =
                if (entry.reblogged) {
                    timelineEntryRepository.unreblog(entryId)
                } else {
                    timelineEntryRepository.reblog(entryId)
                }
            if (newEntry != null) {
                updateEntryInState(entryId) {
                    it.copy(
                        reblogged = newEntry.reblogged,
                        reblogCount = newEntry.reblogCount,
                    )
                }
            }
        }
    }

    private fun toggleFavorite(entryId: String) {
        val entry = uiState.value.entries.firstOrNull { it.id == entryId } ?: return
        screenModelScope.launch {
            val newEntry =
                if (entry.favorite) {
                    timelineEntryRepository.unfavorite(entryId)
                } else {
                    timelineEntryRepository.favorite(entryId)
                }
            if (newEntry != null) {
                updateEntryInState(entryId) {
                    it.copy(
                        favorite = newEntry.favorite,
                        favoriteCount = newEntry.favoriteCount,
                    )
                }
            }
        }
    }

    private fun toggleBookmark(entryId: String) {
        val entry = uiState.value.entries.firstOrNull { it.id == entryId } ?: return
        screenModelScope.launch {
            val newEntry =
                if (entry.bookmarked) {
                    timelineEntryRepository.unbookmark(entryId)
                } else {
                    timelineEntryRepository.bookmark(entryId)
                }
            if (newEntry != null) {
                updateEntryInState(entryId) {
                    it.copy(
                        bookmarked = newEntry.bookmarked,
                    )
                }
            }
        }
    }
}
