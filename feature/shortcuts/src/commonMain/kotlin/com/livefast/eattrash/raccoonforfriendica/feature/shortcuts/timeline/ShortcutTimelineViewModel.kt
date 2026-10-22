package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
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

@AssistedInject
class ShortcutTimelineViewModel(
    @Assisted private val name: String,
    private val paginationManager: TimelinePaginationManager,
    private val identityRepository: IdentityRepository,
    private val postRepository: PostRepository,
    private val settingsRepository: SettingsRepository,
    private val searchRepository: SearchRepository,
    private val hapticFeedback: HapticFeedback,
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
    MviDelegate<ShortcutTimelineMvi.Intent, ShortcutTimelineMvi.State, ShortcutTimelineMvi.Effect>
    by DefaultMviDelegate(initialState = ShortcutTimelineMvi.State()),
    ShortcutTimelineMvi {

    init {
        viewModelScope.launch {
            updateState { it.copy(nodeName = name) }

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

            if (uiState.value.initial) {
                refresh(initial = true, forceRefresh = true)
            }
        }
    }

    override fun reduce(intent: ShortcutTimelineMvi.Intent) {
        when (intent) {
            ShortcutTimelineMvi.Intent.Refresh -> handleRefresh()

            ShortcutTimelineMvi.Intent.LoadNextPage -> handleLoadNextPage()

            is ShortcutTimelineMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is ShortcutTimelineMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is ShortcutTimelineMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is ShortcutTimelineMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            is ShortcutTimelineMvi.Intent.SubmitPollVote -> handleSubmitPollVote(
                post = intent.post,
                choices = intent.choices,
            )

            is ShortcutTimelineMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is ShortcutTimelineMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is ShortcutTimelineMvi.Intent.WillOpenDetail -> handleWillOpenDetail(intent.post)

            is ShortcutTimelineMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
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
            emitEffect(ShortcutTimelineMvi.Effect.OpenDetail(post))
        }
    }

    private suspend fun refresh(initial: Boolean = false, forceRefresh: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val settings = settingsRepository.current.value ?: SettingsModel()
        paginationManager.reset(
            TimelinePaginationSpecification.Feed(
                timelineType = TimelineType.Foreign(name),
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
                emitEffect(ShortcutTimelineMvi.Effect.BackToTop)
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
                        post.id == postId -> post.let(block)

                        post.reblog?.id == postId ->
                            post.copy(reblog = post.reblog?.let(block))

                        else -> post
                    }
                },
            )
        }
    }

    private suspend fun resolveToLocal(post: PostModel): PostModel? = post.url?.let { url ->
        val results =
            searchRepository.search(
                query = url,
                resolve = true,
                type = SearchResultType.Posts,
            )
        val result = results?.firstOrNull() as? ExploreItemModel.Post
        result?.post
    }

    private fun handleToggleReblog(post: PostModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updatePostInState(post.id) {
                it.copy(reblogLoading = true)
            }
            val newPost =
                resolveToLocal(post)?.let { le ->
                    if (le.reblogged) {
                        postRepository.unreblog(le.id)
                    } else {
                        postRepository.reblog(le.id)
                    }
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
                    it.copy(reblogLoading = false)
                }
                emitEffect(ShortcutTimelineMvi.Effect.Failure)
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

            val localPost =
                resolveToLocal(post)
                    ?.let { le ->
                        toggleFavorite(le)
                    }?.also { le ->
                        notificationCenter.send(PostUpdatedEvent(post = le))
                    }
            if (localPost != null) {
                updatePostInState(post.id) {
                    it.copy(
                        favorite = localPost.favorite,
                        favoriteCount = localPost.favoriteCount,
                        disliked = localPost.disliked,
                        dislikesCount = localPost.dislikesCount,
                        favoriteLoading = false,
                    )
                }
            } else {
                updatePostInState(post.id) {
                    it.copy(favoriteLoading = false)
                }
                emitEffect(ShortcutTimelineMvi.Effect.Failure)
            }
        }
    }

    private fun handleToggleDislike(post: PostModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updatePostInState(post.id) {
                it.copy(dislikeLoading = true)
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
                it.copy(bookmarkLoading = true)
            }
            val newPost =
                resolveToLocal(post)?.let { le ->
                    if (le.bookmarked) {
                        postRepository.unbookmark(le.id)
                    } else {
                        postRepository.bookmark(le.id)
                    }
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
                    it.copy(bookmarkLoading = false)
                }
                emitEffect(ShortcutTimelineMvi.Effect.Failure)
            }
        }
    }

    private fun handleSubmitPollVote(post: PostModel, choices: List<Int>) {
        val poll = post.poll ?: return
        viewModelScope.launch {
            updatePostInState(post.id) { it.copy(poll = poll.copy(loading = true)) }
            val newPoll =
                resolveToLocal(post)?.poll?.let { poll ->
                    postRepository.submitPoll(
                        pollId = poll.id,
                        choices = choices,
                    )
                }
            if (newPoll != null) {
                updatePostInState(post.id) {
                    it.copy(poll = newPoll).also { post ->
                        notificationCenter.send(PostUpdatedEvent(post))
                    }
                }
            } else {
                updatePostInState(post.id) { it.copy(poll = poll.copy(loading = false)) }
                emitEffect(ShortcutTimelineMvi.Effect.PollVoteFailure)
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
                emitEffect(ShortcutTimelineMvi.Effect.TriggerCopy(text))
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

    private fun handleOpenInBrowser(post: PostModel) {
        viewModelScope.launch {
            val url = getInnerUrl(post)
            if (url != null) {
                emitEffect(ShortcutTimelineMvi.Effect.OpenUrl(url))
            }
        }
    }

    companion object {
        fun getExtras(name: String) = CreationExtras {
            this[KEY_NAME] = name
        }
    }
}

private val KEY_NAME = CreationExtras.Key<String>()

@AssistedFactory
@ViewModelAssistedFactoryKey(ShortcutTimelineViewModel::class)
@ContributesIntoMap(AppScope::class)
interface ShortcutTimelineViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ShortcutTimelineViewModel =
        create(extras[KEY_NAME] ?: error("name argument not found"))

    fun create(@Assisted name: String): ShortcutTimelineViewModel
}
