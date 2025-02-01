package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
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
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.InstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

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
    private val getTranslation: GetTranslationUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<TimelineMviModel.Intent, TimelineMviModel.State, TimelineMviModel.Effect>(
        initialState = TimelineMviModel.State(),
    ),
    TimelineMviModel {
    private var circlesRefreshed = false

    init {
        screenModelScope.launch {
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

            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
                    refreshCirclesInTimelineTypes(currentUser != null)
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
            }.debounce(750)
                .distinctUntilChanged()
                .onEach { (settings, user) ->
                    circlesRefreshed = false
                    val hasSettings = settings != null
                    // wait until either there is a logged user if there are valid credentials stored
                    val hasUser =
                        user != null || !apiConfigurationRepository.hasCachedAuthCredentials()
                    if (hasSettings && hasUser) {
                        refresh(
                            initial = true,
                            forceRefresh = true,
                        )
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: TimelineMviModel.Intent) {
        when (intent) {
            TimelineMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            TimelineMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            is TimelineMviModel.Intent.ChangeType ->
                screenModelScope.launch {
                    changeTimelineType(intent.type)
                }

            is TimelineMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is TimelineMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is TimelineMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is TimelineMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is TimelineMviModel.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )
            is TimelineMviModel.Intent.BlockUser ->
                block(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )
            is TimelineMviModel.Intent.TogglePin -> togglePin(intent.entry)
            is TimelineMviModel.Intent.SubmitPollVote ->
                submitPoll(
                    intent.entry,
                    intent.choices,
                )

            is TimelineMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
            is TimelineMviModel.Intent.ToggleDislike -> toggleDislike(intent.entry)
            is TimelineMviModel.Intent.ToggleTranslation -> toggleTranslation(intent.entry)
            is TimelineMviModel.Intent.WillOpenDetail ->
                screenModelScope.launch {
                    val state = paginationManager.extractState()
                    timelineNavigationManager.push(state)
                    emitEffect(TimelineMviModel.Effect.OpenDetail(intent.entry))
                }

            is TimelineMviModel.Intent.AddInstanceShortcut -> addInstanceShortcut(intent.node)
        }
    }

    private suspend fun changeTimelineType(type: TimelineType) {
        updateState {
            it.copy(
                initial = true,
                timelineType = type,
            )
        }
        emitEffect(TimelineMviModel.Effect.BackToTop)
        refresh(
            initial = true,
            forceRefresh = true,
        )
    }

    private suspend fun refreshCirclesInTimelineTypes(isLogged: Boolean) {
        val circles = circlesRepository.getAll().orEmpty()
        val settings = settingsRepository.current.value ?: SettingsModel()
        val defaultTimelineTypes =
            buildList {
                this += TimelineType.All
                if (isLogged) {
                    this += TimelineType.Subscriptions
                }
                this += TimelineType.Local
            }
        val newTimelineTypes =
            defaultTimelineTypes + circles.map { TimelineType.Circle(circle = it) }
        val currentTimelineType = uiState.value.timelineType
        val newTimelineType =
            if (currentTimelineType is TimelineType.Circle) {
                val currentCircleId = currentTimelineType.circle?.id
                val newCircle = circles.firstOrNull { it.id == currentCircleId }
                if (newCircle == null) {
                    // circle has been deleted
                    settings.defaultTimelineType
                        .toTimelineType()
                        .takeIf { type ->
                            type !is TimelineType.Circle || settings.defaultTimelineId != currentCircleId
                        } ?: TimelineType.Local
                } else {
                    // circle has been renamed
                    TimelineType.Circle(newCircle)
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
    }

    private suspend fun refresh(
        initial: Boolean = false,
        forceRefresh: Boolean = false,
    ) {
        // do not do anything if type is unknown
        val timelineType = uiState.value.timelineType ?: return

        // workaround to handle refresh after initial network call failed
        if (activeAccountMonitor.isNotLoggedButItShould()) {
            activeAccountMonitor.forceRefresh()
            return
        }
        if (circlesRefreshed) {
            // needed as a last-resort to update circles if edited elsewhere
            circlesRefreshed = true
            val isLogged = identityRepository.currentUser.value != null
            refreshCirclesInTimelineTypes(isLogged)
        }

        updateState {
            it.copy(initial = initial, refreshing = !initial)
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
        if (uiState.value.loading) {
            return
        }

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
            emitEffect(TimelineMviModel.Effect.BackToTop)
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
                emitEffect(TimelineMviModel.Effect.PollVoteFailure)
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
                emitEffect(TimelineMviModel.Effect.TriggerCopy(text))
            }
        }
    }

    private fun toggleTranslation(entry: TimelineEntryModel) {
        val targetLang = uiState.value.lang ?: return
        if (entry.translationLoading) {
            return
        }

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
}
