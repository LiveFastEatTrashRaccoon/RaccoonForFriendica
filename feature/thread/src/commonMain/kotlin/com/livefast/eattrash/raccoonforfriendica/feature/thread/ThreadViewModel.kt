package com.livefast.eattrash.raccoonforfriendica.feature.thread

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.PopulateThreadUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.InstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration

class ThreadViewModel(
    private val entryId: String,
    private val swipeNavigationEnabled: Boolean,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val hapticFeedback: HapticFeedback,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val populateThread: PopulateThreadUseCase,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val getTranslation: GetTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<ThreadMviModel.Intent, ThreadMviModel.State, ThreadMviModel.Effect>(
    initialState = ThreadMviModel.State(),
),
    ThreadMviModel {
    private val currentMainEntry: TimelineEntryModel?
        get() {
            val currentState = uiState.value
            return currentState.mainEntries.getOrNull(currentState.currentIndex)
        }

    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            blurNsfw = settings?.blurNsfw ?: true,
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                            layout = settings?.timelineLayout ?: TimelineLayout.Full,
                            lang = settings?.lang,
                        )
                    }
                }.launchIn(this)

            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)

            notificationCenter
                .subscribe(TimelineEntryUpdatedEvent::class)
                .onEach { event ->
                    updateEntryInState(event.entry.id) { event.entry }
                }.launchIn(this)

            apiConfigurationRepository.node
                .onEach { node ->
                    updateState { it.copy(currentNode = node) }
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ThreadMviModel.Intent) {
        when (intent) {
            ThreadMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is ThreadMviModel.Intent.LoadMoreReplies ->
                screenModelScope.launch {
                    loadMoreReplies(intent.entry)
                }

            is ThreadMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is ThreadMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is ThreadMviModel.Intent.ToggleDislike -> toggleDislike(intent.entry)
            is ThreadMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is ThreadMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is ThreadMviModel.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )
            is ThreadMviModel.Intent.BlockUser ->
                block(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )
            is ThreadMviModel.Intent.SubmitPollVote -> submitPoll(intent.entry, intent.choices)
            is ThreadMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
            is ThreadMviModel.Intent.ToggleTranslation -> toggleTranslation(intent.entry)
            is ThreadMviModel.Intent.ChangeNavigationIndex ->
                changeNavigationIndex(intent.index)
            is ThreadMviModel.Intent.AddInstanceShortcut -> addInstanceShortcut(intent.node)
            is ThreadMviModel.Intent.OpenInBrowser -> openInBrowser(intent.entry)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(
                initial = initial,
                refreshing = !initial,
            )
        }

        updateState {
            it.copy(
                canFetchMore = true,
            )
        }

        if (initial) {
            val currentEntry = entryCache.get(entryId)
            val mainEntries =
                if (swipeNavigationEnabled) {
                    timelineNavigationManager.currentList
                } else {
                    currentEntry?.let { listOf(it) } ?: emptyList()
                }
            val initialIndex = mainEntries.indexOfFirst { it.id == entryId }.coerceAtLeast(0)
            val replies: List<List<TimelineEntryModel>> = mainEntries.map { emptyList() }
            check(mainEntries.size == replies.size)
            updateState {
                it.copy(
                    currentIndex = initialIndex,
                    mainEntries = mainEntries,
                    replies = replies,
                )
            }

            if (initialIndex.isNearTheEnd(timelineNavigationManager.currentList)) {
                loadNavigationNextPage()
            }
        }

        loadReplies()

        updateState {
            it.copy(
                initial = false,
                refreshing = false,
                canFetchMore = false,
            )
        }
    }

    private suspend fun loadReplies() {
        val mainEntry = currentMainEntry?.original ?: return
        val result = populateThread(entry = mainEntry)
        val replies = result.filter { it.id != mainEntry.id }
        replies.preloadImages()
        updateState {
            it.copy(
                replies =
                it.replies.mapIndexed { idx, list ->
                    if (idx == it.currentIndex) {
                        replies
                    } else {
                        list
                    }
                },
            )
        }
    }

    private suspend fun loadMoreReplies(entry: TimelineEntryModel) {
        check(!entry.loadMoreButtonLoading) { return }
        val currentState = uiState.value
        val currentReplies = currentState.replies[currentState.currentIndex]
        updateEntryInState(entry.id) { it.copy(loadMoreButtonLoading = true) }
        val result = populateThread(entry = entry.original)
        val newReplies = result.filter { e1 -> currentReplies.none { e2 -> e1.id == e2.id } }
        if (newReplies.isEmpty()) {
            // abort and disable load more button
            updateEntryInState(entry.id) {
                it.copy(
                    loadMoreButtonVisible = false,
                    loadMoreButtonLoading = false,
                )
            }
        } else {
            newReplies.preloadImages()
            val index = currentReplies.indexOfFirst { e -> e.id == entry.id }
            val replies =
                buildList {
                    if (index > 0) {
                        addAll(currentReplies.subList(fromIndex = 0, toIndex = index))
                    }
                    add(
                        entry.copy(
                            loadMoreButtonVisible = false,
                            loadMoreButtonLoading = false,
                        ),
                    )
                    addAll(newReplies)
                    if (index < currentReplies.size) {
                        addAll(
                            currentReplies.subList(
                                fromIndex = index + 1,
                                toIndex = currentReplies.size,
                            ),
                        )
                    }
                }
            updateState {
                it.copy(
                    replies =
                    it.replies.mapIndexed { idx, list ->
                        if (idx == it.currentIndex) {
                            replies
                        } else {
                            list
                        }
                    },
                )
            }
        }
    }

    private suspend fun List<TimelineEntryModel>.preloadImages() {
        flatMap { entry ->
            entry.original.urlsForPreload
        }.forEach { url ->
            imagePreloadManager.preload(url)
        }
        flatMap { entry ->
            entry.blurHashParamsForPreload
        }.forEach {
            blurHashRepository.preload(it)
        }
    }

    private suspend fun updateEntryInState(entryId: String, block: (TimelineEntryModel) -> TimelineEntryModel) {
        val mainEntry = currentMainEntry ?: return
        when (entryId) {
            mainEntry.id -> {
                updateState {
                    it.copy(
                        mainEntries =
                        it.mainEntries.mapIndexed { idx, entry ->
                            if (idx == it.currentIndex) {
                                mainEntry.let(block)
                            } else {
                                entry
                            }
                        },
                    )
                }
            }

            mainEntry.reblog?.id -> {
                updateState {
                    it.copy(
                        mainEntries =
                        it.mainEntries.mapIndexed { idx, entry ->
                            if (idx == it.currentIndex) {
                                mainEntry.copy(reblog = mainEntry.reblog?.let(block))
                            } else {
                                entry
                            }
                        },
                    )
                }
            }

            else -> {
                updateState {
                    it.copy(
                        replies =
                        it.replies.mapIndexed { idx, list ->
                            if (idx == it.currentIndex) {
                                list.map { entry ->
                                    when {
                                        entry.id == entryId -> entry.let(block)
                                        entry.reblog?.id == entryId ->
                                            entry.copy(reblog = entry.reblog?.let(block))

                                        else -> entry
                                    }
                                }
                            } else {
                                list
                            }
                        },
                    )
                }
            }
        }
    }

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                replies =
                it.replies.mapIndexed { idx, list ->
                    if (idx == it.currentIndex) {
                        list.filter { e -> e.id != entryId && e.reblog?.id != entryId }
                    } else {
                        list
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
                    it
                        .copy(
                            reblogged = newEntry.reblogged,
                            reblogCount = newEntry.reblogCount,
                            reblogLoading = false,
                        ).also { entry ->
                            notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                        }
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
                toggleEntryFavorite(entry)?.also { e ->
                    notificationCenter.send(TimelineEntryUpdatedEvent(entry = e))
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    newEntry
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

    private fun toggleDislike(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    dislikeLoading = true,
                )
            }
            val newEntry =
                toggleEntryDislike(entry)?.also { e ->
                    notificationCenter.send(TimelineEntryUpdatedEvent(entry = e))
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    newEntry
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        dislikeLoading = false,
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
                    it
                        .copy(
                            bookmarked = newEntry.bookmarked,
                            bookmarkLoading = false,
                        ).also { entry ->
                            notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                        }
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
                notificationCenter.send(TimelineEntryDeletedEvent(entryId))
                removeEntryFromState(entryId)
            }
        }
    }

    private fun mute(userId: String, entryId: String, duration: Duration, disableNotifications: Boolean) {
        screenModelScope.launch {
            val res =
                userRepository.mute(
                    id = userId,
                    durationSeconds = if (duration.isInfinite()) 0 else duration.inWholeSeconds,
                    notifications = disableNotifications,
                )
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun block(userId: String, entryId: String) {
        screenModelScope.launch {
            val res = userRepository.block(userId)
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun submitPoll(entry: TimelineEntryModel, choices: List<Int>) {
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
                updateEntryInState(entry.id) {
                    it.copy(poll = poll.copy(loading = false)).also { entry ->
                        notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                    }
                }
                emitEffect(ThreadMviModel.Effect.PollVoteFailure)
            }
        }
    }

    private fun copyToClipboard(entry: TimelineEntryModel) {
        screenModelScope.launch {
            val source = timelineEntryRepository.getSource(entry.id)
            if (source != null) {
                val text =
                    buildString {
                        if (!entry.title.isNullOrBlank()) {
                            append(entry.title)
                            append("\n")
                        }
                        append(source.content)
                    }
                emitEffect(ThreadMviModel.Effect.TriggerCopy(text))
            }
        }
    }

    private fun toggleTranslation(entry: TimelineEntryModel) {
        val targetLang = uiState.value.lang ?: return
        check(!entry.translationLoading) { return }

        screenModelScope.launch {
            updateEntryInState(entry.id) { entry.copy(translationLoading = true) }
            val isBeingTranslated = !entry.isShowingTranslation

            val (translation, provider) =
                when {
                    isBeingTranslated && entry.translation == null -> {
                        val result = getTranslation(entry = entry, targetLang = targetLang)
                        result?.target to result?.provider
                    }

                    isBeingTranslated -> entry.translation to entry.translationProvider

                    else -> entry to entry.translationProvider
                }
            val newEntry =
                entry.copy(
                    isShowingTranslation = isBeingTranslated,
                    translation = translation,
                    translationProvider = provider,
                    translationLoading = false,
                )
            updateEntryInState(entry.id) { newEntry }
        }
    }

    private fun changeNavigationIndex(newIndex: Int) {
        check(swipeNavigationEnabled) { return }
        screenModelScope.launch {
            updateState {
                it.copy(
                    currentIndex = newIndex,
                )
            }
            val currentState = uiState.value
            val mainEntry = currentMainEntry
            val replies = currentState.replies.getOrNull(newIndex).orEmpty()
            val hasReplies = (mainEntry?.original?.replyCount ?: 0) > 0
            if (replies.isEmpty() && hasReplies) {
                updateState { it.copy(initial = true) }
                loadReplies()
            }
            updateState { it.copy(initial = false) }

            if (newIndex.isNearTheEnd(timelineNavigationManager.currentList)) {
                loadNavigationNextPage()
            }
        }
    }

    private suspend fun loadNavigationNextPage() {
        check(swipeNavigationEnabled) { return }
        timelineNavigationManager.loadNextPage()
        updateState {
            val currentEntries = it.mainEntries
            val newEntries =
                timelineNavigationManager.currentList.drop(currentEntries.size)
            val mainEntries = currentEntries + newEntries
            val replies =
                buildList<List<TimelineEntryModel>> {
                    addAll(it.replies)
                    val sizeDiff = abs(mainEntries.size - it.replies.size)
                    repeat(sizeDiff) {
                        add(emptyList())
                    }
                }

            check(mainEntries.size == replies.size)
            it.copy(
                mainEntries = mainEntries,
                replies = replies,
            )
        }
    }

    private fun addInstanceShortcut(nodeName: String) {
        screenModelScope.launch {
            accountRepository.getActive()?.id?.also { accountId ->
                instanceShortcutRepository.create(
                    accountId = accountId,
                    node = nodeName,
                )
            }
        }
    }

    private fun openInBrowser(entry: TimelineEntryModel) {
        screenModelScope.launch {
            val url = getInnerUrl(entry)
            if (url != null) {
                emitEffect(ThreadMviModel.Effect.OpenUrl(url))
            }
        }
    }
}
