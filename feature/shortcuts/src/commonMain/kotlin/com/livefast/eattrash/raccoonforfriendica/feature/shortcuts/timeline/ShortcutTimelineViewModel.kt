package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ShortcutTimelineViewModel(
    private val node: String,
    private val paginationManager: TimelinePaginationManager,
    private val identityRepository: IdentityRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val settingsRepository: SettingsRepository,
    private val searchRepository: SearchRepository,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val getTranslation: GetTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<ShortcutTimelineMviModel.Intent, ShortcutTimelineMviModel.State, ShortcutTimelineMviModel.Effect>(
        initialState = ShortcutTimelineMviModel.State(),
    ),
    ShortcutTimelineMviModel {
    init {
        screenModelScope.launch {
            updateState { it.copy(nodeName = node) }

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            blurNsfw = settings?.blurNsfw ?: true,
                            maxBodyLines = settings?.maxPostBodyLines ?: Int.MAX_VALUE,
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

            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
                }.launchIn(this)

            notificationCenter
                .subscribe(TimelineEntryUpdatedEvent::class)
                .onEach { event ->
                    updateEntryInState(event.entry.id) { event.entry }
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(initial = true, forceRefresh = true)
            }
        }
    }

    override fun reduce(intent: ShortcutTimelineMviModel.Intent) {
        when (intent) {
            ShortcutTimelineMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            ShortcutTimelineMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            is ShortcutTimelineMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is ShortcutTimelineMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is ShortcutTimelineMviModel.Intent.ToggleDislike -> toggleDislike(intent.entry)
            is ShortcutTimelineMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)

            is ShortcutTimelineMviModel.Intent.SubmitPollVote ->
                submitPoll(
                    intent.entry,
                    intent.choices,
                )

            is ShortcutTimelineMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
            is ShortcutTimelineMviModel.Intent.ToggleTranslation -> toggleTranslation(intent.entry)
            is ShortcutTimelineMviModel.Intent.WillOpenDetail ->
                screenModelScope.launch {
                    val state = paginationManager.extractState()
                    timelineNavigationManager.push(state)
                    emitEffect(ShortcutTimelineMviModel.Effect.OpenDetail(intent.entry))
                }
            is ShortcutTimelineMviModel.Intent.OpenInBrowser -> openInBrowser(intent.entry)
        }
    }

    private suspend fun refresh(
        initial: Boolean = false,
        forceRefresh: Boolean = false,
    ) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val settings = settingsRepository.current.value ?: SettingsModel()
        paginationManager.reset(
            TimelinePaginationSpecification.Feed(
                timelineType = TimelineType.Foreign(node),
                includeNsfw = settings.includeNsfw,
                excludeReplies = settings.excludeRepliesFromTimeline,
                refresh = forceRefresh || !initial,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        check(!uiState.value.loading) { return }

        val wasRefreshing = uiState.value.refreshing
        updateState { it.copy(loading = true) }
        val entries = paginationManager.loadNextPage()
        entries.preloadImages()
        updateState {
            it.copy(
                entries = entries,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
        if (wasRefreshing) {
            emitEffect(ShortcutTimelineMviModel.Effect.BackToTop)
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
                    it.entries.map { entry ->
                        when {
                            entry.id == entryId -> entry.let(block)

                            entry.reblog?.id == entryId ->
                                entry.copy(reblog = entry.reblog?.let(block))

                            else -> entry
                        }
                    },
            )
        }
    }

    private suspend fun resolveToLocal(entry: TimelineEntryModel): TimelineEntryModel? =
        entry.url?.let { url ->
            val results =
                searchRepository.search(
                    query = url,
                    resolve = true,
                    type = SearchResultType.Entries,
                )
            val result = results?.firstOrNull() as? ExploreItemModel.Entry
            result?.entry
        }

    private fun toggleReblog(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(reblogLoading = true)
            }
            val newEntry =
                resolveToLocal(entry)?.let { le ->
                    if (le.reblogged) {
                        timelineEntryRepository.unreblog(le.id)
                    } else {
                        timelineEntryRepository.reblog(le.id)
                    }
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
                    it.copy(reblogLoading = false)
                }
                emitEffect(ShortcutTimelineMviModel.Effect.Failure)
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

            val localEntry =
                resolveToLocal(entry)
                    ?.let { le ->
                        toggleEntryFavorite(le)
                    }?.also { le ->
                        notificationCenter.send(TimelineEntryUpdatedEvent(entry = le))
                    }
            if (localEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        favorite = localEntry.favorite,
                        favoriteCount = localEntry.favoriteCount,
                        disliked = localEntry.disliked,
                        dislikesCount = localEntry.dislikesCount,
                        favoriteLoading = false,
                    )
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(favoriteLoading = false)
                }
                emitEffect(ShortcutTimelineMviModel.Effect.Failure)
            }
        }
    }

    private fun toggleDislike(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(dislikeLoading = true)
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
                it.copy(bookmarkLoading = true)
            }
            val newEntry =
                resolveToLocal(entry)?.let { le ->
                    if (le.bookmarked) {
                        timelineEntryRepository.unbookmark(le.id)
                    } else {
                        timelineEntryRepository.bookmark(le.id)
                    }
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
                    it.copy(bookmarkLoading = false)
                }
                emitEffect(ShortcutTimelineMviModel.Effect.Failure)
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
                resolveToLocal(entry)?.poll?.let { poll ->
                    timelineEntryRepository.submitPoll(
                        pollId = poll.id,
                        choices = choices,
                    )
                }
            if (newPoll != null) {
                updateEntryInState(entry.id) {
                    it.copy(poll = newPoll).also { entry ->
                        notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                    }
                }
            } else {
                updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = false)) }
                emitEffect(ShortcutTimelineMviModel.Effect.PollVoteFailure)
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
                emitEffect(ShortcutTimelineMviModel.Effect.TriggerCopy(text))
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

    private fun openInBrowser(entry: TimelineEntryModel) {
        screenModelScope.launch {
            val url = getInnerUrl(entry)
            if (url != null) {
                emitEffect(ShortcutTimelineMviModel.Effect.OpenUrl(url))
            }
        }
    }
}
