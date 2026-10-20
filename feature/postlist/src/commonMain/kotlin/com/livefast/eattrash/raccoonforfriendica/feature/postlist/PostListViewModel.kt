package com.livefast.eattrash.raccoonforfriendica.feature.postlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification.Bookmarks
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification.Favorites
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification.Quotes
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
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
class PostListViewModel(
    @Assisted private val args: PostListViewModelArgs,
    private val paginationManager: TimelinePaginationManager,
    private val postRepository: PostRepository,
    private val settingsRepository: SettingsRepository,
    private val identityRepository: IdentityRepository,
    private val userRepository: UserRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val blurHashRepository: BlurHashRepository,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val toggleDislike: ToggleDislikeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val toggleTranslation: ToggleTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<PostListMvi.Intent, PostListMvi.State, PostListMvi.Effect>
    by DefaultMviDelegate(initialState = PostListMvi.State()),
    PostListMvi {

    init {
        viewModelScope.launch {
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
                .subscribe(PostUpdatedEvent::class)
                .onEach { event ->
                    updatePostInState(event.post.id) { event.post }
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

    override fun reduce(intent: PostListMvi.Intent) {
        when (intent) {
            PostListMvi.Intent.Refresh -> handleRefresh()

            PostListMvi.Intent.LoadNextPage -> handleLoadNextPage()

            is PostListMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is PostListMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is PostListMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is PostListMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            is PostListMvi.Intent.Delete -> handleDelete(intent.postId)

            is PostListMvi.Intent.MuteUser -> handleMuteUser(
                userId = intent.userId,
                postId = intent.postId,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            is PostListMvi.Intent.BlockUser -> handleBlockUser(userId = intent.userId, postId = intent.postId)

            is PostListMvi.Intent.TogglePin -> handleTogglePin(intent.post)

            is PostListMvi.Intent.SubmitPollVote -> handleSubmitPollVote(intent.post, intent.choices)

            is PostListMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is PostListMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is PostListMvi.Intent.WillOpenDetail -> handleWillOpenDetail(intent.post)

            is PostListMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is PostListMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
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

    private fun handleWillOpenDetail(post: PostModel) {
        viewModelScope.launch {
            val state = paginationManager.extractState()
            timelineNavigationManager.push(state)
            emitEffect(PostListMvi.Effect.OpenDetail(post))
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            when (args.type) {
                PostListType.Bookmarks ->
                    Bookmarks(includeNsfw = settingsRepository.current.value?.includeNsfw == true)

                PostListType.Favorites ->
                    Favorites(includeNsfw = settingsRepository.current.value?.includeNsfw == true)

                is PostListType.Quoting ->
                    Quotes(id = args.type.postId, otherInstance = args.type.otherInstance)
            },
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
            val posts = paginationManager.loadNextPage()
            posts.preloadImages()
            val wasRefreshing = uiState.value.refreshing
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
                emitEffect(PostListMvi.Effect.BackToTop)
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
                if (!newPost.bookmarked && args.type == PostListType.Bookmarks) {
                    removePostFromState(post.id)
                } else {
                    updatePostInState(post.id) {
                        it
                            .copy(
                                bookmarked = newPost.bookmarked,
                                bookmarkLoading = false,
                            ).also { post ->
                                notificationCenter.send(PostUpdatedEvent(post))
                            }
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
                emitEffect(PostListMvi.Effect.PollVoteFailure)
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
                emitEffect(PostListMvi.Effect.TriggerCopy(text))
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
                emitEffect(PostListMvi.Effect.OpenUrl(url))
            }
        }
    }

    companion object {
        fun getExtras(args: PostListViewModelArgs) = CreationExtras {
            this[KEY_ARGS] = args
        }
    }
}

private val KEY_ARGS = CreationExtras.Key<PostListViewModelArgs>()

data class PostListViewModelArgs(val type: PostListType)

@AssistedFactory
@ViewModelAssistedFactoryKey(PostListViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface PostListViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): PostListViewModel =
        create(extras[KEY_ARGS] ?: error("ViewModel creation args not found"))

    fun create(@Assisted args: PostListViewModelArgs): PostListViewModel
}
