package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val POLLING_INTERVAL = 1200L

class ConversationViewModel(
    private val otherUserId: String,
    parentUri: String,
    private val paginationManager: DirectMessagesPaginationManager,
    private val identityRepository: IdentityRepository,
    private val userRepository: UserRepository,
    private val messageRepository: DirectMessageRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviModelDelegate<ConversationMviModel.Intent, ConversationMviModel.State, ConversationMviModel.Effect>
    by DefaultMviModelDelegate(initialState = ConversationMviModel.State()),
    ConversationMviModel {
    private var parentUriToUse = parentUri
    private var job: Job? = null

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

    override fun reduce(intent: ConversationMviModel.Intent) {
        when (intent) {
            ConversationMviModel.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }

            ConversationMviModel.Intent.LoadNextPage ->
                viewModelScope.launch {
                    loadNextPage()
                }

            is ConversationMviModel.Intent.SetNewMessageValue ->
                viewModelScope.launch {
                    updateState { it.copy(newMessageValue = intent.value) }
                }

            ConversationMviModel.Intent.Submit -> submit()
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
        check(!uiState.value.loading) { return }

        updateState { it.copy(loading = true) }
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
            emitEffect(ConversationMviModel.Effect.BackToTop)
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
            emitEffect(ConversationMviModel.Effect.BackToTop)
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

    private fun submit() {
        val currentState = uiState.value
        val text = currentState.newMessageValue.text
        check(text.isNotEmpty() && !currentState.sendInProgress) { return }

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

                emitEffect(ConversationMviModel.Effect.FollowUserRequired)
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
            emitEffect(ConversationMviModel.Effect.BackToTop)
            val remoteMessage =
                messageRepository.create(
                    recipientId = otherUserId,
                    text = text,
                    inReplyTo = inReplyToId,
                )
            updateState { it.copy(sendInProgress = false) }
            if (remoteMessage == null) {
                emitEffect(ConversationMviModel.Effect.Failure)
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
}
