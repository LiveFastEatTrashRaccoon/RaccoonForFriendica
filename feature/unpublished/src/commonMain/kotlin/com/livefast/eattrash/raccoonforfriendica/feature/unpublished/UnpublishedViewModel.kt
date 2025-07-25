package com.livefast.eattrash.raccoonforfriendica.feature.unpublished

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.DraftDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryCreatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UnpublishedPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UnpublishedPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UnpublishedViewModel(
    private val paginationManager: UnpublishedPaginationManager,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val scheduledEntryRepository: ScheduledEntryRepository,
    private val draftRepository: DraftRepository,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : ViewModel(),
    MviModelDelegate<UnpublishedMviModel.Intent, UnpublishedMviModel.State, UnpublishedMviModel.Effect>
    by DefaultMviModelDelegate(initialState = UnpublishedMviModel.State()),
    UnpublishedMviModel {
    init {
        viewModelScope.launch {
            notificationCenter
                .subscribe(DraftDeletedEvent::class)
                .onEach { event ->
                    removeEntryFromState(event.id)
                }.launchIn(this)
            notificationCenter
                .subscribe(TimelineEntryUpdatedEvent::class)
                .onEach { event ->
                    updateEntryInState(event.entry.id) { event.entry }
                }.launchIn(this)
            notificationCenter
                .subscribe(TimelineEntryCreatedEvent::class)
                .onEach {
                    refresh()
                }.launchIn(this)

            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUser = currentUser) }

                    if (uiState.value.initial) {
                        refresh(initial = true)
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

    override fun reduce(intent: UnpublishedMviModel.Intent) {
        when (intent) {
            is UnpublishedMviModel.Intent.ChangeSection ->
                viewModelScope.launch {
                    check(!uiState.value.loading) { return@launch }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(UnpublishedMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            is UnpublishedMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            UnpublishedMviModel.Intent.LoadNextPage ->
                viewModelScope.launch {
                    loadNextPage()
                }

            UnpublishedMviModel.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }
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
        check(!uiState.value.loading) { return }

        updateState { it.copy(loading = true) }
        val currentUser = uiState.value.currentUser
        val entries = paginationManager.loadNextPage().map { it.copy(creator = currentUser) }
        entries.preloadImages()
        updateState {
            it.copy(
                entries = entries,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }

    private suspend fun List<TimelineEntryModel>.preloadImages() {
        flatMap { entry ->
            entry.original.urlsForPreload
        }.forEach { url ->
            imagePreloadManager.preload(url)
        }
        flatMap { entry ->
            entry.blurHashParamsForPreload
        }.forEach {
            blurHashRepository.preload(it)
        }
    }

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                entries = it.entries.filter { e -> e.id != entryId },
            )
        }
    }

    private suspend fun updateEntryInState(entryId: String, block: (TimelineEntryModel) -> TimelineEntryModel) {
        updateState {
            it.copy(
                entries =
                it.entries.map { entry ->
                    when {
                        entry.id == entryId -> entry.let(block)
                        else -> entry
                    }
                },
            )
        }
    }

    private fun deleteEntry(entryId: String) {
        val currentState = uiState.value
        viewModelScope.launch {
            val success =
                when (currentState.section) {
                    UnpublishedType.Scheduled -> scheduledEntryRepository.delete(entryId)
                    UnpublishedType.Drafts -> draftRepository.delete(entryId)
                }
            if (success) {
                notificationCenter.send(TimelineEntryDeletedEvent(entryId))
                removeEntryFromState(entryId)
            }
        }
    }
}
