package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryCreatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
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
import kotlin.time.Duration

class EntryDetailViewModel(
    private val id: String,
    private val swipeNavigationEnabled: Boolean,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val blurHashRepository: BlurHashRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val hapticFeedback: HapticFeedback,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
    private val imagePreloadManager: ImagePreloadManager,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val getTranslation: GetTranslationUseCase,
    private val populateThread: PopulateThreadUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<EntryDetailMviModel.Intent, EntryDetailMviModel.State, EntryDetailMviModel.Effect>(
        initialState = EntryDetailMviModel.State(),
    ),
    EntryDetailMviModel {
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
            notificationCenter
                .subscribe(TimelineEntryDeletedEvent::class)
                .onEach { event ->
                    removeEntryFromState(event.id)
                }.launchIn(this)
            notificationCenter
                .subscribe(TimelineEntryCreatedEvent::class)
                .onEach { event ->
                    addEntryInState(event.entry)
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

    override fun onDispose() {
        super.onDispose()
        if (swipeNavigationEnabled) {
            timelineNavigationManager.pop()
        }
    }

    override fun reduce(intent: EntryDetailMviModel.Intent) {
        when (intent) {
            EntryDetailMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is EntryDetailMviModel.Intent.LoadMoreReplies ->
                screenModelScope.launch {
                    loadMoreReplies(intent.entry)
                }

            is EntryDetailMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is EntryDetailMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is EntryDetailMviModel.Intent.ToggleDislike -> toggleDislike(intent.entry)
            is EntryDetailMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is EntryDetailMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is EntryDetailMviModel.Intent.MuteUser ->
                mute(
                    entryId = intent.entryId,
                    userId = intent.entryId,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )
            is EntryDetailMviModel.Intent.BlockUser ->
                block(
                    entryId = intent.entryId,
                    userId = intent.entryId,
                )
            is EntryDetailMviModel.Intent.TogglePin -> togglePin(intent.entry)
            is EntryDetailMviModel.Intent.SubmitPollVote -> submitPoll(intent.entry, intent.choices)
            is EntryDetailMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
            is EntryDetailMviModel.Intent.ToggleTranslation -> toggleTranslation(intent.entry)
            is EntryDetailMviModel.Intent.ChangeNavigationIndex ->
                changeNavigationIndex(intent.index)
            is EntryDetailMviModel.Intent.AddInstanceShortcut -> addInstanceShortcut(intent.node)
            is EntryDetailMviModel.Intent.OpenInBrowser -> openInBrowser(intent.entry)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(
                initial = initial,
                refreshing = !initial,
            )
        }

        if (initial) {
            val currentEntry = entryCache.get(id)
            val entries =
                if (swipeNavigationEnabled) {
                    timelineNavigationManager.currentList.map { e -> listOf(e) }
                } else {
                    currentEntry?.let { listOf(listOf(it)) } ?: listOf(emptyList())
                }
            val initialIndex =
                entries.indexOfFirst { list ->
                    list.any { e -> e.original.id == currentEntry?.original?.id }
                }
            updateState {
                it.copy(
                    mainEntry = currentEntry,
                    currentIndex = initialIndex,
                    entries =
                        if (initial) {
                            entries
                        } else {
                            it.entries
                        },
                )
            }

            if (initialIndex.isNearTheEnd(timelineNavigationManager.currentList)) {
                loadNavigationNextPage()
            }
        }

        loadContext()

        if (initial) {
            val currentState = uiState.value
            val currentEntryList = currentState.entries.getOrNull(currentState.currentIndex).orEmpty()
            val index = currentEntryList.indexOf(currentState.mainEntry).takeIf { it > 0 } ?: 0
            emitEffect(EntryDetailMviModel.Effect.ScrollToItem(index))
        }
    }

    private suspend fun loadContext() {
        check(!uiState.value.loading) { return }

        updateState { it.copy(loading = true) }
        val mainEntry = uiState.value.mainEntry
        val maxDepth = settingsRepository.current.value?.replyDepth ?: 1
        val context =
            mainEntry?.let { e ->
                timelineEntryRepository.getContext(e.id)
            }
        val root = context?.ancestors?.firstOrNull() ?: mainEntry ?: return
        val currentEntryList =
            populateThread(entry = root, maxDepth = maxDepth)
                .map {
                    with(emojiHelper) { it.withEmojisIfMissing() }
                }.map {
                    with(replyHelper) { it.withInReplyToIfMissing() }
                }.let { list ->
                    when {
                        mainEntry != null && list.none { e -> e.id == mainEntry.id } -> {
                            val additionalList =
                                reconstructAncestors(
                                    entry = mainEntry,
                                    rootId = root.id,
                                )
                            list + additionalList.drop(1)
                        }

                        else -> list
                    }
                }.distinctBy { e -> e.safeKey }
        currentEntryList.preloadImages()
        updateState {
            it.copy(
                entries =
                    it.entries.mapIndexed { idx, entryList ->
                        if (idx == it.currentIndex) {
                            currentEntryList
                        } else {
                            entryList
                        }
                    },
                refreshing = false,
                initial = false,
                loading = false,
            )
        }
    }

    private suspend fun reconstructAncestors(
        entry: TimelineEntryModel,
        rootId: String,
    ): List<TimelineEntryModel> {
        suspend fun buildAncestorsRec(
            entry: TimelineEntryModel,
            list: List<TimelineEntryModel>,
        ): List<TimelineEntryModel> {
            val parentId = entry.parentId
            check(!parentId.isNullOrEmpty() && entry.id != rootId) { return list }
            val parent = timelineEntryRepository.getById(parentId) ?: return list
            return buildAncestorsRec(parent, listOf(parent) + list)
        }

        val res = buildAncestorsRec(entry, listOf(entry))
        return res
            .mapIndexed { idx, e ->
                val hasMoreReplies =
                    when {
                        idx == res.lastIndex -> e.replyCount > 0
                        else -> e.replyCount > 1
                    }
                e.copy(
                    depth = idx,
                    loadMoreButtonVisible = hasMoreReplies,
                )
            }
    }

    private suspend fun loadMoreReplies(entry: TimelineEntryModel) {
        check(!entry.loadMoreButtonLoading) { return }

        val currentState = uiState.value
        val currentReplies = currentState.entries.getOrNull(currentState.currentIndex).orEmpty()
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
                    entries =
                        it.entries.mapIndexed { idx, list ->
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

    private suspend fun updateEntryInState(
        entryId: String,
        block: (TimelineEntryModel) -> TimelineEntryModel,
    ) {
        updateState {
            it.copy(
                entries =
                    it.entries.mapIndexed { idx, entryList ->
                        if (idx == uiState.value.currentIndex) {
                            entryList.map { entry ->
                                when {
                                    entry.id == entryId -> {
                                        entry.let(block)
                                    }

                                    entry.reblog?.id == entryId -> {
                                        entry.copy(reblog = entry.reblog?.let(block))
                                    }

                                    else -> {
                                        entry
                                    }
                                }
                            }
                        } else {
                            entryList
                        }
                    },
            )
        }
    }

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                entries =
                    it.entries.mapIndexed { idx, entryList ->
                        if (idx == it.currentIndex) {
                            entryList.filter { e -> e.id != entryId && e.reblog?.id != entryId }
                        } else {
                            entryList
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

    private fun mute(
        userId: String,
        entryId: String,
        duration: Duration,
        disableNotifications: Boolean,
    ) {
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
                updateEntryInState(entry.id) {
                    it.copy(poll = newPoll).also { entry ->
                        notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                    }
                }
            } else {
                updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = false)) }
                emitEffect(EntryDetailMviModel.Effect.PollVoteFailure)
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
                emitEffect(EntryDetailMviModel.Effect.TriggerCopy(text))
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
                    mainEntry = it.entries.getOrNull(newIndex)?.firstOrNull(),
                )
            }
            val currentState = uiState.value
            val hasReplies = (currentState.mainEntry?.replyCount ?: 0) > 0
            val currentEntries = currentState.entries.getOrNull(newIndex) ?: emptyList()
            if (currentEntries.isEmpty() || (currentEntries.size == 1 && hasReplies)) {
                loadContext()
            }

            if (newIndex.isNearTheEnd(timelineNavigationManager.currentList)) {
                loadNavigationNextPage()
            }
        }
    }

    private suspend fun loadNavigationNextPage() {
        check(swipeNavigationEnabled) { return }

        timelineNavigationManager.loadNextPage()
        updateState {
            val currentEntries = it.entries
            val newEntries =
                timelineNavigationManager.currentList
                    .map { e ->
                        listOf(e)
                    }.drop(currentEntries.size)
            it.copy(
                entries = currentEntries + newEntries,
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

    private suspend fun addEntryInState(entry: TimelineEntryModel) {
        val currentEntries = uiState.value.let { it.entries.getOrNull(it.currentIndex) }.orEmpty()
        val parentIndex =
            currentEntries
                .indexOfFirst { e ->
                    e.id == entry.parentId || e.original.id == entry.parentId
                }.takeIf { it >= 0 } ?: return
        val newEntries =
            buildList {
                // insert everything included the parent
                addAll(currentEntries.subList(0, parentIndex + 1))
                var indexOfFirstNotDescendant = -1
                val familyMembers = mutableListOf(currentEntries[parentIndex].id)
                for (i in parentIndex + 1 until currentEntries.size) {
                    val e = currentEntries[i]
                    if (e.original.parentId in familyMembers) {
                        familyMembers += e.original.id
                        add(e)
                    } else {
                        indexOfFirstNotDescendant = i
                        break
                    }
                }
                // insert the new entry with its depth
                val depth = currentEntries[parentIndex].depth + 1
                add(entry.copy(depth = depth))
                // add all the rest
                if (indexOfFirstNotDescendant >= 0 && indexOfFirstNotDescendant < currentEntries.size) {
                    addAll(currentEntries.subList(indexOfFirstNotDescendant, currentEntries.size))
                }
            }
        updateState {
            it.copy(
                entries =
                    it.entries.mapIndexed { idx, entryList ->
                        if (idx == it.currentIndex) {
                            newEntries
                        } else {
                            entryList
                        }
                    },
            )
        }
    }

    private fun openInBrowser(entry: TimelineEntryModel) {
        screenModelScope.launch {
            val url = getInnerUrl(entry)
            if (url != null) {
                emitEffect(EntryDetailMviModel.Effect.OpenUrl(url))
            }
        }
    }
}
