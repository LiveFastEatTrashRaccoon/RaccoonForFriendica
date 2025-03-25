package com.livefast.eattrash.feature.userdetail.classic

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserRateLimitModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetTranslationUseCase
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

class UserDetailViewModel(
    private val id: String,
    private val userRepository: UserRepository,
    private val paginationManager: TimelinePaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val hapticFeedback: HapticFeedback,
    private val userCache: LocalItemCache<UserModel>,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val accountRepository: AccountRepository,
    private val userRateLimitRepository: UserRateLimitRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val emojiHelper: EmojiHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val getTranslation: GetTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<UserDetailMviModel.Intent, UserDetailMviModel.State, UserDetailMviModel.Effect>(
        initialState = UserDetailMviModel.State(),
    ),
    UserDetailMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { user ->
                    updateState { it.copy(currentUserId = user?.id) }
                    loadUser()
                }.launchIn(this)

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

    override fun reduce(intent: UserDetailMviModel.Intent) {
        when (intent) {
            is UserDetailMviModel.Intent.ChangeSection ->
                screenModelScope.launch {
                    check(!uiState.value.loading) { return@launch }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(UserDetailMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            UserDetailMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            UserDetailMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            UserDetailMviModel.Intent.Follow -> follow()
            UserDetailMviModel.Intent.Unfollow -> unfollow()
            is UserDetailMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is UserDetailMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is UserDetailMviModel.Intent.ToggleDislike -> toggleDislike(intent.entry)
            is UserDetailMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            UserDetailMviModel.Intent.DisableNotifications -> toggleNotifications(enabled = false)
            UserDetailMviModel.Intent.EnableNotifications -> toggleNotifications(enabled = true)
            is UserDetailMviModel.Intent.SubmitPollVote -> submitPoll(intent.entry, intent.choices)
            is UserDetailMviModel.Intent.ToggleBlock -> toggleBlock(intent.blocked)
            is UserDetailMviModel.Intent.ToggleMute ->
                toggleMute(
                    muted = intent.muted,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )
            UserDetailMviModel.Intent.TogglePersonalNoteEditMode ->
                toggleEditPersonalNote()

            is UserDetailMviModel.Intent.SetPersonalNote ->
                screenModelScope.launch {
                    updateState { it.copy(personalNote = intent.note) }
                }

            UserDetailMviModel.Intent.SubmitPersonalNote -> updatePersonalNote()
            is UserDetailMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
            is UserDetailMviModel.Intent.SetRateLimit -> setRateLimit(intent.value)
            is UserDetailMviModel.Intent.ToggleTranslation -> toggleTranslation(intent.entry)
            is UserDetailMviModel.Intent.WillOpenDetail ->
                screenModelScope.launch {
                    val state = paginationManager.extractState()
                    timelineNavigationManager.push(state)
                    emitEffect(UserDetailMviModel.Effect.OpenDetail(intent.entry))
                }
            is UserDetailMviModel.Intent.AddInstanceShortcut -> addInstanceShortcut(intent.node)
            is UserDetailMviModel.Intent.OpenInBrowser -> openInBrowser(intent.entry)
        }
    }

    private suspend fun loadUser() {
        val user =
            with(emojiHelper) {
                userCache.get(id)?.withEmojisIfMissing()
            }
        updateState { it.copy(user = user) }
        val relationship =
            if (id != uiState.value.currentUserId) {
                userRepository.getRelationships(listOf(id))?.firstOrNull()
            } else {
                null
            }
        val accountId = accountRepository.getActive()?.id
        val handle = user?.handle.orEmpty()
        val rateLimit =
            if (accountId != null && handle.isNotEmpty()) {
                userRateLimitRepository.getBy(handle = handle, accountId = accountId)
            } else {
                null
            }
        updateState {
            it.copy(
                user =
                    user?.copy(
                        relationshipStatus = relationship?.toStatus(),
                        notificationStatus = relationship?.toNotificationStatus(),
                        muted = relationship?.muting == true,
                        blocked = relationship?.blocking == true,
                    ),
                personalNote = relationship?.note,
                rateLimit = rateLimit,
            )
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            TimelinePaginationSpecification.User(
                userId = id,
                excludeReplies = uiState.value.section == UserSection.Posts,
                onlyMedia = uiState.value.section == UserSection.Media,
                pinned = uiState.value.section == UserSection.Pinned,
                includeNsfw = settingsRepository.current.value?.includeNsfw == true,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        check(!uiState.value.loading) { return }

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

    private fun follow() {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateState { it.copy(user = it.user?.copy(relationshipStatusPending = true)) }
            val newRelationship = userRepository.follow(id)
            val newStatus = newRelationship?.toStatus() ?: uiState.value.user?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: uiState.value.user?.notificationStatus
            updateState {
                it.copy(
                    user =
                        it.user
                            ?.copy(
                                relationshipStatus = newStatus,
                                notificationStatus = newNotificationStatus,
                                relationshipStatusPending = false,
                            )?.also { user ->
                                notificationCenter.send(UserUpdatedEvent(user = user))
                            },
                )
            }
        }
    }

    private fun unfollow() {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateState { it.copy(user = it.user?.copy(relationshipStatusPending = true)) }
            val newRelationship = userRepository.unfollow(id)
            val newStatus = newRelationship?.toStatus() ?: uiState.value.user?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: uiState.value.user?.notificationStatus
            updateState {
                it.copy(
                    user =
                        it.user
                            ?.copy(
                                relationshipStatus = newStatus,
                                notificationStatus = newNotificationStatus,
                                relationshipStatusPending = false,
                            )?.also { user ->
                                notificationCenter.send(UserUpdatedEvent(user = user))
                            },
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

    private fun toggleNotifications(enabled: Boolean) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateState { it.copy(user = it.user?.copy(notificationStatusPending = true)) }
            val newRelationship =
                userRepository.follow(
                    id = id,
                    notifications = enabled,
                )
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: uiState.value.user?.notificationStatus
            updateState {
                it.copy(
                    user =
                        it.user?.copy(
                            notificationStatus = newNotificationStatus,
                            notificationStatusPending = false,
                        ),
                )
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
                emitEffect(UserDetailMviModel.Effect.PollVoteFailure)
            }
        }
    }

    private fun toggleMute(
        muted: Boolean,
        duration: Duration = Duration.INFINITE,
        disableNotifications: Boolean,
    ) {
        screenModelScope.launch {
            val relationship =
                if (muted) {
                    userRepository.mute(
                        id = id,
                        durationSeconds = duration.inWholeSeconds,
                        notifications = disableNotifications,
                    )
                } else {
                    userRepository.unmute(id)
                }
            if (relationship != null) {
                updateState {
                    it.copy(
                        user =
                            it.user?.copy(
                                relationshipStatus = relationship.toStatus(),
                                notificationStatus = relationship.toNotificationStatus(),
                                muted = relationship.muting,
                                blocked = relationship.blocking,
                            ),
                    )
                }
            }
        }
    }

    private fun toggleBlock(blocked: Boolean) {
        screenModelScope.launch {
            val relationship =
                if (blocked) {
                    userRepository.block(id)
                } else {
                    userRepository.unblock(id)
                }
            if (relationship != null) {
                updateState {
                    it.copy(
                        user =
                            it.user?.copy(
                                relationshipStatus = relationship.toStatus(),
                                notificationStatus = relationship.toNotificationStatus(),
                                muted = relationship.muting,
                                blocked = relationship.blocking,
                            ),
                    )
                }
            }
        }
    }

    private fun toggleEditPersonalNote() {
        screenModelScope.launch {
            val currentState = uiState.value
            if (!currentState.personalNoteEditEnabled) {
                updateState {
                    it.copy(personalNoteEditEnabled = true)
                }
            } else {
                val relationship = userRepository.getRelationships(listOf(id))?.firstOrNull()
                updateState {
                    it.copy(
                        personalNote = relationship?.note,
                        personalNoteEditEnabled = false,
                    )
                }
            }
        }
    }

    private fun updatePersonalNote() {
        val note = uiState.value.personalNote ?: return
        screenModelScope.launch {
            val relationShip = userRepository.updatePersonalNote(id, note)
            if (relationShip != null) {
                updateState {
                    it.copy(personalNoteEditEnabled = false)
                }
            } else {
                emitEffect(UserDetailMviModel.Effect.Failure)
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
                emitEffect(UserDetailMviModel.Effect.TriggerCopy(text))
            }
        }
    }

    private fun setRateLimit(value: Double) {
        screenModelScope.launch {
            val accountId = accountRepository.getActive()?.id ?: return@launch
            val handle = uiState.value.user?.handle ?: return@launch

            val currentRate = uiState.value.rateLimit
            val newRate =
                when {
                    value >= 1 && currentRate != null -> {
                        val success = userRateLimitRepository.delete(currentRate.id)
                        if (success) {
                            null
                        } else {
                            currentRate
                        }
                    }

                    value < 1 && currentRate != null -> {
                        userRateLimitRepository.update(currentRate.copy(rate = value))
                    }

                    value < 1 -> {
                        userRateLimitRepository.create(
                            UserRateLimitModel(
                                accountId = accountId,
                                handle = handle,
                                rate = value,
                            ),
                        )
                    }

                    else -> currentRate
                }

            updateState { it.copy(rateLimit = newRate) }
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
                emitEffect(UserDetailMviModel.Effect.OpenUrl(url))
            }
        }
    }
}
