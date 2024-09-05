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
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
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
    private val identityRepository: IdentityRepository,
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

            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(author = currentUser) }
                }.launchIn(this)

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
            is ComposerMviModel.Intent.SetFieldValue ->
                screenModelScope.launch {
                    updateState {
                        when (intent.fieldType) {
                            ComposerFieldType.Body -> it.copy(bodyValue = intent.value)
                            ComposerFieldType.Spoiler -> it.copy(spoilerValue = intent.value)
                            ComposerFieldType.Title -> it.copy(titleValue = intent.value)
                        }
                    }
                }

            ComposerMviModel.Intent.ToggleHasSpoiler ->
                screenModelScope.launch {
                    updateState { it.copy(hasSpoiler = !it.hasSpoiler) }
                }

            ComposerMviModel.Intent.ToggleHasTitle ->
                screenModelScope.launch {
                    updateState { it.copy(hasTitle = !it.hasTitle) }
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
                    val newValue =
                        getNewTextFieldValue(
                            value = uiState.value.bodyValue,
                            additionalPart =
                                buildString {
                                    append(before)
                                    append(anchor)
                                    append(after)
                                },
                            offsetAfter = before.length,
                        )
                    updateState { it.copy(bodyValue = newValue) }
                }
            }

            is ComposerMviModel.Intent.AddMention -> {
                screenModelScope.launch {
                    val additionalPart = "@${intent.handle}"
                    val newValue =
                        getNewTextFieldValue(
                            value = uiState.value.bodyValue,
                            additionalPart = additionalPart,
                            offsetAfter = additionalPart.length,
                        )
                    updateState { it.copy(bodyValue = newValue) }
                }
            }

            is ComposerMviModel.Intent.AddGroupReference -> {
                screenModelScope.launch {
                    val additionalPart = "!${intent.handle}"
                    val newValue =
                        getNewTextFieldValue(
                            value = uiState.value.bodyValue,
                            additionalPart = additionalPart,
                            offsetAfter = additionalPart.length,
                        )
                    updateState { it.copy(bodyValue = newValue) }
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

            is ComposerMviModel.Intent.AddBoldFormat -> addBoldFormat(intent.fieldType)
            is ComposerMviModel.Intent.AddItalicFormat -> addItalicFormat(intent.fieldType)
            is ComposerMviModel.Intent.AddUnderlineFormat -> addUnderlineFormat(intent.fieldType)
            ComposerMviModel.Intent.Submit -> submit()
        }
    }

    private fun addBoldFormat(fieldType: ComposerFieldType) {
        screenModelScope.launch {
            val value =
                when (fieldType) {
                    ComposerFieldType.Body ->
                        uiState.value.bodyValue

                    ComposerFieldType.Spoiler ->
                        uiState.value.spoilerValue

                    ComposerFieldType.Title ->
                        uiState.value.titleValue
                }
            val selectedText = value.getSelectedText().text
            val additionalPart = "<b>$selectedText</b>"
            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart = additionalPart,
                    offsetAfter = 3,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body -> it.copy(bodyValue = newValue)
                    ComposerFieldType.Spoiler -> it.copy(spoilerValue = newValue)
                    ComposerFieldType.Title -> it.copy(titleValue = newValue)
                }
            }
        }
    }

    private fun addItalicFormat(fieldType: ComposerFieldType) {
        screenModelScope.launch {
            val value =
                when (fieldType) {
                    ComposerFieldType.Body ->
                        uiState.value.bodyValue

                    ComposerFieldType.Spoiler ->
                        uiState.value.spoilerValue

                    ComposerFieldType.Title ->
                        uiState.value.titleValue
                }
            val selectedText = value.getSelectedText().text
            val additionalPart = "<i>$selectedText</i>"

            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart = additionalPart,
                    offsetAfter = 3,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body -> it.copy(bodyValue = newValue)
                    ComposerFieldType.Spoiler -> it.copy(spoilerValue = newValue)
                    ComposerFieldType.Title -> it.copy(titleValue = newValue)
                }
            }
        }
    }

    private fun addUnderlineFormat(fieldType: ComposerFieldType) {
        screenModelScope.launch {
            val value =
                when (fieldType) {
                    ComposerFieldType.Body ->
                        uiState.value.bodyValue

                    ComposerFieldType.Spoiler ->
                        uiState.value.spoilerValue

                    ComposerFieldType.Title ->
                        uiState.value.titleValue
                }
            val selectedText = value.getSelectedText().text
            val additionalPart = "<u>$selectedText</u>"

            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart = additionalPart,
                    offsetAfter = 3,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body -> it.copy(bodyValue = newValue)
                    ComposerFieldType.Spoiler -> it.copy(spoilerValue = newValue)
                    ComposerFieldType.Title -> it.copy(titleValue = newValue)
                }
            }
        }
    }

    private fun uploadAttachment(byteArray: ByteArray) {
        screenModelScope.launch {
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
            } else {
                emitEffect(ComposerMviModel.Effect.Failure())
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
        userPaginationManager.reset(
            UserPaginationSpecification.Search(
                query = query,
                withRelationship = false,
            ),
        )
        updateState { it.copy(userSearchCanFetchMore = userPaginationManager.canFetchMore) }
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

    private fun getNewTextFieldValue(
        value: TextFieldValue,
        additionalPart: String,
        offsetAfter: Int,
    ): TextFieldValue {
        val (text, selection) = value.let { it.text to it.selection }
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
        return value.copy(text = newText, selection = newSelection)
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

        val spoiler = currentState.spoilerValue.text.takeIf { currentState.hasSpoiler }
        val title = currentState.titleValue.text.takeIf { it.isNotBlank() && currentState.hasTitle }
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
                            title = title,
                            text = text,
                            inReplyTo = inReplyToId,
                            spoilerText = spoiler,
                            sensitive = currentState.sensitive,
                            visibility = visibility,
                            lang = currentState.lang,
                            mediaIds = attachmentIds,
                        )
                    } else {
                        timelineEntryRepository.create(
                            localId = key,
                            title = title,
                            text = text,
                            inReplyTo = inReplyToId,
                            spoilerText = spoiler,
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
