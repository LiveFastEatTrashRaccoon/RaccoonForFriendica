package com.livefast.eattrash.raccoonforfriendica.feaure.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.SearchPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.SearchPaginationSpecification
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
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.SearchSection
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
@OptIn(FlowPreview::class)
class SearchViewModel(
    private val paginationManager: SearchPaginationManager,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val settingsRepository: SettingsRepository,
    private val identityRepository: IdentityRepository,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val toggleDislike: ToggleDislikeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val toggleTranslation: ToggleTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<SearchMvi.Intent, SearchMvi.State, SearchMvi.Effect>
    by DefaultMviDelegate(initialState = SearchMvi.State()),
    SearchMvi {

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

            uiState
                .map { it.query }
                .distinctUntilChanged()
                .drop(1)
                .debounce(1.seconds)
                .onEach {
                    refresh()
                }.launchIn(this)

            notificationCenter
                .subscribe(UserUpdatedEvent::class)
                .onEach { event ->
                    updateUserInState(event.user.id) { event.user }
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

    override fun reduce(intent: SearchMvi.Intent) {
        when (intent) {
            is SearchMvi.Intent.SetSearch -> handleSetSearch(intent.query)

            is SearchMvi.Intent.ChangeSection -> handleChangeSection(intent.section)

            SearchMvi.Intent.LoadNextPage -> handleLoadNextPage()

            SearchMvi.Intent.Refresh -> handleRefresh()

            is SearchMvi.Intent.Follow -> handleFollow(intent.userId)

            is SearchMvi.Intent.Unfollow -> handleUnfollow(intent.userId)

            is SearchMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            is SearchMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is SearchMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is SearchMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is SearchMvi.Intent.Delete -> handleDelete(intent.postId)

            is SearchMvi.Intent.MuteUser -> handleMuteUser(
                userId = intent.userId,
                postId = intent.postId,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            is SearchMvi.Intent.BlockUser -> handleBlockUser(
                userId = intent.userId,
                postId = intent.postId,
            )

            is SearchMvi.Intent.TogglePin -> handleTogglePin(intent.post)

            is SearchMvi.Intent.SubmitPollVote -> handleSubmitPollVote(intent.post, intent.choices)

            is SearchMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is SearchMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is SearchMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is SearchMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
        }
    }

    private fun handleSetSearch(query: String) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    earlyLoading = true,
                    query = query,
                )
            }
        }
    }

    private fun handleChangeSection(section: SearchSection) {
        viewModelScope.launch {
            if (uiState.value.loading) {
                return@launch
            }
            updateState { it.copy(section = section) }
            emitEffect(SearchMvi.Effect.BackToTop)
            refresh(initial = true)
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

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val query = uiState.value.query
        paginationManager.reset(
            when (uiState.value.section) {
                SearchSection.Hashtags -> SearchPaginationSpecification.Hashtags(query)

                SearchSection.Posts ->
                    SearchPaginationSpecification.Posts(
                        query = query,
                        includeNsfw = settingsRepository.current.value?.includeNsfw == true,
                    )

                SearchSection.Users -> SearchPaginationSpecification.Users(query)

                SearchSection.Groups -> SearchPaginationSpecification.Groups(query)
            },
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
            val items = paginationManager.loadNextPage()
            items.mapNotNull { (it as? ExploreItemModel.Post)?.post }.preloadImages()
            items.mapNotNull { (it as? ExploreItemModel.User)?.user }.preloadAvatars()
            updateState {
                it.copy(
                    items = items,
                    canFetchMore = paginationManager.canFetchMore,
                    loading = false,
                    earlyLoading = false,
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

    private fun List<UserModel>.preloadAvatars() {
        mapNotNull { user ->
            user.avatar?.takeIf { it.isNotEmpty() }
        }.forEach { url ->
            imagePreloadManager.preload(url)
        }
    }

    private suspend fun updateUserInState(userId: String, block: (UserModel) -> UserModel) {
        updateState {
            it.copy(
                items =
                it.items.map { item ->
                    if (item is ExploreItemModel.User && item.user.id == userId) {
                        item.copy(
                            user = item.user.let(block),
                        )
                    } else {
                        item
                    }
                },
            )
        }
    }

    private fun handleFollow(userId: String) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.items
                    .firstOrNull { it is ExploreItemModel.User && it.user.id == userId }
                    ?.let { (it as? ExploreItemModel.User)?.user }
            val newRelationship = userRepository.follow(userId)
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it
                    .copy(
                        relationshipStatus = newStatus,
                        notificationStatus = newNotificationStatus,
                        relationshipStatusPending = false,
                    ).also { user ->
                        notificationCenter.send(UserUpdatedEvent(user = user))
                    }
            }
        }
    }

    private fun handleUnfollow(userId: String) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.items
                    .firstOrNull { it is ExploreItemModel.User && it.user.id == userId }
                    ?.let { (it as? ExploreItemModel.User)?.user }
            val newRelationship = userRepository.unfollow(userId)
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it
                    .copy(
                        relationshipStatus = newStatus,
                        notificationStatus = newNotificationStatus,
                        relationshipStatusPending = false,
                    ).also { user ->
                        notificationCenter.send(UserUpdatedEvent(user = user))
                    }
            }
        }
    }

    private suspend fun updatePostInState(postId: String, block: (PostModel) -> PostModel) {
        updateState {
            it.copy(
                items =
                it.items.map { item ->
                    when {
                        item is ExploreItemModel.Post && item.post.id == postId -> {
                            item.copy(
                                post = item.post.let(block),
                            )
                        }

                        item is ExploreItemModel.Post && item.post.reblog?.id == postId -> {
                            item.copy(
                                post =
                                item.post.copy(
                                    reblog = item.post.reblog?.let(block),
                                ),
                            )
                        }

                        else -> {
                            item
                        }
                    }
                },
            )
        }
    }

    private suspend fun removePostFromState(postId: String) {
        updateState {
            it.copy(
                items =
                it.items.filter { item ->
                    when {
                        item is ExploreItemModel.Post && item.post.id == postId -> false
                        item is ExploreItemModel.Post && item.post.reblog?.id == postId -> false
                        else -> true
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
                emitEffect(SearchMvi.Effect.PollVoteFailure)
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
                emitEffect(SearchMvi.Effect.TriggerCopy(text))
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
                emitEffect(SearchMvi.Effect.OpenUrl(url))
            }
        }
    }
}
