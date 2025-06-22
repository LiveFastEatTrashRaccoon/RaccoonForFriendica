package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.StopWordRepository
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.ManageBlocksItem
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.ManageBlocksSection
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ManageBlocksViewModel(
    private val paginationManager: UserPaginationManager,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val accountRepository: AccountRepository,
    private val userRateLimitRepository: UserRateLimitRepository,
    private val imagePreloadManager: ImagePreloadManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val stopWordRepository: StopWordRepository,
) : ViewModel(),
    MviModelDelegate<ManageBlocksMviModel.Intent, ManageBlocksMviModel.State, ManageBlocksMviModel.Effect>
    by DefaultMviModelDelegate(initialState = ManageBlocksMviModel.State()),
    ManageBlocksMviModel {
    private var originalStopWords: List<String> = emptyList()
    private val mutex = Mutex()

    init {
        viewModelScope.launch {
            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ManageBlocksMviModel.Intent) {
        when (intent) {
            is ManageBlocksMviModel.Intent.ChangeSection ->
                viewModelScope.launch {
                    check(!uiState.value.loading) { return@launch }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(ManageBlocksMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }
            ManageBlocksMviModel.Intent.LoadNextPage ->
                viewModelScope.launch {
                    if (uiState.value.section != ManageBlocksSection.StopWords) {
                        loadNextUserPage()
                    }
                }
            ManageBlocksMviModel.Intent.Refresh -> viewModelScope.launch { refresh() }
            is ManageBlocksMviModel.Intent.ToggleMute -> unmute(intent.userId)
            is ManageBlocksMviModel.Intent.ToggleBlock -> unblock(intent.userId)
            is ManageBlocksMviModel.Intent.SetRateLimit ->
                setRateLimit(
                    handle = intent.handle,
                    value = intent.rate,
                )

            is ManageBlocksMviModel.Intent.AddStopWord -> addStopWord(intent.word)
            is ManageBlocksMviModel.Intent.RemoveStopWord -> removeStopWord(intent.word)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(
                initial = initial,
                refreshing = !initial,
            )
        }
        mutex.withLock {
            val accountId = accountRepository.getActive()?.id
            originalStopWords = stopWordRepository.get(accountId)
        }
        val section = uiState.value.section
        when (section) {
            ManageBlocksSection.Blocked -> {
                paginationManager.reset(UserPaginationSpecification.Blocked)
                loadNextUserPage()
            }

            ManageBlocksSection.Limited -> {
                paginationManager.reset(UserPaginationSpecification.Limited)
                loadNextUserPage()
            }

            ManageBlocksSection.Muted -> {
                paginationManager.reset(UserPaginationSpecification.Muted)
                loadNextUserPage()
            }

            ManageBlocksSection.StopWords -> {
                val items = originalStopWords.map { ManageBlocksItem.StopWord(it) }
                updateState {
                    it.copy(
                        items = items,
                        initial = false,
                        canFetchMore = false,
                        refreshing = false,
                    )
                }
            }
        }
    }

    private suspend fun loadNextUserPage() {
        check(!uiState.value.loading) { return }

        updateState { it.copy(loading = true) }
        val users = paginationManager.loadNextPage()
        users.preloadImages()
        updateState {
            it.copy(
                items = users.map { user -> ManageBlocksItem.User(user) },
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }

    private fun List<UserModel>.preloadImages() {
        mapNotNull { user ->
            user.avatar?.takeIf { it.isNotEmpty() }
        }.onEach { url ->
            imagePreloadManager.preload(url)
        }
    }

    private suspend fun removeUserFromState(userId: String) {
        updateState {
            it.copy(
                items =
                it.items.filter { item ->
                    when (item) {
                        is ManageBlocksItem.User -> item.user.id != userId
                        else -> false
                    }
                },
            )
        }
    }

    private fun unmute(userId: String) {
        viewModelScope.launch {
            val res = userRepository.unmute(userId)
            if (res != null) {
                removeUserFromState(userId)
            }
        }
    }

    private fun unblock(userId: String) {
        viewModelScope.launch {
            val res = userRepository.unblock(userId)
            if (res != null) {
                removeUserFromState(userId)
            }
        }
    }

    private fun setRateLimit(handle: String, value: Double) {
        viewModelScope.launch {
            val accountId = accountRepository.getActive()?.id ?: return@launch
            val currentRate = userRateLimitRepository.getBy(handle = handle, accountId = accountId)
            when {
                value >= 1 && currentRate != null -> {
                    userRateLimitRepository.delete(currentRate.id)
                }

                value < 1 && currentRate != null -> {
                    userRateLimitRepository.update(currentRate.copy(rate = value))
                }
            }

            refresh()
        }
    }

    private fun addStopWord(word: String) {
        check(word.isNotBlank()) { return }
        viewModelScope.launch {
            mutex.withLock {
                val newValues =
                    if (originalStopWords.contains(word)) {
                        originalStopWords
                    } else {
                        originalStopWords + word
                    }
                val accountId = accountRepository.getActive()?.id
                originalStopWords = newValues
                stopWordRepository.update(accountId = accountId, items = newValues)
            }
            refresh()
        }
    }

    private fun removeStopWord(word: String) {
        viewModelScope.launch {
            mutex.withLock {
                val newValues = originalStopWords - word
                val accountId = accountRepository.getActive()?.id
                originalStopWords = newValues
                stopWordRepository.update(accountId = accountId, items = newValues)
            }
            refresh()
        }
    }
}
