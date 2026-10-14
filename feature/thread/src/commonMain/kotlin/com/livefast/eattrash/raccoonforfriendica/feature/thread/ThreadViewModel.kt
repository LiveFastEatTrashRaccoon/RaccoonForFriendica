package com.livefast.eattrash.raccoonforfriendica.feature.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
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
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.EntryActionHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.PopulateThreadUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.InstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration

@AssistedInject
class ThreadViewModel(
    @Assisted private val args: ThreadViewModelArgs,
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
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val entryActionHelper: EntryActionHelper,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val populateThread: PopulateThreadUseCase,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val toggleTranslation: ToggleTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<ThreadMvi.Intent, ThreadMvi.State, ThreadMvi.Effect>
    by DefaultMviDelegate(initialState = ThreadMvi.State()),
    ThreadMvi {

    private val currentMainEntry: TimelineEntryModel?
        get() {
            val currentState = uiState.value
            return currentState.mainEntries.getOrNull(currentState.currentIndex)
        }

    init {
        viewModelScope.launch {
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

    override fun reduce(intent: ThreadMvi.Intent) {
        when (intent) {
            ThreadMvi.Intent.Refresh -> handleRefresh()

            is ThreadMvi.Intent.LoadMoreReplies -> handleLoadMoreReplies(intent.entry)

            is ThreadMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.entry)

            is ThreadMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.entry)

            is ThreadMvi.Intent.ToggleDislike -> handleToggleDislike(intent.entry)

            is ThreadMvi.Intent.ToggleReblog -> handleToggleReblog(intent.entry)

            is ThreadMvi.Intent.DeleteEntry -> handleDeleteEntry(intent.entryId)

            is ThreadMvi.Intent.MuteUser -> handleMuteUser(
                userId = intent.userId,
                entryId = intent.entryId,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            is ThreadMvi.Intent.BlockUser -> handleBlockUser(userId = intent.userId, entryId = intent.entryId)

            is ThreadMvi.Intent.SubmitPollVote -> handleSubmitPollVote(intent.entry, intent.choices)

            is ThreadMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.entry)

            is ThreadMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.entry)

            is ThreadMvi.Intent.ChangeNavigationIndex -> handleChangeNavigationIndex(intent.index)

            is ThreadMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is ThreadMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.entry)
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            refresh()
        }
    }

    private fun handleLoadMoreReplies(entry: TimelineEntryModel) {
        viewModelScope.launch {
            loadMoreReplies(entry)
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
            val currentEntry = entryCache.get(args.entryId)
            val mainEntries =
                if (args.swipeNavigationEnabled) {
                    timelineNavigationManager.currentList
                } else {
                    currentEntry?.let { listOf(it) } ?: emptyList()
                }
            val initialIndex = mainEntries.indexOfFirst { it.id == args.entryId }.coerceAtLeast(0)
            val replies: List<List<TimelineEntryModel>> = mainEntries.map { emptyList() }
            check(mainEntries.size == replies.size) { "Unable to create navigation set " }
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
        val result = populateThread(entry = mainEntry).map { entry ->
            entry.let {
                with(emojiHelper) { it.withEmojisIfMissing() }
            }.let {
                with(replyHelper) { it.withInReplyToIfMissing() }
            }.let {
                with(entryActionHelper) { it.withActions() }
            }
        }
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
        if (entry.loadMoreButtonLoading) {
            return
        }
        val currentState = uiState.value
        val currentReplies = currentState.replies[currentState.currentIndex]
        updateEntryInState(entry.id) { it.copy(loadMoreButtonLoading = true) }
        val result = populateThread(entry = entry.original).map { entry ->
            entry.let {
                with(emojiHelper) { it.withEmojisIfMissing() }
            }.let {
                with(replyHelper) { it.withInReplyToIfMissing() }
            }.let {
                with(entryActionHelper) { it.withActions() }
            }
        }
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

    private fun handleToggleReblog(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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

    private fun handleToggleFavorite(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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

    private fun handleToggleDislike(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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

    private fun handleToggleBookmark(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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

    private fun handleDeleteEntry(entryId: String) {
        viewModelScope.launch {
            val success = timelineEntryRepository.delete(entryId)
            if (success) {
                notificationCenter.send(TimelineEntryDeletedEvent(entryId))
                removeEntryFromState(entryId)
            }
        }
    }

    private fun handleMuteUser(userId: String, entryId: String, duration: Duration, disableNotifications: Boolean) {
        viewModelScope.launch {
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

    private fun handleBlockUser(userId: String, entryId: String) {
        viewModelScope.launch {
            val res = userRepository.block(userId)
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun handleSubmitPollVote(entry: TimelineEntryModel, choices: List<Int>) {
        val poll = entry.poll ?: return
        viewModelScope.launch {
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
                emitEffect(ThreadMvi.Effect.PollVoteFailure)
            }
        }
    }

    private fun handleCopyToClipboard(entry: TimelineEntryModel) {
        viewModelScope.launch {
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
                emitEffect(ThreadMvi.Effect.TriggerCopy(text))
            }
        }
    }

    private fun handleToggleTranslation(entry: TimelineEntryModel) {
        val targetLang = uiState.value.lang ?: return
        if (entry.translationLoading) {
            return
        }

        viewModelScope.launch {
            updateEntryInState(entry.id) { entry.copy(translationLoading = true) }
            val newEntry = toggleTranslation(entry = entry, targetLang = targetLang)
            updateEntryInState(entry.id) { newEntry }
        }
    }

    private fun handleChangeNavigationIndex(newIndex: Int) {
        if (!args.swipeNavigationEnabled) {
            return
        }
        viewModelScope.launch {
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
        if (!args.swipeNavigationEnabled) {
            return
        }
        timelineNavigationManager.loadNextPage()
        updateState {
            val currentEntries = it.mainEntries
            val newEntries =
                timelineNavigationManager.currentList.drop(currentEntries.size)
            val mainEntries = currentEntries + newEntries
            val replies =
                buildList {
                    addAll(it.replies)
                    val sizeDiff = abs(mainEntries.size - it.replies.size)
                    repeat(sizeDiff) {
                        add(emptyList())
                    }
                }

            check(mainEntries.size == replies.size) { "Unable to create navigation entry set" }
            it.copy(
                mainEntries = mainEntries,
                replies = replies,
            )
        }
    }

    private fun handleAddInstanceShortcut(nodeName: String) {
        viewModelScope.launch {
            accountRepository.getActive()?.id?.also { accountId ->
                instanceShortcutRepository.create(
                    accountId = accountId,
                    node = nodeName,
                )
            }
        }
    }

    private fun handleOpenInBrowser(entry: TimelineEntryModel) {
        viewModelScope.launch {
            val url = getInnerUrl(entry)
            if (url != null) {
                emitEffect(ThreadMvi.Effect.OpenUrl(url))
            }
        }
    }

    companion object {
        fun getExtras(args: ThreadViewModelArgs) = CreationExtras {
            this[KEY_ARGS] = args
        }
    }
}

private val KEY_ARGS = CreationExtras.Key<ThreadViewModelArgs>()

data class ThreadViewModelArgs(val entryId: String, val swipeNavigationEnabled: Boolean)

@AssistedFactory
@ViewModelAssistedFactoryKey(ThreadViewModel::class)
@ContributesIntoMap(AppScope::class)
interface ThreadViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ThreadViewModel =
        create(extras[KEY_ARGS] ?: error("ViewModel creation args not found"))

    fun create(@Assisted args: ThreadViewModelArgs): ThreadViewModel
}
