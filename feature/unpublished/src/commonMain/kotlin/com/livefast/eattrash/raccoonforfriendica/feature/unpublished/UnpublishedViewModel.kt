package com.livefast.eattrash.raccoonforfriendica.feature.unpublished

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.DraftDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostCreatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UnpublishedPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UnpublishedPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledPostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class UnpublishedViewModel(
    private val paginationManager: UnpublishedPaginationManager,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val scheduledPostRepository: ScheduledPostRepository,
    private val draftRepository: DraftRepository,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<UnpublishedMvi.Intent, UnpublishedMvi.State, UnpublishedMvi.Effect>
    by DefaultMviDelegate(initialState = UnpublishedMvi.State()),
    UnpublishedMvi {

    init {
        viewModelScope.launch {
            notificationCenter
                .subscribe(DraftDeletedEvent::class)
                .onEach { event ->
                    removePostFromState(event.id)
                }.launchIn(this)
            notificationCenter
                .subscribe(PostUpdatedEvent::class)
                .onEach { event ->
                    updatePostInState(event.post.id) { event.post }
                }.launchIn(this)
            notificationCenter
                .subscribe(PostCreatedEvent::class)
                .onEach {
                    refresh()
                }.launchIn(this)

            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUser = currentUser) }

                    if (uiState.value.initial) {
                        viewModelScope.launch {
                            refresh(initial = true)
                        }
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
        }
    }

    override fun reduce(intent: UnpublishedMvi.Intent) {
        when (intent) {
            is UnpublishedMvi.Intent.ChangeSection -> handleChangeSection(intent.section)
            is UnpublishedMvi.Intent.Delete -> handleDelete(intent.postId)
            UnpublishedMvi.Intent.LoadNextPage -> handleLoadNextPage()
            UnpublishedMvi.Intent.Refresh -> handleRefresh()
        }
    }

    private fun handleChangeSection(section: UnpublishedType) {
        viewModelScope.launch {
            if (uiState.value.loading) {
                return@launch
            }
            updateState { it.copy(section = section) }
            emitEffect(UnpublishedMvi.Effect.BackToTop)
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
        paginationManager.reset(
            when (uiState.value.section) {
                UnpublishedType.Scheduled -> UnpublishedPaginationSpecification.Scheduled
                UnpublishedType.Drafts -> UnpublishedPaginationSpecification.Drafts
            },
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
            val currentUser = uiState.value.currentUser
            val posts = paginationManager.loadNextPage().map { it.copy(creator = currentUser) }
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

    private suspend fun removePostFromState(postId: String) {
        updateState {
            it.copy(
                posts = it.posts.filter { post -> post.id != postId },
            )
        }
    }

    private suspend fun updatePostInState(postId: String, block: (PostModel) -> PostModel) {
        updateState {
            it.copy(
                posts =
                it.posts.map { post ->
                    when {
                        post.id == postId -> post.let(block)
                        else -> post
                    }
                },
            )
        }
    }

    private fun handleDelete(postId: String) {
        val currentState = uiState.value
        viewModelScope.launch {
            val success =
                when (currentState.section) {
                    UnpublishedType.Scheduled -> scheduledPostRepository.delete(postId)
                    UnpublishedType.Drafts -> draftRepository.delete(postId)
                }
            if (success) {
                notificationCenter.send(PostDeletedEvent(postId))
                removePostFromState(postId)
            }
        }
    }
}
