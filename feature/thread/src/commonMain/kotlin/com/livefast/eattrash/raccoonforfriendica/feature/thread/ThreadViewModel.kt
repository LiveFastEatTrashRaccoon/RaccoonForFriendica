package com.livefast.eattrash.raccoonforfriendica.feature.thread

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
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.PostActionHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.PopulateThreadUseCase
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration

@AssistedInject
class ThreadViewModel(
    @Assisted private val postId: String,
    @Assisted private val swipeNavigationEnabled: Boolean,
    private val postRepository: PostRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val hapticFeedback: HapticFeedback,
    private val postCache: LocalItemCache<PostModel>,
    private val imagePreloadManager: ImagePreloadManager,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val postActionHelper: PostActionHelper,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val populateThread: PopulateThreadUseCase,
    private val toggleDislike: ToggleDislikeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val toggleTranslation: ToggleTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<ThreadMvi.Intent, ThreadMvi.State, ThreadMvi.Effect>
    by DefaultMviDelegate(initialState = ThreadMvi.State()),
    ThreadMvi {

    private val currentMainPost: PostModel?
        get() {
            val currentState = uiState.value
            return currentState.mainPosts.getOrNull(currentState.currentIndex)
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

    override fun reduce(intent: ThreadMvi.Intent) {
        when (intent) {
            ThreadMvi.Intent.Refresh -> handleRefresh()

            is ThreadMvi.Intent.LoadMoreReplies -> handleLoadMoreReplies(intent.post)

            is ThreadMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            is ThreadMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is ThreadMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is ThreadMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is ThreadMvi.Intent.Delete -> handleDelete(intent.postId)

            is ThreadMvi.Intent.MuteUser -> handleMuteUser(
                userId = intent.userId,
                postId = intent.postId,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            is ThreadMvi.Intent.BlockUser -> handleBlockUser(userId = intent.userId, postId = intent.postId)

            is ThreadMvi.Intent.SubmitPollVote -> handleSubmitPollVote(intent.post, intent.choices)

            is ThreadMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is ThreadMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is ThreadMvi.Intent.ChangeNavigationIndex -> handleChangeNavigationIndex(intent.index)

            is ThreadMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is ThreadMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            refresh()
        }
    }

    private fun handleLoadMoreReplies(post: PostModel) {
        viewModelScope.launch {
            loadMoreReplies(post)
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
            val currentPost = postCache.get(postId)
            val mainPosts =
                if (swipeNavigationEnabled) {
                    timelineNavigationManager.currentList
                } else {
                    currentPost?.let { listOf(it) } ?: emptyList()
                }
            val initialIndex = mainPosts.indexOfFirst { it.id == postId }.coerceAtLeast(0)
            val replies: List<List<PostModel>> = mainPosts.map { emptyList() }
            check(mainPosts.size == replies.size) { "Unable to create navigation set " }
            updateState {
                it.copy(
                    currentIndex = initialIndex,
                    mainPosts = mainPosts,
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
        val mainPost = currentMainPost?.original ?: return
        val result = populateThread(post = mainPost).map { post ->
            post.let {
                with(emojiHelper) { it.withEmojisIfMissing() }
            }.let {
                with(replyHelper) { it.withInReplyToIfMissing() }
            }.let {
                with(postActionHelper) { it.withActions() }
            }
        }
        val replies = result.filter { it.id != mainPost.id }
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

    private suspend fun loadMoreReplies(post: PostModel) {
        if (post.loadMoreButtonLoading) {
            return
        }
        val currentState = uiState.value
        val currentReplies = currentState.replies[currentState.currentIndex]
        updatePostInState(post.id) { it.copy(loadMoreButtonLoading = true) }
        val result = populateThread(post = post.original).map { post ->
            post.let {
                with(emojiHelper) { it.withEmojisIfMissing() }
            }.let {
                with(replyHelper) { it.withInReplyToIfMissing() }
            }.let {
                with(postActionHelper) { it.withActions() }
            }
        }
        val newReplies = result.filter { e1 -> currentReplies.none { e2 -> e1.id == e2.id } }
        if (newReplies.isEmpty()) {
            // abort and disable load more button
            updatePostInState(post.id) {
                it.copy(
                    loadMoreButtonVisible = false,
                    loadMoreButtonLoading = false,
                )
            }
        } else {
            newReplies.preloadImages()
            val index = currentReplies.indexOfFirst { post -> post.id == post.id }
            val replies =
                buildList {
                    if (index > 0) {
                        addAll(currentReplies.subList(fromIndex = 0, toIndex = index))
                    }
                    add(
                        post.copy(
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
        val mainPost = currentMainPost ?: return
        when (postId) {
            mainPost.id -> {
                updateState {
                    it.copy(
                        mainPosts =
                        it.mainPosts.mapIndexed { idx, post ->
                            if (idx == it.currentIndex) {
                                mainPost.let(block)
                            } else {
                                post
                            }
                        },
                    )
                }
            }

            mainPost.reblog?.id -> {
                updateState {
                    it.copy(
                        mainPosts =
                        it.mainPosts.mapIndexed { idx, post ->
                            if (idx == it.currentIndex) {
                                mainPost.copy(reblog = mainPost.reblog?.let(block))
                            } else {
                                post
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
                                list.map { post ->
                                    when {
                                        post.id == postId -> post.let(block)

                                        post.reblog?.id == postId ->
                                            post.copy(reblog = post.reblog?.let(block))

                                        else -> post
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

    private suspend fun removePostFromState(postId: String) {
        updateState {
            it.copy(
                replies =
                it.replies.mapIndexed { idx, list ->
                    if (idx == it.currentIndex) {
                        list.filter { post -> post.id != postId && post.reblog?.id != postId }
                    } else {
                        list
                    }
                },
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
                updatePostInState(post.id) { it.copy(poll = newPoll) }
            } else {
                updatePostInState(post.id) {
                    it.copy(poll = poll.copy(loading = false)).also { post ->
                        notificationCenter.send(PostUpdatedEvent(post))
                    }
                }
                emitEffect(ThreadMvi.Effect.PollVoteFailure)
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
                emitEffect(ThreadMvi.Effect.TriggerCopy(text))
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

    private fun handleChangeNavigationIndex(newIndex: Int) {
        if (!swipeNavigationEnabled) {
            return
        }
        viewModelScope.launch {
            updateState {
                it.copy(
                    currentIndex = newIndex,
                )
            }
            val currentState = uiState.value
            val mainPost = currentMainPost
            val replies = currentState.replies.getOrNull(newIndex).orEmpty()
            val hasReplies = (mainPost?.original?.replyCount ?: 0) > 0
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
        if (!swipeNavigationEnabled) {
            return
        }
        timelineNavigationManager.loadNextPage()
        updateState {
            val currentPosts = it.mainPosts
            val newPosts = timelineNavigationManager.currentList.drop(currentPosts.size)
            val mainPosts = currentPosts + newPosts
            val replies =
                buildList {
                    addAll(it.replies)
                    val sizeDiff = abs(mainPosts.size - it.replies.size)
                    repeat(sizeDiff) {
                        add(emptyList())
                    }
                }

            check(mainPosts.size == replies.size) { "Unable to create navigation post set" }
            it.copy(
                mainPosts = mainPosts,
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

    private fun handleOpenInBrowser(post: PostModel) {
        viewModelScope.launch {
            val url = getInnerUrl(post)
            if (url != null) {
                emitEffect(ThreadMvi.Effect.OpenUrl(url))
            }
        }
    }

    companion object {
        fun getExtras(postId: String, swipeNavigationEnabled: Boolean) = CreationExtras {
            this[KEY_POST_ID] = postId
            this[KEY_SWIPE_NAV_ENABLED] = swipeNavigationEnabled
        }
    }
}

private val KEY_POST_ID = CreationExtras.Key<String>()
private val KEY_SWIPE_NAV_ENABLED = CreationExtras.Key<Boolean>()

@AssistedFactory
@ViewModelAssistedFactoryKey(ThreadViewModel::class)
@ContributesIntoMap(AppScope::class)
interface ThreadViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ThreadViewModel = create(
        extras[KEY_POST_ID] ?: error("postId argument not found"),
        extras[KEY_SWIPE_NAV_ENABLED] ?: error("swipeNavigationEnabled argument not found"),
    )

    fun create(@Assisted postId: String, @Assisted swipeNavigationEnabled: Boolean): ThreadViewModel
}
