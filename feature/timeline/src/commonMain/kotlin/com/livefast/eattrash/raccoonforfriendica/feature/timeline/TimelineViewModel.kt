package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
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
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleFavoriteUseCase
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
    private val postRepository: PostRepository,
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
    private val toggleDislike: ToggleDislikeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
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
                .subscribe(PostUpdatedEvent::class)
                .onEach { event ->
                    updatePostInState(event.post.id) { event.post }
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
            TimelineMvi.Intent.Refresh -> handleRefresh()

            TimelineMvi.Intent.LoadNextPage -> handleLoadNextPage()

            is TimelineMvi.Intent.ChangeType -> handleChangeType(intent.type)

            is TimelineMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is TimelineMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is TimelineMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            is TimelineMvi.Intent.Delete -> handleDelete(intent.postId)

            is TimelineMvi.Intent.MuteUser -> handleMuteUser(
                userId = intent.userId,
                postId = intent.postId,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            is TimelineMvi.Intent.BlockUser -> handleBlockUser(userId = intent.userId, postId = intent.postId)

            is TimelineMvi.Intent.TogglePin -> handleTogglePin(intent.post)

            is TimelineMvi.Intent.SubmitPollVote -> handleSubmitPollVote(post = intent.post, choices = intent.choices)

            is TimelineMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is TimelineMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is TimelineMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is TimelineMvi.Intent.WillOpenDetail -> handleWillOpenDetail(intent.post)

            is TimelineMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is TimelineMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            refresh()
        }
    }

    private fun handleLoadNextPage() {
        viewModelScope.launch {
            loadNextPage()
        }
    }

    private fun handleChangeType(type: TimelineType) {
        viewModelScope.launch {
            changeTimelineType(type)
        }
    }

    private fun handleWillOpenDetail(post: PostModel) {
        viewModelScope.launch {
            val state = paginationManager.extractState()
            timelineNavigationManager.push(state)
            emitEffect(TimelineMvi.Effect.OpenDetail(post))
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
            val posts = paginationManager.loadNextPage()
            posts.preloadImages()
            updateState {
                it.copy(
                    posts = posts,
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

    private suspend fun List<PostModel>.preloadImages() {
        flatMap { post ->
            post.original.urlsForPreload
        }.forEach { url ->
            imagePreloadManager.preload(url)
        }
        flatMap { post ->
            post.blurHashParamsForPreload
        }.forEach {
            blurHashRepository.preload(it)
        }
    }

    private suspend fun updatePostInState(postId: String, block: (PostModel) -> PostModel) {
        updateState {
            it.copy(
                posts =
                it.posts.map { post ->
                    when {
                        post.id == postId -> {
                            post.let(block)
                        }

                        post.reblog?.id == postId -> {
                            post.copy(reblog = post.reblog?.let(block))
                        }

                        else -> {
                            post
                        }
                    }
                },
            )
        }
    }

    private suspend fun removePostFromState(postId: String) {
        updateState {
            it.copy(
                posts = it.posts.filter { post -> post.id != postId && post.reblog?.id != postId },
            )
        }
    }

    private fun handleToggleReblog(post: PostModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updatePostInState(post.id) {
                it.copy(
                    reblogLoading = true,
                )
            }
            val newPost =
                if (post.reblogged) {
                    postRepository.unreblog(post.id)
                } else {
                    postRepository.reblog(post.id)
                }
            if (newPost != null) {
                updatePostInState(post.id) {
                    it
                        .copy(
                            reblogged = newPost.reblogged,
                            reblogCount = newPost.reblogCount,
                            reblogLoading = false,
                        ).also { post ->
                            notificationCenter.send(PostUpdatedEvent(post))
                        }
                }
            } else {
                updatePostInState(post.id) {
                    it.copy(
                        reblogLoading = false,
                    )
                }
            }
        }
    }

    private fun handleToggleFavorite(post: PostModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updatePostInState(post.id) {
                it.copy(
                    favoriteLoading = true,
                )
            }
            val newPost =
                toggleFavorite(post)?.also { post ->
                    notificationCenter.send(PostUpdatedEvent(post))
                }
            if (newPost != null) {
                updatePostInState(post.id) {
                    newPost
                }
            } else {
                updatePostInState(post.id) {
                    it.copy(
                        favoriteLoading = false,
                    )
                }
            }
        }
    }

    private fun handleToggleDislike(post: PostModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updatePostInState(post.id) {
                it.copy(
                    dislikeLoading = true,
                )
            }
            val newPost =
                toggleDislike(post)?.also { post ->
                    notificationCenter.send(PostUpdatedEvent(post))
                }
            if (newPost != null) {
                updatePostInState(post.id) {
                    newPost
                }
            } else {
                updatePostInState(post.id) {
                    it.copy(
                        dislikeLoading = false,
                    )
                }
            }
        }
    }

    private fun handleToggleBookmark(post: PostModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updatePostInState(post.id) {
                it.copy(
                    bookmarkLoading = true,
                )
            }
            val newPost =
                if (post.bookmarked) {
                    postRepository.unbookmark(post.id)
                } else {
                    postRepository.bookmark(post.id)
                }
            if (newPost != null) {
                updatePostInState(post.id) {
                    it
                        .copy(
                            bookmarked = newPost.bookmarked,
                            bookmarkLoading = false,
                        ).also { post ->
                            notificationCenter.send(PostUpdatedEvent(post))
                        }
                }
            } else {
                updatePostInState(post.id) {
                    it.copy(
                        bookmarkLoading = false,
                    )
                }
            }
        }
    }

    private fun handleDelete(postId: String) {
        viewModelScope.launch {
            val success = postRepository.delete(postId)
            if (success) {
                notificationCenter.send(PostDeletedEvent(postId))
                removePostFromState(postId)
            }
        }
    }

    private fun handleMuteUser(userId: String, postId: String, duration: Duration, disableNotifications: Boolean) {
        viewModelScope.launch {
            val res =
                userRepository.mute(
                    id = userId,
                    durationSeconds = if (duration.isInfinite()) 0 else duration.inWholeSeconds,
                    notifications = disableNotifications,
                )
            if (res != null) {
                removePostFromState(postId)
            }
        }
    }

    private fun handleBlockUser(userId: String, postId: String) {
        viewModelScope.launch {
            val res = userRepository.block(userId)
            if (res != null) {
                removePostFromState(postId)
            }
        }
    }

    private fun handleTogglePin(post: PostModel) {
        viewModelScope.launch {
            val newPost =
                if (post.pinned) {
                    postRepository.unpin(post.id)
                } else {
                    postRepository.pin(post.id)
                }
            if (newPost != null) {
                updatePostInState(post.id) {
                    it.copy(
                        pinned = newPost.pinned,
                    )
                }
            }
        }
    }

    private fun handleSubmitPollVote(post: PostModel, choices: List<Int>) {
        val poll = post.poll ?: return
        viewModelScope.launch {
            updatePostInState(post.id) { it.copy(poll = poll.copy(loading = true)) }
            val newPoll =
                postRepository.submitPoll(
                    pollId = poll.id,
                    choices = choices,
                )
            if (newPoll != null) {
                updatePostInState(post.id) {
                    it.copy(poll = newPoll).also { post ->
                        notificationCenter.send(PostUpdatedEvent(post))
                    }
                }
            } else {
                updatePostInState(post.id) { it.copy(poll = poll.copy(loading = false)) }
                emitEffect(TimelineMvi.Effect.PollVoteFailure)
            }
        }
    }

    private fun handleCopyToClipboard(post: PostModel) {
        viewModelScope.launch {
            val source = postRepository.getSource(post.id)
            if (source != null) {
                val text =
                    buildString {
                        if (!post.title.isNullOrBlank()) {
                            append(post.title)
                            append("\n")
                        }
                        append(source.content)
                    }
                emitEffect(TimelineMvi.Effect.TriggerCopy(text))
            }
        }
    }

    private fun handleToggleTranslation(post: PostModel) {
        val targetLang = uiState.value.lang ?: return
        if (post.translationLoading) {
            return
        }

        viewModelScope.launch {
            updatePostInState(post.id) { post.copy(translationLoading = true) }
            val newPost = toggleTranslation(post = post, targetLang = targetLang)
            updatePostInState(post.id) { newPost }
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

    private fun handleOpenInBrowser(post: PostModel) {
        viewModelScope.launch {
            val url = getInnerUrl(post)
            if (url != null) {
                emitEffect(TimelineMvi.Effect.OpenUrl(url))
            }
        }
    }
}
