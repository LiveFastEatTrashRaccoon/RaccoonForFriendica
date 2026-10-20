package com.livefast.eattrash.raccoonforfriendica.feature.explore

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
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.ExplorePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.ExplorePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.InstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class ExploreViewModel(
    private val paginationManager: ExplorePaginationManager,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
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
    private val credentialsRepository: CredentialsRepository,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    ExploreMvi,
    MviDelegate<ExploreMvi.Intent, ExploreMvi.State, ExploreMvi.Effect> by DefaultMviDelegate(
        initialState = ExploreMvi.State(),
    ) {

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
                    updateState {
                        it.copy(currentUserId = currentUser?.id)
                    }
                    refreshAvailableSections()

                    if (uiState.value.initial) {
                        viewModelScope.launch {
                            refresh(initial = true)
                        }
                    }
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
                    if (node.isNotEmpty()) {
                        viewModelScope.launch {
                            refresh()
                        }
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: ExploreMvi.Intent) {
        when (intent) {
            is ExploreMvi.Intent.ChangeSection -> handleChangeSection(intent.section)

            ExploreMvi.Intent.LoadNextPage -> handleLoadNextPage()

            ExploreMvi.Intent.Refresh -> handleRefresh()

            is ExploreMvi.Intent.Follow -> handleFollow(intent.userId)

            is ExploreMvi.Intent.Unfollow -> handleUnfollow(intent.userId)

            is ExploreMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            is ExploreMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is ExploreMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is ExploreMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is ExploreMvi.Intent.Delete -> handleDelete(intent.postId)

            is ExploreMvi.Intent.MuteUser -> handleMuteUser(
                userId = intent.userId,
                postId = intent.postId,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            is ExploreMvi.Intent.BlockUser -> handleBlockUser(userId = intent.userId, postId = intent.postId)

            is ExploreMvi.Intent.TogglePin -> handleTogglePin(intent.post)

            is ExploreMvi.Intent.SubmitPollVote -> handleSubmitPollVote(intent.post, intent.choices)

            is ExploreMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is ExploreMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is ExploreMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is ExploreMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)

            ExploreMvi.Intent.ResetOtherInstance -> handleResetOtherInstance()

            is ExploreMvi.Intent.SetSelectForeignInstanceName -> handleSetSelectForeignInstanceName(intent.name)

            ExploreMvi.Intent.SubmitSelectForeignInstanceName -> handleSubmitSelectForeignInstanceName()
        }
    }

    private fun handleChangeSection(section: ExploreSection) {
        viewModelScope.launch {
            if (uiState.value.loading) {
                return@launch
            }
            updateState { it.copy(section = section) }
            emitEffect(ExploreMvi.Effect.BackToTop)
            refresh(initial = true)
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

    private fun handleResetOtherInstance() {
        viewModelScope.launch {
            updateState { it.copy(otherInstance = null, refreshing = true) }
            refreshAvailableSections()
            refresh()
        }
    }

    private fun handleSetSelectForeignInstanceName(name: String) {
        viewModelScope.launch {
            updateState { it.copy(selectForeignInstanceName = name) }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            when (uiState.value.section) {
                ExploreSection.Hashtags ->
                    ExplorePaginationSpecification.Hashtags(
                        refresh = !initial,
                        otherInstance = uiState.value.otherInstance,
                    )

                ExploreSection.Links ->
                    ExplorePaginationSpecification.Links(otherInstance = uiState.value.otherInstance)

                ExploreSection.Posts ->
                    ExplorePaginationSpecification.Posts(
                        includeNsfw = settingsRepository.current.value?.includeNsfw == true,
                        otherInstance = uiState.value.otherInstance,
                    )

                ExploreSection.Suggestions -> ExplorePaginationSpecification.Suggestions
            },
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
            val posts = paginationManager.loadNextPage()
            posts.mapNotNull { (it as? ExploreItemModel.Post)?.post }.preloadImages()
            posts.mapNotNull { (it as? ExploreItemModel.User)?.user }.preloadAvatars()
            updateState {
                it.copy(
                    items = posts,
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
                    when (item) {
                        is ExploreItemModel.Post if item.post.id == postId -> {
                            item.copy(
                                post = item.post.let(block),
                            )
                        }

                        is ExploreItemModel.Post if item.post.reblog?.id == postId -> {
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
            val res = userRepository.unblock(userId)
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
                emitEffect(ExploreMvi.Effect.PollVoteFailure)
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
                emitEffect(ExploreMvi.Effect.TriggerCopy(text))
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
                emitEffect(ExploreMvi.Effect.OpenUrl(url))
            }
        }
    }

    private suspend fun refreshAvailableSections() {
        updateState { oldState ->
            oldState.copy(
                availableSections = buildList {
                    this += ExploreSection.Hashtags
                    this += ExploreSection.Posts
                    this += ExploreSection.Links
                    if (oldState.currentUserId != null && oldState.otherInstance.isNullOrEmpty()) {
                        this += ExploreSection.Suggestions
                    }
                },
            )
        }
    }

    private fun handleSubmitSelectForeignInstanceName() {
        viewModelScope.launch {
            val newNode = uiState.value.selectForeignInstanceName
            if (apiConfigurationRepository.node.value == newNode) {
                return@launch
            }

            // validate fields
            val nodeNameError =
                if (newNode.isBlank()) {
                    ValidationError.MissingField
                } else {
                    updateState { it.copy(selectForeignInstanceValidationInProgress = true) }
                    val isNodeValid = credentialsRepository.validateNode(newNode)
                    if (!isNodeValid) {
                        ValidationError.InvalidField
                    } else {
                        null
                    }
                }
            updateState {
                it.copy(
                    selectForeignInstanceNameError = nodeNameError,
                    selectForeignInstanceValidationInProgress = false,
                )
            }

            val isValid = nodeNameError == null
            if (!isValid) {
                return@launch
            }
            updateState { it.copy(otherInstance = newNode, selectForeignInstanceName = "", refreshing = true) }
            refreshAvailableSections()
            emitEffect(ExploreMvi.Effect.SelectForeignInstanceSuccess)
            emitEffect(ExploreMvi.Effect.BackToTop)
            refresh()
        }
    }
}
