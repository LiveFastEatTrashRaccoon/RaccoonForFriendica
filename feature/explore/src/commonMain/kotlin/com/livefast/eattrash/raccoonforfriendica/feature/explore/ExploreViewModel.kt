package com.livefast.eattrash.raccoonforfriendica.feature.explore

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.ExplorePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.ExplorePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val paginationManager: ExplorePaginationManager,
    private val userRepository: UserRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
) : DefaultMviModel<ExploreMviModel.Intent, ExploreMviModel.State, ExploreMviModel.Effect>(
        initialState = ExploreMviModel.State(),
    ),
    ExploreMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ExploreMviModel.Intent) {
        when (intent) {
            is ExploreMviModel.Intent.ChangeSection ->
                screenModelScope.launch {
                    if (uiState.value.loading) {
                        return@launch
                    }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(ExploreMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            ExploreMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            ExploreMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is ExploreMviModel.Intent.AcceptFollowRequest -> acceptFollowRequest(intent.userId)
            is ExploreMviModel.Intent.Follow -> follow(intent.userId)
            is ExploreMviModel.Intent.Unfollow -> unfollow(intent.userId)
            is ExploreMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entryId)
            is ExploreMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entryId)
            is ExploreMviModel.Intent.ToggleReblog -> toggleReblog(intent.entryId)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            when (uiState.value.section) {
                ExploreSection.Hashtags -> ExplorePaginationSpecification.Hashtags
                ExploreSection.Links -> ExplorePaginationSpecification.Links
                ExploreSection.Posts -> ExplorePaginationSpecification.Posts
                ExploreSection.Suggestions -> ExplorePaginationSpecification.Suggestions
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
                        if (item is ExploreItemModel.Suggestion && item.user.id == userId) {
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
        screenModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.items
                    .firstOrNull { it is ExploreItemModel.Suggestion && it.user.id == userId }
                    ?.let { (it as? ExploreItemModel.Suggestion)?.user }
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
        screenModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.items
                    .firstOrNull { it is ExploreItemModel.Suggestion && it.user.id == userId }
                    ?.let { (it as? ExploreItemModel.Suggestion)?.user }
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
        screenModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.items
                    .firstOrNull { it is ExploreItemModel.Suggestion && it.user.id == userId }
                    ?.let { (it as? ExploreItemModel.Suggestion)?.user }
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
                        if (item is ExploreItemModel.Entry && item.entry.id == entryId) {
                            item.copy(
                                entry = item.entry.let(block),
                            )
                        } else {
                            item
                        }
                    },
            )
        }
    }

    private fun toggleReblog(entryId: String) {
        val entry =
            uiState.value.items
                .firstOrNull { it is ExploreItemModel.Entry && it.entry.id == entryId }
                ?.let { (it as? ExploreItemModel.Entry)?.entry } ?: return
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
        val entry =
            uiState.value.items
                .firstOrNull { it is ExploreItemModel.Entry && it.entry.id == entryId }
                ?.let { (it as? ExploreItemModel.Entry)?.entry } ?: return
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
        val entry =
            uiState.value.items
                .firstOrNull { it is ExploreItemModel.Entry && it.entry.id == entryId }
                ?.let { (it as? ExploreItemModel.Entry)?.entry } ?: return
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
