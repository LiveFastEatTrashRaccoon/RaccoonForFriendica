package com.livefast.eattrash.raccoonforfriendica.feature.userdetail.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleFavoriteUseCase
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

@AssistedInject
class ForumListViewModel(
    @Assisted private val id: String,
    private val userRepository: UserRepository,
    private val paginationManager: TimelinePaginationManager,
    private val postRepository: PostRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val hapticFeedback: HapticFeedback,
    private val userCache: LocalItemCache<UserModel>,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val toggleDislike: ToggleDislikeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val toggleTranslation: ToggleTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<ForumListMvi.Intent, ForumListMvi.State, ForumListMvi.Effect>
    by DefaultMviDelegate(initialState = ForumListMvi.State()),
    ForumListMvi {

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
                .subscribe(PostUpdatedEvent::class)
                .onEach { event ->
                    updatePostInState(event.post.id) { event.post }
                }.launchIn(this)

            apiConfigurationRepository.node
                .onEach { node ->
                    updateState { it.copy(currentNode = node) }
                }.launchIn(this)

            if (uiState.value.initial) {
                loadUser()
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ForumListMvi.Intent) {
        when (intent) {
            ForumListMvi.Intent.LoadNextPage -> handleLoadNextPage()

            ForumListMvi.Intent.Refresh -> handleRefresh()

            is ForumListMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is ForumListMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is ForumListMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is ForumListMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            is ForumListMvi.Intent.Delete -> handleDelete(intent.postId)

            is ForumListMvi.Intent.MuteUser -> handleMuteUser(
                userId = intent.userId,
                postId = intent.postId,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            is ForumListMvi.Intent.BlockUser -> handleBlockUser(userId = intent.userId, postId = intent.postId)

            ForumListMvi.Intent.Follow -> handleFollow()

            ForumListMvi.Intent.Unfollow -> handleUnfollow()

            ForumListMvi.Intent.DisableNotifications -> handleDisableNotifications()

            ForumListMvi.Intent.EnableNotifications -> handleEnableNotifications()

            is ForumListMvi.Intent.SubmitPollVote -> handleSubmitPollVote(intent.post, intent.choices)

            is ForumListMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is ForumListMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is ForumListMvi.Intent.WillOpenDetail -> handleWillOpenDetail(intent.post)

            is ForumListMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is ForumListMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
        }
    }

    private fun handleLoadNextPage() {
        viewModelScope.launch {
            loadNextPage()
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            refresh()
        }
    }

    private fun handleDisableNotifications() {
        toggleNotifications(enabled = false)
    }

    private fun handleEnableNotifications() {
        toggleNotifications(enabled = true)
    }

    private fun handleWillOpenDetail(post: PostModel) {
        viewModelScope.launch {
            val state = paginationManager.extractState()
            timelineNavigationManager.push(state)
            emitEffect(ForumListMvi.Effect.OpenDetail(post))
        }
    }

    private suspend fun loadUser() {
        val account = userCache.get(id)
        updateState {
            it.copy(user = account)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            TimelinePaginationSpecification.Forum(
                userId = id,
                includeNsfw = settingsRepository.current.value?.includeNsfw == true,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
            val posts =
                run {
                    // must wait until some content is fetched (due to replies being filtered out)
                    var res: List<PostModel> = emptyList()
                    while (paginationManager.canFetchMore && res.isEmpty()) {
                        res = paginationManager.loadNextPage()
                    }
                    res
                }
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
                emitEffect(ForumListMvi.Effect.PollVoteFailure)
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
                emitEffect(ForumListMvi.Effect.TriggerCopy(text))
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
                emitEffect(ForumListMvi.Effect.OpenUrl(url))
            }
        }
    }

    private fun handleFollow() {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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

    private fun handleUnfollow() {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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

    private fun toggleNotifications(enabled: Boolean) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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

    companion object {
        fun getExtras(id: String) = CreationExtras {
            this[KEY_GROUP_ID] = id
        }
    }
}

private val KEY_GROUP_ID = CreationExtras.Key<String>()

@AssistedFactory
@ViewModelAssistedFactoryKey(ForumListViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface ForumListViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ForumListViewModel =
        create(extras[KEY_GROUP_ID] ?: error("groupId argument not found"))

    fun create(@Assisted id: String): ForumListViewModel
}
