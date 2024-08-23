package com.livefast.eattrash.raccoonforfriendica.feaure.search

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.SearchPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.SearchPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.SearchSection
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val paginationManager: SearchPaginationManager,
    private val userRepository: UserRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val tagRepository: TagRepository,
    private val settingsRepository: SettingsRepository,
    private val identityRepository: IdentityRepository,
    private val hapticFeedback: HapticFeedback,
) : DefaultMviModel<SearchMviModel.Intent, SearchMviModel.State, SearchMviModel.Effect>(
        initialState = SearchMviModel.State(),
    ),
    SearchMviModel {
    init {
        screenModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState { it.copy(blurNsfw = settings?.blurNsfw ?: true) }
                }.launchIn(this)
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
                }.launchIn(this)
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .drop(1)
                .debounce(1000)
                .onEach {
                    refresh()
                }.launchIn(this)
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: SearchMviModel.Intent) {
        when (intent) {
            is SearchMviModel.Intent.SetSearch ->
                screenModelScope.launch {
                    updateState { it.copy(query = intent.query) }
                }

            is SearchMviModel.Intent.ChangeSection ->
                screenModelScope.launch {
                    if (uiState.value.loading) {
                        return@launch
                    }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(SearchMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            SearchMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            SearchMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is SearchMviModel.Intent.AcceptFollowRequest -> acceptFollowRequest(intent.userId)
            is SearchMviModel.Intent.Follow -> follow(intent.userId)
            is SearchMviModel.Intent.Unfollow -> unfollow(intent.userId)
            is SearchMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is SearchMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is SearchMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is SearchMviModel.Intent.ToggleTagFollow ->
                toggleTagFollow(
                    intent.name,
                    intent.newValue,
                )

            is SearchMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is SearchMviModel.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )
            is SearchMviModel.Intent.BlockUser ->
                block(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )
            is SearchMviModel.Intent.TogglePin -> togglePin(intent.entry)
            is SearchMviModel.Intent.SubmitPollVote -> submitPoll(intent.entry, intent.choices)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val query = uiState.value.query
        paginationManager.reset(
            when (uiState.value.section) {
                SearchSection.Hashtags -> SearchPaginationSpecification.Hashtags(query)
                SearchSection.Posts ->
                    SearchPaginationSpecification.Entries(
                        query = query,
                        includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
                    )
                SearchSection.Users -> SearchPaginationSpecification.Users(query)
            },
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
                items = entries,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }

    private suspend fun updateUserInState(
        userId: String,
        block: (UserModel) -> UserModel,
    ) {
        updateState {
            it.copy(
                items =
                    it.items.map { item ->
                        if (item is ExploreItemModel.User && item.user.id == userId) {
                            item.copy(
                                user = item.user.let(block),
                            )
                        } else {
                            item
                        }
                    },
            )
        }
    }

    private fun acceptFollowRequest(userId: String) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.items
                    .firstOrNull { it is ExploreItemModel.User && it.user.id == userId }
                    ?.let { (it as? ExploreItemModel.User)?.user }
            userRepository.acceptFollowRequest(userId)
            val newRelationship = userRepository.getRelationships(listOf(userId)).firstOrNull()
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it.copy(
                    relationshipStatus = newStatus,
                    notificationStatus = newNotificationStatus,
                    relationshipStatusPending = false,
                )
            }
        }
    }

    private fun follow(userId: String) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.items
                    .firstOrNull { it is ExploreItemModel.User && it.user.id == userId }
                    ?.let { (it as? ExploreItemModel.User)?.user }
            val newRelationship = userRepository.follow(userId)
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it.copy(
                    relationshipStatus = newStatus,
                    notificationStatus = newNotificationStatus,
                    relationshipStatusPending = false,
                )
            }
        }
    }

    private fun unfollow(userId: String) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.items
                    .firstOrNull { it is ExploreItemModel.User && it.user.id == userId }
                    ?.let { (it as? ExploreItemModel.User)?.user }
            val newRelationship = userRepository.unfollow(userId)
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it.copy(
                    relationshipStatus = newStatus,
                    notificationStatus = newNotificationStatus,
                    relationshipStatusPending = false,
                )
            }
        }
    }

    private suspend fun updateEntryInState(
        entryId: String,
        block: (TimelineEntryModel) -> TimelineEntryModel,
    ) {
        updateState {
            it.copy(
                items =
                    it.items.map { item ->
                        when {
                            item is ExploreItemModel.Entry && item.entry.id == entryId -> {
                                item.copy(
                                    entry = item.entry.let(block),
                                )
                            }

                            item is ExploreItemModel.Entry && item.entry.reblog?.id == entryId -> {
                                item.copy(
                                    entry =
                                        item.entry.copy(
                                            reblog = item.entry.reblog?.let(block),
                                        ),
                                )
                            }

                            else -> {
                                item
                            }
                        }
                    },
            )
        }
    }

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                items =
                    it.items.filter { item ->
                        when {
                            item is ExploreItemModel.Entry && item.entry.id == entryId -> false
                            item is ExploreItemModel.Entry && item.entry.reblog?.id == entryId -> false
                            else -> true
                        }
                    },
            )
        }
    }

    private fun toggleReblog(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    reblogLoading = true,
                )
            }
            val newEntry =
                if (entry.reblogged) {
                    timelineEntryRepository.unreblog(entry.id)
                } else {
                    timelineEntryRepository.reblog(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        reblogged = newEntry.reblogged,
                        reblogCount = newEntry.reblogCount,
                        reblogLoading = false,
                    )
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        reblogLoading = false,
                    )
                }
            }
        }
    }

    private fun toggleFavorite(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    favoriteLoading = true,
                )
            }
            val newEntry =
                if (entry.favorite) {
                    timelineEntryRepository.unfavorite(entry.id)
                } else {
                    timelineEntryRepository.favorite(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        favorite = newEntry.favorite,
                        favoriteCount = newEntry.favoriteCount,
                        favoriteLoading = false,
                    )
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        favoriteLoading = false,
                    )
                }
            }
        }
    }

    private fun toggleBookmark(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    bookmarkLoading = true,
                )
            }
            val newEntry =
                if (entry.bookmarked) {
                    timelineEntryRepository.unbookmark(entry.id)
                } else {
                    timelineEntryRepository.bookmark(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        bookmarked = newEntry.bookmarked,
                        bookmarkLoading = false,
                    )
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        bookmarkLoading = false,
                    )
                }
            }
        }
    }

    private suspend fun updateHashtagInState(
        name: String,
        block: (TagModel) -> TagModel,
    ) {
        updateState {
            it.copy(
                items =
                    it.items.map { item ->
                        if (item is ExploreItemModel.HashTag && item.hashtag.name == name) {
                            item.copy(
                                hashtag = item.hashtag.let(block),
                            )
                        } else {
                            item
                        }
                    },
            )
        }
    }

    private fun toggleTagFollow(
        name: String,
        follow: Boolean,
    ) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateHashtagInState(name) { it.copy(followingPending = true) }
            val newTag =
                if (!follow) {
                    tagRepository.unfollow(name)
                } else {
                    tagRepository.follow(name)
                }
            if (newTag != null) {
                updateHashtagInState(name) { newTag }
            }
        }
    }

    private fun deleteEntry(entryId: String) {
        screenModelScope.launch {
            val success = timelineEntryRepository.delete(entryId)
            if (success) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun mute(
        userId: String,
        entryId: String,
    ) {
        screenModelScope.launch {
            val res = userRepository.mute(userId)
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun block(
        userId: String,
        entryId: String,
    ) {
        screenModelScope.launch {
            val res = userRepository.block(userId)
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun togglePin(entry: TimelineEntryModel) {
        screenModelScope.launch {
            val newEntry =
                if (entry.pinned) {
                    timelineEntryRepository.unpin(entry.id)
                } else {
                    timelineEntryRepository.pin(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        pinned = newEntry.pinned,
                    )
                }
            }
        }
    }

    private fun submitPoll(
        entry: TimelineEntryModel,
        choices: List<Int>,
    ) {
        val poll = entry.poll ?: return
        screenModelScope.launch {
            updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = true)) }
            val newPoll =
                timelineEntryRepository.submitPoll(
                    pollId = poll.id,
                    choices = choices,
                )
            if (newPoll != null) {
                updateEntryInState(entry.id) { it.copy(poll = newPoll) }
            } else {
                updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = false)) }
                emitEffect(SearchMviModel.Effect.PollVoteFailure)
            }
        }
    }
}
