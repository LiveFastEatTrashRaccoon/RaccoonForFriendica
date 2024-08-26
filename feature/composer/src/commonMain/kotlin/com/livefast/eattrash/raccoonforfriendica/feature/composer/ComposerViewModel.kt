package com.livefast.eattrash.raccoonforfriendica.feature.composer

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val PLACEHOLDER_ID = "placeholder"

@OptIn(FlowPreview::class)
class ComposerViewModel(
    private val inReplyToId: String? = null,
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val photoRepository: PhotoRepository,
    private val userPaginationManager: UserPaginationManager,
    private val circlesRepository: CirclesRepository,
) : DefaultMviModel<ComposerMviModel.Intent, ComposerMviModel.State, ComposerMviModel.Effect>(
        initialState = ComposerMviModel.State(),
    ),
    ComposerMviModel {
    private var uploadJobs = mutableMapOf<String, Job>()
    private var editedPostId: String? = null

    init {
        screenModelScope.launch {
            uiState
                .map { it.userSearchQuery }
                .distinctUntilChanged()
                .drop(1)
                .debounce(750)
                .onEach { query ->
                    refreshUsers(query)
                }.launchIn(this)

            refreshAuthor()
            loadAvailableCircles()
        }
    }

    override fun onDispose() {
        super.onDispose()
        uploadJobs.forEach { entry ->
            entry.value.cancel()
        }
        uploadJobs.clear()
    }

    private suspend fun refreshAuthor() {
        val handle = accountRepository.getActive()?.handle.orEmpty()
        val currentAccount = userRepository.getByHandle(handle)
        updateState { it.copy(author = currentAccount) }
    }

    private suspend fun loadAvailableCircles() {
        val circles = circlesRepository.getAll()
        updateState { it.copy(availableCircles = circles) }
    }

    override fun reduce(intent: ComposerMviModel.Intent) {
        when (intent) {
            is ComposerMviModel.Intent.LoadEditedPost -> {
                editedPostId = intent.id
                loadEditedPost()
            }
            is ComposerMviModel.Intent.SetBodyValue ->
                screenModelScope.launch {
                    updateState { it.copy(bodyValue = intent.value) }
                }

            is ComposerMviModel.Intent.SetSpoilerText ->
                screenModelScope.launch {
                    updateState { it.copy(spoilerText = intent.spoiler) }
                }

            is ComposerMviModel.Intent.SetVisibility ->
                screenModelScope.launch {
                    updateState { it.copy(visibility = intent.visibility) }
                }

            is ComposerMviModel.Intent.AddAttachment -> uploadAttachment(intent.byteArray)
            is ComposerMviModel.Intent.EditAttachmentDescription ->
                updateAttachmentDescription(intent.attachment, intent.description)

            is ComposerMviModel.Intent.RemoveAttachment -> removeAttachment(intent.attachmentId)
            is ComposerMviModel.Intent.AddLink -> {
                screenModelScope.launch {
                    val (anchor, url) = intent.link
                    val before = "<a href=\"$url\">"
                    val after = "</a>"
                    updateBodyValue(
                        additionalPart =
                            buildString {
                                append(before)
                                append(anchor)
                                append(after)
                            },
                        offsetAfter = before.length,
                    )
                }
            }

            is ComposerMviModel.Intent.AddMention -> {
                screenModelScope.launch {
                    val additionalPart = "@${intent.handle}"
                    updateBodyValue(
                        additionalPart = additionalPart,
                        offsetAfter = additionalPart.length,
                    )
                }
            }

            is ComposerMviModel.Intent.AddGroupReference -> {
                screenModelScope.launch {
                    val additionalPart = "!${intent.handle}"
                    updateBodyValue(
                        additionalPart = additionalPart,
                        offsetAfter = additionalPart.length,
                    )
                }
            }

            is ComposerMviModel.Intent.UserSearchSetQuery ->
                screenModelScope.launch {
                    updateState { it.copy(userSearchQuery = intent.query) }
                }

            ComposerMviModel.Intent.UserSearchClear ->
                screenModelScope.launch {
                    updateState { it.copy(userSearchUsers = emptyList()) }
                }

            ComposerMviModel.Intent.UserSearchLoadNextPage ->
                screenModelScope.launch {
                    loadNextPageUsers()
                }

            is ComposerMviModel.Intent.SetSensitive ->
                screenModelScope.launch {
                    updateState { it.copy(sensitive = intent.sensitive) }
                }

            ComposerMviModel.Intent.AddBoldFormat ->
                screenModelScope.launch {
                    val selectedText =
                        uiState.value.bodyValue
                            .getSelectedText()
                            .text
                    val additionalPart = "<b>$selectedText</b>"
                    updateBodyValue(
                        additionalPart = additionalPart,
                        offsetAfter = 3,
                    )
                }

            ComposerMviModel.Intent.AddItalicFormat ->
                screenModelScope.launch {
                    val selectedText =
                        uiState.value.bodyValue
                            .getSelectedText()
                            .text
                    val additionalPart = "<i>$selectedText</i>"
                    updateBodyValue(
                        additionalPart = additionalPart,
                        offsetAfter = 3,
                    )
                }

            ComposerMviModel.Intent.AddUnderlineFormat ->
                screenModelScope.launch {
                    val selectedText =
                        uiState.value.bodyValue
                            .getSelectedText()
                            .text
                    val additionalPart = "<u>$selectedText</u>"
                    updateBodyValue(
                        additionalPart = additionalPart,
                        offsetAfter = 3,
                    )
                }

            ComposerMviModel.Intent.Submit -> submit()
        }
    }

    private fun uploadAttachment(byteArray: ByteArray) {
        screenModelScope.launch {
            try {
                updateState {
                    it.copy(
                        attachments =
                            it.attachments +
                                AttachmentModel(
                                    id = PLACEHOLDER_ID,
                                    url = "",
                                    loading = true,
                                ),
                    )
                }
                val attachment = photoRepository.create(byteArray)
                if (attachment != null) {
                    updateState {
                        it.copy(
                            attachments = it.attachments.filter { a -> a.id != PLACEHOLDER_ID } + attachment,
                        )
                    }
                }
            } catch (e: Throwable) {
                emitEffect(ComposerMviModel.Effect.Failure(e.message))
            }
        }
    }

    private suspend fun updateAttachmentInState(
        attachmentId: String,
        block: (AttachmentModel) -> AttachmentModel,
    ) {
        updateState {
            it.copy(
                attachments =
                    it.attachments.map { attachment ->
                        if (attachment.id == attachmentId) {
                            attachment.let(block)
                        } else {
                            attachment
                        }
                    },
            )
        }
    }

    private suspend fun removeAttachmentFromState(attachmentId: String) {
        updateState {
            it.copy(attachments = it.attachments.filter { attachment -> attachment.id != attachmentId })
        }
    }

    private fun updateAttachmentDescription(
        attachment: AttachmentModel,
        description: String,
    ) {
        screenModelScope.launch {
            val successful =
                photoRepository.update(
                    id = attachment.id,
                    album = attachment.album.orEmpty(),
                    alt = description,
                )
            if (successful) {
                updateAttachmentInState(attachment.id) {
                    it.copy(description = description)
                }
            }
        }
    }

    private fun removeAttachment(attachmentId: String) {
        screenModelScope.launch {
            val success = photoRepository.delete(attachmentId)
            if (success) {
                removeAttachmentFromState(attachmentId)
            }
        }
    }

    private suspend fun refreshUsers(query: String) {
        if (query.isEmpty()) {
            return
        }
        userPaginationManager.reset(UserPaginationSpecification.Search(query))
        loadNextPageUsers()
    }

    private suspend fun loadNextPageUsers() {
        if (uiState.value.userSearchLoading) {
            return
        }

        updateState { it.copy(userSearchLoading = true) }
        val users = userPaginationManager.loadNextPage()
        updateState {
            it.copy(
                userSearchUsers = users,
                userSearchCanFetchMore = userPaginationManager.canFetchMore,
                userSearchLoading = false,
            )
        }
    }

    private suspend fun updateBodyValue(
        additionalPart: String,
        offsetAfter: Int,
    ) {
        val bodyValue = uiState.value.bodyValue
        val (text, selection) = uiState.value.bodyValue.let { it.text to it.selection }
        val newText =
            buildString {
                append(text.substring(0, selection.start))
                append(additionalPart)
                append(
                    text.substring(
                        selection.end,
                        text.length,
                    ),
                )
            }

        val newSelection =
            if (selection.collapsed) {
                TextRange(index = selection.start + offsetAfter)
            } else {
                TextRange(
                    start = selection.start + offsetAfter,
                    end = selection.end + offsetAfter,
                )
            }
        val newValue = bodyValue.copy(text = newText, selection = newSelection)
        updateState { it.copy(bodyValue = newValue) }
    }

    private fun loadEditedPost() {
        val id = editedPostId ?: return
        screenModelScope.launch {
            val entry = timelineEntryRepository.getById(id)
            val visibility =
                entry?.visibility.let { visibility ->
                    when (visibility) {
                        is Visibility.Circle ->
                            visibility.id
                                ?.let { circleId -> circlesRepository.get(circleId) }
                                ?.let { circle ->
                                    Visibility.Circle(id = circle.id, name = circle.name)
                                }

                        else -> {
                            visibility
                        }
                    }
                } ?: Visibility.Public

            updateState {
                it.copy(
                    bodyValue = TextFieldValue(text = entry?.content.orEmpty()),
                    sensitive = entry?.sensitive ?: false,
                    attachments = entry?.attachments.orEmpty(),
                    visibility = visibility,
                )
            }
        }
    }

    private fun submit() {
        val currentState = uiState.value
        if (currentState.loading) {
            return
        }

        val text = currentState.bodyValue.text
        // use the mediaId for this call otherwise the backend returns a 500
        val attachmentIds = currentState.attachments.map { it.mediaId }
        val visibility = currentState.visibility
        val key = getUuid()

        screenModelScope.launch {
            if (text.isBlank() && attachmentIds.isEmpty()) {
                emitEffect(ComposerMviModel.Effect.ValidationError.TextOrImagesMandatory)
                return@launch
            }

            if (visibility is Visibility.Circle && visibility.id == null) {
                emitEffect(ComposerMviModel.Effect.ValidationError.InvalidVisibility)
                return@launch
            }

            updateState { it.copy(loading = true) }
            val editId = editedPostId
            try {
                val res =
                    if (editId != null) {
                        timelineEntryRepository.update(
                            id = editId,
                            text = text,
                            inReplyTo = inReplyToId,
                            spoilerText = currentState.spoilerText,
                            sensitive = currentState.sensitive,
                            visibility = visibility,
                            lang = currentState.lang,
                            mediaIds = attachmentIds,
                        )
                    } else {
                        timelineEntryRepository.create(
                            localId = key,
                            text = text,
                            inReplyTo = inReplyToId,
                            spoilerText = currentState.spoilerText,
                            sensitive = currentState.sensitive,
                            visibility = currentState.visibility,
                            lang = currentState.lang,
                            mediaIds = attachmentIds,
                        )
                    }
                updateState { it.copy(loading = false) }
                if (res != null) {
                    emitEffect(ComposerMviModel.Effect.Success)
                } else {
                    emitEffect(ComposerMviModel.Effect.Failure(null))
                }
            } catch (e: Throwable) {
                updateState { it.copy(loading = false) }
                emitEffect(ComposerMviModel.Effect.Failure(message = e.message))
            }
        }
    }
}
