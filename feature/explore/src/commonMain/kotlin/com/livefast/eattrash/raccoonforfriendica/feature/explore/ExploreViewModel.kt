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
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val paginationManager: ExplorePaginationManager,
    private val userRepository: UserRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
) : DefaultMviModel<ExploreMviModel.Intent, ExploreMviModel.State, ExploreMviModel.Effect>(
        initialState = ExploreMviModel.State(),
    ),
    ExploreMviModel {
    init {
        screenModelScope.launch {
            combine(
                settingsRepository.current,
                identityRepository.currentUser,
            ) { settings, currentUser ->
                settings to currentUser
            }.onEach { (settings, currentUser) ->
                updateState {
                    it.copy(
                        availableSections =
                            buildList {
                                this += ExploreSection.Hashtags
                                this += ExploreSection.Posts
                                this += ExploreSection.Links
                                if (currentUser != null) {
                                    this += ExploreSection.Suggestions
                                }
                            },
                        blurNsfw = settings?.blurNsfw ?: true,
                        currentUserId = currentUser?.id,
                    )
                }
            }.launchIn(this)
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
            is ExploreMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is ExploreMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is ExploreMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is ExploreMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is ExploreMviModel.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )
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
                ExploreSection.Posts ->
                    ExplorePaginationSpecification.Posts(
                        includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
                    )
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
}
