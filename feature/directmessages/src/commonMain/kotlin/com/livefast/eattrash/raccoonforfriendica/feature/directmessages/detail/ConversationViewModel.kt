package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private val POLLING_INTERVAL = 1.2.seconds

@AssistedInject
class ConversationViewModel(
    @Assisted private val otherUserId: String,
    @Assisted private val parentUri: String,
    private val paginationManager: DirectMessagesPaginationManager,
    private val identityRepository: IdentityRepository,
    private val userRepository: UserRepository,
    private val messageRepository: DirectMessageRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviDelegate<ConversationMvi.Intent, ConversationMvi.State, ConversationMvi.Effect>
    by DefaultMviDelegate(initialState = ConversationMvi.State()),
    ConversationMvi {
    private var job: Job? = null

    private var parentUriToUse = parentUri

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
            val otherUser = userCache.get(otherUserId)
            val currentUser = identityRepository.currentUser.value
            updateState {
                it.copy(
                    currentUser = currentUser,
                    otherUser = otherUser,
                )
            }
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
        job =
            viewModelScope.launch {
                while (isActive) {
                    delay(POLLING_INTERVAL)
                    poll()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        job = null
    }

    override fun reduce(intent: ConversationMvi.Intent) {
        when (intent) {
            ConversationMvi.Intent.Refresh -> handleRefresh()
            ConversationMvi.Intent.LoadNextPage -> handleLoadNextPage()
            is ConversationMvi.Intent.SetNewMessageValue -> handleSetNewMessageValue(intent.value)
            ConversationMvi.Intent.Submit -> handleSubmit()
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

    private fun handleSetNewMessageValue(value: TextFieldValue) {
        viewModelScope.launch {
            updateState { it.copy(newMessageValue = value) }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            DirectMessagesPaginationSpecification.Replies(parentUri = parentUriToUse),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
            val items =
                paginationManager
                    .loadNextPage()
                    .onEach { markAsRead(it) }
            val wasRefreshing = uiState.value.refreshing
            updateState {
                it.copy(
                    items = items,
                    canFetchMore = paginationManager.canFetchMore,
                    loading = false,
                    initial = false,
                    refreshing = false,
                )
            }
            if (wasRefreshing) {
                emitEffect(ConversationMvi.Effect.BackToTop)
            }
        } catch (e: Exception) {
            updateState { it.copy(loading = false, refreshing = false) }
            if (e is CancellationException) throw e
        }
    }

    private fun updateParentUriIfNeeded(value: String?) {
        if (parentUriToUse.isEmpty() && !value.isNullOrBlank()) {
            parentUriToUse = value
        }
    }

    private suspend fun poll() {
        val originalItems = uiState.value.items
        val minId = originalItems.lastOrNull()?.id ?: return
        val newMessages =
            messageRepository
                .pollReplies(
                    parentUri = parentUriToUse,
                    minId = minId,
                )?.filter { m1 ->
                    originalItems.none { m2 -> m2.id == m1.id }
                }.orEmpty()
        if (newMessages.isNotEmpty()) {
            updateParentUriIfNeeded(newMessages.lastOrNull()?.parentUri)
            updateState { it.copy(items = newMessages + originalItems) }
            emitEffect(ConversationMvi.Effect.BackToTop)
        }
    }

    private suspend fun markAsRead(message: DirectMessageModel) {
        if (!message.read) {
            messageRepository.markAsRead(message.id)
        }
    }

    private suspend fun updateMessageInState(messageId: String, block: (DirectMessageModel) -> DirectMessageModel) {
        updateState {
            it.copy(
                items =
                it.items.map { msg ->
                    if (msg.id == messageId) {
                        msg.let(block)
                    } else {
                        msg
                    }
                },
            )
        }
    }

    private fun handleSubmit() {
        val currentState = uiState.value
        val text = currentState.newMessageValue.text
        if (text.isEmpty() || currentState.sendInProgress) {
            return
        }

        viewModelScope.launch {
            val relationshipStatus =
                userRepository.getRelationships(listOf(otherUserId))?.firstOrNull()?.toStatus()
            if (relationshipStatus !in
                listOf(
                    RelationshipStatus.MutualFollow,
                    RelationshipStatus.Following,
                )
            ) {
                // it is necessary that you follow the other user, otherwise messages are sent
                // to some other random user https://github.com/friendica/friendica/issues/11274

                emitEffect(ConversationMvi.Effect.FollowUserRequired)
                return@launch
            }

            val localId = getUuid()
            val originalItems = currentState.items
            val inReplyToId = originalItems.lastOrNull()?.id
            val newItem =
                DirectMessageModel(
                    text = text,
                    id = localId,
                    recipient = currentState.otherUser,
                    sender = currentState.currentUser,
                )
            updateState {
                it.copy(
                    sendInProgress = true,
                    items = listOf(newItem) + originalItems,
                )
            }
            emitEffect(ConversationMvi.Effect.BackToTop)
            val remoteMessage =
                messageRepository.create(
                    recipientId = otherUserId,
                    text = text,
                    inReplyTo = inReplyToId,
                )
            updateState { it.copy(sendInProgress = false) }
            if (remoteMessage == null) {
                emitEffect(ConversationMvi.Effect.Failure)
                updateState {
                    it.copy(items = originalItems)
                }
            } else {
                updateParentUriIfNeeded(remoteMessage.parentUri)
                updateMessageInState(localId) {
                    remoteMessage.copy(
                        // the title is appended in a newline before the body
                        text = remoteMessage.text?.substringAfter('\n'),
                    )
                }
                updateState {
                    it.copy(
                        newMessageValue = TextFieldValue(),
                    )
                }
            }
        }
    }

    companion object {
        fun getExtras(otherUserId: String, parentUri: String) = CreationExtras {
            this[KEY_OTHER_USER_ID] = otherUserId
            this[KEY_PARENT_URI] = parentUri
        }
    }
}

private val KEY_OTHER_USER_ID = CreationExtras.Key<String>()
private val KEY_PARENT_URI = CreationExtras.Key<String>()

@AssistedFactory
@ViewModelAssistedFactoryKey(ConversationViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface ConversationViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ConversationViewModel = create(
        extras[KEY_OTHER_USER_ID] ?: error("otherUserId argument not found"),
        extras[KEY_PARENT_URI] ?: error("parentUri argument not found"),
    )

    fun create(@Assisted otherUserId: String, @Assisted parentUri: String): ConversationViewModel
}
