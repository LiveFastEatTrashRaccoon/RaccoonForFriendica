package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toTimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementsManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FollowedHashtagCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.InstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
@OptIn(FlowPreview::class)
class TimelineViewModel(
    private val paginationManager: TimelinePaginationManager,
    private val identityRepository: IdentityRepository,
    private val activeAccountMonitor: ActiveAccountMonitor,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val circlesRepository: CirclesRepository,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val announcementsManager: AnnouncementsManager,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val toggleTranslation: ToggleTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val getTimelineTypes: GetTimelineTypesUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val followedHashtagCache: FollowedHashtagCache,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<TimelineMvi.Intent, TimelineMvi.State, TimelineMvi.Effect>
    by DefaultMviDelegate(initialState = TimelineMvi.State()),
    TimelineMvi {
    init {
        viewModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    val defaultCircle =
                        settings?.defaultTimelineId?.let { circlesRepository.get(it) }
                    val defaultTimelineType =
                        settings?.defaultTimelineType?.toTimelineType().let { type ->
                            when (type) {
                                is TimelineType.Circle -> type.copy(circle = defaultCircle)
                                else -> type
                            }
                        }
                    updateState {
                        it.copy(
                            timelineType = defaultTimelineType,
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

            getTimelineTypes()
                .onEach { newTimelineTypes ->
                    val settings = settingsRepository.current.value ?: SettingsModel()
                    val currentTimelineType = uiState.value.timelineType
                    val newTimelineType =
                        if (currentTimelineType is TimelineType.Circle) {
                            val currentCircleId = currentTimelineType.circle?.id
                            val newCircleTimelineType = newTimelineTypes.firstOrNull {
                                (it as? TimelineType.Circle)?.circle?.id == currentCircleId
                            }
                            newCircleTimelineType ?: run {
                                // circle has been deleted
                                settings.defaultTimelineType
                                    .toTimelineType()
                                    .takeIf { type -> type !is TimelineType.Circle } ?: TimelineType.Local
                            }
                        } else {
                            currentTimelineType
                        }
                    updateState {
                        it.copy(
                            availableTimelineTypes = newTimelineTypes,
                            timelineType = newTimelineType,
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

            announcementsManager.unreadCount
                .onEach { count ->
                    updateState { it.copy(unreadAnnouncements = count) }
                }.launchIn(this)

            apiConfigurationRepository.node
                .onEach { node ->
                    updateState { it.copy(currentNode = node) }
                }.launchIn(this)

            combine(
                settingsRepository.current,
                apiConfigurationRepository.node,
                identityRepository.currentUser,
            ) { settings, _, user ->
                settings to user
            }.debounce(750.milliseconds)
                .distinctUntilChanged()
                .map { it.second }
                .onEach { user ->
                    val cachedAuth = apiConfigurationRepository.hasCachedAuthCredentials()
                    val hasUser = user != null || !cachedAuth
                    if (hasUser) {
                        viewModelScope.launch {
                            refresh(
                                initial = true,
                                forceRefresh = true,
                            )
                        }
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: TimelineMvi.Intent) {
        when (intent) {
            TimelineMvi.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }

            TimelineMvi.Intent.LoadNextPage ->
                viewModelScope.launch {
                    loadNextPage()
                }

            is TimelineMvi.Intent.ChangeType ->
                viewModelScope.launch {
                    changeTimelineType(intent.type)
                }

            is TimelineMvi.Intent.ToggleReblog -> toggleReblog(intent.entry)

            is TimelineMvi.Intent.ToggleFavorite -> toggleFavorite(intent.entry)

            is TimelineMvi.Intent.ToggleBookmark -> toggleBookmark(intent.entry)

            is TimelineMvi.Intent.DeleteEntry -> deleteEntry(intent.entryId)

            is TimelineMvi.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )

            is TimelineMvi.Intent.BlockUser ->
                block(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )

            is TimelineMvi.Intent.TogglePin -> togglePin(intent.entry)

            is TimelineMvi.Intent.SubmitPollVote ->
                submitPoll(
                    intent.entry,
                    intent.choices,
                )

            is TimelineMvi.Intent.CopyToClipboard -> copyToClipboard(intent.entry)

            is TimelineMvi.Intent.ToggleDislike -> toggleDislike(intent.entry)

            is TimelineMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.entry)

            is TimelineMvi.Intent.WillOpenDetail ->
                viewModelScope.launch {
                    val state = paginationManager.extractState()
                    timelineNavigationManager.push(state)
                    emitEffect(TimelineMvi.Effect.OpenDetail(intent.entry))
                }

            is TimelineMvi.Intent.AddInstanceShortcut -> addInstanceShortcut(intent.node)

            is TimelineMvi.Intent.OpenInBrowser -> openInBrowser(intent.entry)
        }
    }

    private suspend fun changeTimelineType(type: TimelineType) {
        updateState {
            it.copy(
                initial = true,
                timelineType = type,
            )
        }
        emitEffect(TimelineMvi.Effect.BackToTop)
        refresh(
            initial = true,
            forceRefresh = true,
        )
    }

    private suspend fun refresh(initial: Boolean = false, forceRefresh: Boolean = false) {
        val timelineType = uiState.value.timelineType ?: return

        val notLogged = activeAccountMonitor.isNotLoggedButItShould()
        if (notLogged) {
            try {
                activeAccountMonitor.forceRefresh()
            } catch (_: Exception) {}
        }

        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }

        if (!initial) {
            followedHashtagCache.refresh()
            getTimelineTypes.refresh()
        }

        val settings = settingsRepository.current.value ?: SettingsModel()
        paginationManager.reset(
            TimelinePaginationSpecification.Feed(
                timelineType = timelineType,
                includeNsfw = settings.includeNsfw,
                excludeReplies = settings.excludeRepliesFromTimeline,
                refresh = forceRefresh || !initial,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        val wasRefreshing = uiState.value.refreshing
        updateState { it.copy(loading = true) }
        try {
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
                emitEffect(TimelineMvi.Effect.BackToTop)
            }
        } catch (e: Exception) {
            updateState { it.copy(loading = false, refreshing = false) }
            if (e is CancellationException) throw e
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
        updateState {
            it.copy(
                entries =
                it.entries.map { entry ->
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
                },
            )
        }
    }

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                entries = it.entries.filter { e -> e.id != entryId && e.reblog?.id != entryId },
            )
        }
    }

    private fun toggleReblog(entry: TimelineEntryModel) {
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

    private fun toggleFavorite(entry: TimelineEntryModel) {
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

    private fun toggleDislike(entry: TimelineEntryModel) {
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

    private fun toggleBookmark(entry: TimelineEntryModel) {
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

    private fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            val success = timelineEntryRepository.delete(entryId)
            if (success) {
                notificationCenter.send(TimelineEntryDeletedEvent(entryId))
                removeEntryFromState(entryId)
            }
        }
    }

    private fun mute(userId: String, entryId: String, duration: Duration, disableNotifications: Boolean) {
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

    private fun block(userId: String, entryId: String) {
        viewModelScope.launch {
            val res = userRepository.block(userId)
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun togglePin(entry: TimelineEntryModel) {
        viewModelScope.launch {
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

    private fun submitPoll(entry: TimelineEntryModel, choices: List<Int>) {
        val poll = entry.poll ?: return
        viewModelScope.launch {
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
                emitEffect(TimelineMvi.Effect.PollVoteFailure)
            }
        }
    }

    private fun copyToClipboard(entry: TimelineEntryModel) {
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
                emitEffect(TimelineMvi.Effect.TriggerCopy(text))
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

    private fun addInstanceShortcut(nodeName: String) {
        viewModelScope.launch {
            accountRepository.getActive()?.id?.also { accountId ->
                instanceShortcutRepository.create(
                    accountId = accountId,
                    node = nodeName,
                )
            }
        }
    }

    private fun openInBrowser(entry: TimelineEntryModel) {
        viewModelScope.launch {
            val url = getInnerUrl(entry)
            if (url != null) {
                emitEffect(TimelineMvi.Effect.OpenUrl(url))
            }
        }
    }
}
