package com.livefast.eattrash.raccoonforfriendica.feature.userdetail.classic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
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
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
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
class UserDetailViewModel(
    @Assisted private val args: UserDetailViewModelArgs,
    private val userRepository: UserRepository,
    private val paginationManager: TimelinePaginationManager,
    private val postRepository: PostRepository,
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
    private val toggleDislike: ToggleDislikeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val toggleTranslation: ToggleTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<UserDetailMvi.Intent, UserDetailMvi.State, UserDetailMvi.Effect>
    by DefaultMviDelegate(initialState = UserDetailMvi.State()),
    UserDetailMvi {

    init {
        viewModelScope.launch {
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

    override fun reduce(intent: UserDetailMvi.Intent) {
        when (intent) {
            is UserDetailMvi.Intent.ChangeSection -> handleChangeSection(intent.section)

            UserDetailMvi.Intent.LoadNextPage -> handleLoadNextPage()

            UserDetailMvi.Intent.Refresh -> handleRefresh()

            UserDetailMvi.Intent.Follow -> handleFollow()

            UserDetailMvi.Intent.Unfollow -> handleUnfollow()

            is UserDetailMvi.Intent.ToggleReblog -> handleToggleReblog(intent.post)

            is UserDetailMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.post)

            is UserDetailMvi.Intent.ToggleDislike -> handleToggleDislike(intent.post)

            is UserDetailMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.post)

            UserDetailMvi.Intent.DisableNotifications -> handleDisableNotifications()

            UserDetailMvi.Intent.EnableNotifications -> handleEnableNotifications()

            is UserDetailMvi.Intent.SubmitPollVote -> handleSubmitPollVote(
                post = intent.post,
                choices = intent.choices,
            )

            is UserDetailMvi.Intent.ToggleBlock -> handleToggleBlock(intent.blocked)

            is UserDetailMvi.Intent.ToggleMute -> handleToggleMute(
                muted = intent.muted,
                duration = intent.duration,
                disableNotifications = intent.disableNotifications,
            )

            UserDetailMvi.Intent.TogglePersonalNoteEditMode -> handleTogglePersonalNoteEditMode()

            is UserDetailMvi.Intent.SetPersonalNote -> handleSetPersonalNote(intent.note)

            UserDetailMvi.Intent.SubmitPersonalNote -> handleSubmitPersonalNote()

            is UserDetailMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.post)

            is UserDetailMvi.Intent.SetRateLimit -> handleSetRateLimit(intent.rateLimit)

            is UserDetailMvi.Intent.ToggleTranslation -> handleToggleTranslation(intent.post)

            is UserDetailMvi.Intent.WillOpenDetail -> handleWillOpenDetail(intent.post)

            is UserDetailMvi.Intent.AddInstanceShortcut -> handleAddInstanceShortcut(intent.node)

            is UserDetailMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.post)
        }
    }

    private fun handleChangeSection(section: UserSection) {
        viewModelScope.launch {
            if (uiState.value.loading) {
                return@launch
            }
            updateState { it.copy(section = section) }
            emitEffect(UserDetailMvi.Effect.BackToTop)
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

    private fun handleDisableNotifications() {
        toggleNotifications(enabled = false)
    }

    private fun handleEnableNotifications() {
        toggleNotifications(enabled = true)
    }

    private fun handleSetPersonalNote(note: String) {
        viewModelScope.launch {
            updateState { it.copy(personalNote = note) }
        }
    }

    private fun handleWillOpenDetail(post: PostModel) {
        viewModelScope.launch {
            val state = paginationManager.extractState()
            timelineNavigationManager.push(state)
            emitEffect(UserDetailMvi.Effect.OpenDetail(post))
        }
    }

    private suspend fun loadUser() {
        val user =
            with(emojiHelper) {
                userCache.get(args.id)?.withEmojisIfMissing()
            }
        updateState { it.copy(user = user) }
        val relationship =
            if (args.id != uiState.value.currentUserId) {
                userRepository.getRelationships(listOf(args.id))?.firstOrNull()
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
                userId = args.id,
                excludeReplies = uiState.value.section == UserSection.Posts,
                onlyMedia = uiState.value.section == UserSection.Media,
                pinned = uiState.value.section == UserSection.Pinned,
                includeNsfw = settingsRepository.current.value?.includeNsfw == true,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

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

    private fun handleFollow() {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateState { it.copy(user = it.user?.copy(relationshipStatusPending = true)) }
            val newRelationship = userRepository.follow(args.id)
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
            val newRelationship = userRepository.unfollow(args.id)
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

    private fun toggleNotifications(enabled: Boolean) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateState { it.copy(user = it.user?.copy(notificationStatusPending = true)) }
            val newRelationship =
                userRepository.follow(
                    id = args.id,
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
                emitEffect(UserDetailMvi.Effect.PollVoteFailure)
            }
        }
    }

    private fun handleToggleMute(
        muted: Boolean,
        duration: Duration = Duration.INFINITE,
        disableNotifications: Boolean,
    ) {
        viewModelScope.launch {
            val relationship =
                if (muted) {
                    userRepository.mute(
                        id = args.id,
                        durationSeconds = duration.inWholeSeconds,
                        notifications = disableNotifications,
                    )
                } else {
                    userRepository.unmute(args.id)
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

    private fun handleToggleBlock(blocked: Boolean) {
        viewModelScope.launch {
            val relationship =
                if (blocked) {
                    userRepository.block(args.id)
                } else {
                    userRepository.unblock(args.id)
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

    private fun handleTogglePersonalNoteEditMode() {
        viewModelScope.launch {
            val currentState = uiState.value
            if (!currentState.personalNoteEditEnabled) {
                updateState {
                    it.copy(personalNoteEditEnabled = true)
                }
            } else {
                val relationship = userRepository.getRelationships(listOf(args.id))?.firstOrNull()
                updateState {
                    it.copy(
                        personalNote = relationship?.note,
                        personalNoteEditEnabled = false,
                    )
                }
            }
        }
    }

    private fun handleSubmitPersonalNote() {
        val note = uiState.value.personalNote ?: return
        viewModelScope.launch {
            val relationShip = userRepository.updatePersonalNote(args.id, note)
            if (relationShip != null) {
                updateState {
                    it.copy(personalNoteEditEnabled = false)
                }
            } else {
                emitEffect(UserDetailMvi.Effect.Failure)
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
                emitEffect(UserDetailMvi.Effect.TriggerCopy(text))
            }
        }
    }

    private fun handleSetRateLimit(value: Double) {
        viewModelScope.launch {
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
                emitEffect(UserDetailMvi.Effect.OpenUrl(url))
            }
        }
    }

    companion object {
        fun getExtras(args: UserDetailViewModelArgs) = CreationExtras {
            this[KEY_ARGS] = args
        }
    }
}

private val KEY_ARGS = CreationExtras.Key<UserDetailViewModelArgs>()

data class UserDetailViewModelArgs(val id: String)

@AssistedFactory
@ViewModelAssistedFactoryKey(UserDetailViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface UserDetailViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): UserDetailViewModel =
        create(extras[KEY_ARGS] ?: error("ViewModel creation args not found"))

    fun create(@Assisted args: UserDetailViewModelArgs): UserDetailViewModel
}
