package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.PostActionHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
@OptIn(FlowPreview::class)
class MyAccountViewModel(
    private val userRepository: UserRepository,
    private val identityRepository: IdentityRepository,
    private val paginationManager: TimelinePaginationManager,
    private val postRepository: PostRepository,
    private val settingsRepository: SettingsRepository,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val postActionHelper: PostActionHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val logout: LogoutUseCase,
    private val toggleDislike: ToggleDislikeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<MyAccountMvi.Intent, MyAccountMvi.State, MyAccountMvi.Effect>
    by DefaultMviDelegate(initialState = MyAccountMvi.State()),
    MyAccountMvi {

    init {
        viewModelScope.launch {
            identityRepository
                .currentUser
                .debounce(750.milliseconds)
                .onEach { user ->
                    val currentUser =
                        user?.let {
                            with(emojiHelper) { it.withEmojisIfMissing() }
                        }
                    viewModelScope.launch {
                        loadData(currentUser)
                    }
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
        }
    }

    private suspend fun loadData(currentUser: UserModel?) {
        if (currentUser != null) {
            updateState { it.copy(user = currentUser) }
            val initialValues = postRepository.getCachedByUser().map { post ->
                post.let {
                    with(emojiHelper) { it.withEmojisIfMissing() }
                }.let {
                    with(replyHelper) { it.withInReplyToIfMissing() }
                }.let {
                    with(postActionHelper) { it.withActions() }
                }
            }
            paginationManager.restoreHistory(initialValues)
            if (initialValues.isNotEmpty()) {
                updateState {
                    it.copy(
                        initial = false,
                        posts = initialValues,
                    )
                }
                paginationManager.reset(
                    TimelinePaginationSpecification.User(
                        userId = currentUser.id,
                        excludeReplies = true,
                        includeNsfw = settingsRepository.current.value?.includeNsfw == true,
                        enableCache = true,
                    ),
                )
            } else {
                refresh(initial = true)
            }
        } else {
            // recover screen
            updateState {
                it.copy(initial = false)
            }
        }
    }

    override fun reduce(intent: MyAccountMvi.Intent) {
        when (intent) {
            is MyAccountMvi.Intent.ChangeSection -> handleChangeSection(intent.section)
            MyAccountMvi.Intent.LoadNextPage -> handleLoadNextPage()
            MyAccountMvi.Intent.Refresh -> handleRefresh()
            is MyAccountMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)
            is MyAccountMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)
            is MyAccountMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)
            is MyAccountMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)
            is MyAccountMvi.Intent.Delete -> handleDelete(intent.postId)
            is MyAccountMvi.Intent.TogglePin -> handleTogglePin(intent.post)
            is MyAccountMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)
            is MyAccountMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
            MyAccountMvi.Intent.Logout -> handleLogout()
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            launch {
                val currentUser = userRepository.getCurrent(refresh = true)
                updateState { it.copy(user = currentUser) }
            }
            launch {
                refresh()
            }
        }
    }

    private fun handleLoadNextPage() {
        viewModelScope.launch {
            loadNextPage()
        }
    }

    private fun handleChangeSection(section: UserSection) {
        viewModelScope.launch {
            if (uiState.value.loading) {
                return@launch
            }
            updateState { it.copy(section = section) }
            emitEffect(MyAccountMvi.Effect.BackToTop)
            refresh(initial = true)
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            logout()
        }
    }

    private suspend fun refresh(initial: Boolean = false, forceRefresh: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val currentState = uiState.value
        val accountId =
            identityRepository.currentUser.value?.id ?: ""
        paginationManager.reset(
            TimelinePaginationSpecification.User(
                userId = accountId,
                excludeReplies = currentState.section == UserSection.Posts,
                onlyMedia = currentState.section == UserSection.Media,
                pinned = currentState.section == UserSection.Pinned,
                includeNsfw = settingsRepository.current.value?.includeNsfw == true,
                enableCache = currentState.section == UserSection.Posts,
                refresh = forceRefresh || !initial,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
            val posts = paginationManager.loadNextPage().distinctBy { it.safeKey }
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
            } else {
                emitEffect(MyAccountMvi.Effect.Failure)
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
                if (uiState.value.section == UserSection.Pinned && !newPost.pinned) {
                    removePostFromState(post.id)
                } else {
                    updatePostInState(post.id) {
                        it.copy(
                            pinned = newPost.pinned,
                        )
                    }
                }
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
                emitEffect(MyAccountMvi.Effect.TriggerCopy(text))
            }
        }
    }

    private fun handleOpenInBrowser(post: PostModel) {
        viewModelScope.launch {
            val url = getInnerUrl(post)
            if (url != null) {
                emitEffect(MyAccountMvi.Effect.OpenUrl(url))
            }
        }
    }
}
