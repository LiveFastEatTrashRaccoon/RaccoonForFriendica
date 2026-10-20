package com.livefast.eattrash.raccoonforfriendica.feature.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostCreatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
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
import kotlin.time.Duration

@AssistedInject
class PostDetailViewModel(
    @Assisted private val args: PostDetailViewModelArgs,
    private val postRepository: PostRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val blurHashRepository: BlurHashRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val hapticFeedback: HapticFeedback,
    private val postCache: LocalItemCache<PostModel>,
    private val imagePreloadManager: ImagePreloadManager,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val postActionHelper: PostActionHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val newPost: ToggleDislikeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val toggleTranslation: ToggleTranslationUseCase,
    private val populateThread: PopulateThreadUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<PostDetailMvi.Intent, PostDetailMvi.State, PostDetailMvi.Effect>
    by DefaultMviDelegate(initialState = PostDetailMvi.State()),
    PostDetailMvi {

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
            notificationCenter
                .subscribe(PostDeletedEvent::class)
                .onEach { event ->
                    removePostFromState(event.id)
                }.launchIn(this)
            notificationCenter
                .subscribe(PostCreatedEvent::class)
                .onEach { event ->
                    addPostInState(event.post)
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

    override fun onCleared() {
        super.onCleared()
        if (args.swipeNavigationEnabled) {
            timelineNavigationManager.pop()
        }
    }

    override fun reduce(intent: PostDetailMvi.Intent) {
        when (intent) {
            PostDetailMvi.Intent.Refresh -> handleRefresh()

            is PostDetailMvi.Intent.LoadMoreReplies -> handleLoadMoreReplies(intent.post)

            is PostDetailMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is PostDetailMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is PostDetailMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is PostDetailMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            is PostDetailMvi.Intent.Delete -> handleDelete(intent.postId)

            is PostDetailMvi.Intent.MuteUser -> handleMuteUser(
                postId = intent.postId,
                userId = intent.postId,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            is PostDetailMvi.Intent.BlockUser -> handleBlockUser(postId = intent.postId, userId = intent.postId)

            is PostDetailMvi.Intent.TogglePin -> handleTogglePin(intent.post)

            is PostDetailMvi.Intent.SubmitPollVote -> handleSubmitPollVote(intent.post, intent.choices)

            is PostDetailMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is PostDetailMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is PostDetailMvi.Intent.ChangeNavigationIndex -> handleChangeNavigationIndex(intent.index)

            is PostDetailMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is PostDetailMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            refresh()
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
            val currentPost = postCache.get(args.id)
            val posts =
                if (args.swipeNavigationEnabled) {
                    timelineNavigationManager.currentList.map { post -> listOf(post) }
                } else {
                    currentPost?.let { listOf(listOf(it)) } ?: listOf(emptyList())
                }
            val initialIndex =
                posts.indexOfFirst { list ->
                    list.any { post -> post.original.id == currentPost?.original?.id }
                }
            updateState {
                it.copy(
                    mainPost = currentPost,
                    currentIndex = initialIndex,
                    posts =
                    if (initial) {
                        posts
                    } else {
                        it.posts
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
            val currentPostList = currentState.posts.getOrNull(currentState.currentIndex).orEmpty()
            val index = currentPostList.indexOf(currentState.mainPost).takeIf { it > 0 } ?: 0
            emitEffect(PostDetailMvi.Effect.ScrollToItem(index))
        }
    }

    private suspend fun loadContext() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val mainPost = uiState.value.mainPost
        val maxDepth = settingsRepository.current.value?.replyDepth ?: 1
        val context =
            mainPost?.let { post ->
                postRepository.getContext(post.id)
            }
        val root = context?.ancestors?.firstOrNull() ?: mainPost ?: return
        val currentPostList =
            populateThread(post = root, maxDepth = maxDepth)
                .let { list ->
                    when {
                        mainPost != null &&
                            list.none { post ->
                                post.id == mainPost.id || post.original.id == mainPost.original.id
                            } -> {
                            val additionalList =
                                reconstructAncestors(
                                    post = mainPost,
                                    rootId = root.id,
                                )
                            list + additionalList.drop(1)
                        }

                        else -> list
                    }
                }.map { post ->
                    post.let {
                        with(emojiHelper) { it.withEmojisIfMissing() }
                    }.let {
                        with(replyHelper) { it.withInReplyToIfMissing() }
                    }.let {
                        with(postActionHelper) { it.withActions() }
                    }
                }.distinctBy { post -> post.safeKey }
        currentPostList.preloadImages()
        updateState {
            it.copy(
                posts =
                it.posts.mapIndexed { idx, postList ->
                    if (idx == it.currentIndex) {
                        currentPostList
                    } else {
                        postList
                    }
                },
                refreshing = false,
                initial = false,
                loading = false,
            )
        }
    }

    private suspend fun reconstructAncestors(post: PostModel, rootId: String): List<PostModel> {
        suspend fun buildAncestorsRec(post: PostModel, list: List<PostModel>): List<PostModel> {
            val parentId = post.parentId
            if (parentId.isNullOrEmpty() || post.id == rootId) {
                return list
            }
            val parent = postRepository.getById(parentId) ?: return list
            return buildAncestorsRec(parent, listOf(parent) + list)
        }

        val res = buildAncestorsRec(post, listOf(post))
        return res
            .mapIndexed { idx, post ->
                val hasMoreReplies =
                    when {
                        idx == res.lastIndex -> post.replyCount > 0
                        else -> post.replyCount > 1
                    }
                post.copy(
                    depth = idx,
                    loadMoreButtonVisible = hasMoreReplies,
                )
            }
    }

    private fun handleLoadMoreReplies(post: PostModel) {
        if (post.loadMoreButtonLoading) {
            return
        }

        viewModelScope.launch {
            val currentState = uiState.value
            val currentReplies = currentState.posts.getOrNull(currentState.currentIndex).orEmpty()
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
                val index = currentReplies.indexOfFirst { p -> p.id == post.id }
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
                        posts =
                        it.posts.mapIndexed { idx, list ->
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
                it.posts.mapIndexed { idx, postList ->
                    if (idx == uiState.value.currentIndex) {
                        postList.map { post ->
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
                        }
                    } else {
                        postList
                    }
                },
            )
        }
    }

    private suspend fun removePostFromState(postId: String) {
        updateState {
            it.copy(
                posts =
                it.posts.mapIndexed { idx, postList ->
                    if (idx == it.currentIndex) {
                        postList.filter { post -> post.id != postId && post.reblog?.id != postId }
                    } else {
                        postList
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
                newPost(post)?.also { post ->
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
                emitEffect(PostDetailMvi.Effect.PollVoteFailure)
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
                emitEffect(PostDetailMvi.Effect.TriggerCopy(text))
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
        if (!args.swipeNavigationEnabled) {
            return
        }

        viewModelScope.launch {
            updateState {
                it.copy(
                    currentIndex = newIndex,
                    mainPost = it.posts.getOrNull(newIndex)?.firstOrNull(),
                )
            }
            val currentState = uiState.value
            val hasReplies = (currentState.mainPost?.replyCount ?: 0) > 0
            val currentPosts = currentState.posts.getOrNull(newIndex) ?: emptyList()
            if (currentPosts.isEmpty() || (currentPosts.size == 1 && hasReplies)) {
                loadContext()
            }

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
            val currentPosts = it.posts
            val newPosts =
                timelineNavigationManager.currentList
                    .map { post ->
                        listOf(post)
                    }.drop(currentPosts.size)
            it.copy(
                posts = currentPosts + newPosts,
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

    private suspend fun addPostInState(post: PostModel) {
        val posts = uiState.value.let { it.posts.getOrNull(it.currentIndex) }.orEmpty()
        val parentIndex =
            posts
                .indexOfFirst { p ->
                    p.id == post.parentId || p.original.id == post.parentId
                }.takeIf { it >= 0 } ?: return
        val newPosts =
            buildList {
                // insert everything included the parent
                addAll(posts.subList(0, parentIndex + 1))
                var indexOfFirstNotDescendant = -1
                val familyMembers = mutableListOf(posts[parentIndex].id)
                for (i in parentIndex + 1 until posts.size) {
                    val e = posts[i]
                    if (e.original.parentId in familyMembers) {
                        familyMembers += e.original.id
                        add(e)
                    } else {
                        indexOfFirstNotDescendant = i
                        break
                    }
                }
                // insert the new post with its depth
                val depth = posts[parentIndex].depth + 1
                add(post.copy(depth = depth))
                // add all the rest
                if (indexOfFirstNotDescendant >= 0 && indexOfFirstNotDescendant < posts.size) {
                    addAll(posts.subList(indexOfFirstNotDescendant, posts.size))
                }
            }
        updateState {
            it.copy(
                posts =
                it.posts.mapIndexed { idx, postList ->
                    if (idx == it.currentIndex) {
                        newPosts
                    } else {
                        postList
                    }
                },
            )
        }
    }

    private fun handleOpenInBrowser(post: PostModel) {
        viewModelScope.launch {
            val url = getInnerUrl(post)
            if (url != null) {
                emitEffect(PostDetailMvi.Effect.OpenUrl(url))
            }
        }
    }

    companion object {
        fun getExtras(args: PostDetailViewModelArgs) = CreationExtras {
            this[KEY_ARGS] = args
        }
    }
}

private val KEY_ARGS = CreationExtras.Key<PostDetailViewModelArgs>()

data class PostDetailViewModelArgs(val id: String, val swipeNavigationEnabled: Boolean)

@AssistedFactory
@ViewModelAssistedFactoryKey(PostDetailViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface PostDetailViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): PostDetailViewModel =
        create(extras[KEY_ARGS] ?: error("ViewModel creation args not found"))

    fun create(@Assisted args: PostDetailViewModelArgs): PostDetailViewModel
}
