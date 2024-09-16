package com.livefast.eattrash.raccoonforfriendica.feature.composer

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.DraftDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.epochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromNowToDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toIso8601Timestamp
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isFriendica
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.MediaRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository
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
import kotlin.time.Duration

private const val PLACEHOLDER_ID = "placeholder"

@OptIn(FlowPreview::class)
class ComposerViewModel(
    private val inReplyToId: String? = null,
    private val identityRepository: IdentityRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val photoRepository: PhotoRepository,
    private val userPaginationManager: UserPaginationManager,
    private val circlesRepository: CirclesRepository,
    private val nodeInfoRepository: NodeInfoRepository,
    private val mediaRepository: MediaRepository,
    private val albumRepository: PhotoAlbumRepository,
    private val albumPhotoPaginationManager: AlbumPhotoPaginationManager,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
    private val scheduledEntryRepository: ScheduledEntryRepository,
    private val draftRepository: DraftRepository,
    private val notificationCenter: NotificationCenter,
) : DefaultMviModel<ComposerMviModel.Intent, ComposerMviModel.State, ComposerMviModel.Effect>(
        initialState = ComposerMviModel.State(),
    ),
    ComposerMviModel {
    private var uploadJobs = mutableMapOf<String, Job>()
    private var editedPostId: String? = null
    private var draftId: String? = null

    /*
     * Attachments work differently on Friendica so a flag is needed in order to determine
     * how attachments will be uploaded/edited/deleted.
     */
    private var isFriendica = true

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
            val nodeInfo = nodeInfoRepository.getInfo()
            isFriendica = nodeInfo?.isFriendica != false
            updateState {
                it.copy(
                    hasGallery = isFriendica,
                    characterLimit = nodeInfo?.characterLimit,
                    attachmentLimit = nodeInfo?.attachmentLimit,
                )
            }

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
        val circles = circlesRepository.getAll().orEmpty()
        updateState { it.copy(availableCircles = circles) }
    }

    override fun reduce(intent: ComposerMviModel.Intent) {
        when (intent) {
            is ComposerMviModel.Intent.LoadEditedPost -> {
                editedPostId = intent.id
                loadEditedPost()
            }

            is ComposerMviModel.Intent.LoadScheduled ->
                screenModelScope.launch {
                    editedPostId = intent.id
                    val entry = entryCache.get(intent.id)
                    if (entry != null) {
                        entry.scheduled?.also { scheduleDate ->
                            updateState {
                                it.copy(publicationType = PublicationType.Scheduled(scheduleDate))
                            }
                        }
                        loadEntry(entry)
                    }
                }

            is ComposerMviModel.Intent.LoadDraft ->
                screenModelScope.launch {
                    draftId = intent.id
                    val entry = entryCache.get(intent.id)
                    if (entry != null) {
                        updateState {
                            it.copy(publicationType = PublicationType.Draft)
                        }
                        loadEntry(entry)
                    }
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

            is ComposerMviModel.Intent.RemoveAttachment -> removeAttachment(intent.attachment)
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
            is ComposerMviModel.Intent.AddAttachmentsFromGallery ->
                addAttachmentsFromGallery(intent.attachments)

            is ComposerMviModel.Intent.GalleryAlbumSelected ->
                screenModelScope.launch {
                    updateState { it.copy(galleryCurrentAlbum = intent.album) }
                    refreshGalleryPhotos()
                }

            ComposerMviModel.Intent.GalleryInitialLoad ->
                screenModelScope.launch {
                    val albums = albumRepository.getAll().orEmpty()
                    val currentAlbum = albums.firstOrNull()
                    updateState {
                        it.copy(
                            galleryAlbums = albums,
                            galleryCurrentAlbum = currentAlbum?.name,
                        )
                    }
                    refreshGalleryPhotos()
                }

            ComposerMviModel.Intent.GalleryLoadMorePhotos ->
                screenModelScope.launch {
                    loadNextPageGalleryPhotos()
                }

            is ComposerMviModel.Intent.ChangePublicationType ->
                screenModelScope.launch {
                    updateState { it.copy(publicationType = intent.type) }
                }

            ComposerMviModel.Intent.Submit -> submit()
        }
    }

    private fun addAttachmentsFromGallery(attachments: List<AttachmentModel>) {
        screenModelScope.launch {
            val currentAttachments = uiState.value.attachments
            val attachmentsToAdd =
                attachments
                    .filter { a1 -> currentAttachments.none { a2 -> a1.id == a2.id } }
                    .map { it.copy(fromGallery = true) }
            updateState {
                it.copy(attachments = it.attachments + attachmentsToAdd)
            }
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
            val attachment =
                if (isFriendica) {
                    photoRepository.create(byteArray)
                } else {
                    mediaRepository.create(byteArray)
                }
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
                if (isFriendica) {
                    photoRepository.update(
                        id = attachment.id,
                        album = attachment.album.orEmpty(),
                        alt = description,
                    )
                } else {
                    mediaRepository.update(
                        id = attachment.id,
                        alt = description,
                    )
                }

            if (successful) {
                updateAttachmentInState(attachment.id) {
                    it.copy(description = description)
                }
            }
        }
    }

    private fun removeAttachment(attachment: AttachmentModel) {
        screenModelScope.launch {
            val attachmentId = attachment.id
            if (attachment.fromGallery || editedPostId != null || draftId != null) {
                // soft removal
                removeAttachmentFromState(attachmentId)
            } else {
                val success =
                    if (isFriendica) {
                        photoRepository.delete(attachmentId)
                    } else {
                        mediaRepository.delete(attachmentId)
                    }
                if (success) {
                    removeAttachmentFromState(attachmentId)
                }
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

    private suspend fun refreshGalleryPhotos() {
        val albumName = uiState.value.galleryCurrentAlbum ?: return
        albumPhotoPaginationManager.reset(
            AlbumPhotoPaginationSpecification.Default(albumName),
        )
        updateState { it.copy(galleryCanFetchMore = albumPhotoPaginationManager.canFetchMore) }
        loadNextPageGalleryPhotos()
    }

    private suspend fun loadNextPageGalleryPhotos() {
        if (uiState.value.galleryLoading) {
            return
        }

        updateState { it.copy(galleryLoading = true) }
        val photos = albumPhotoPaginationManager.loadNextPage()
        updateState {
            it.copy(
                galleryCurrentAlbumPhotos = photos,
                galleryCanFetchMore = albumPhotoPaginationManager.canFetchMore,
                galleryLoading = false,
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
            updateState { it.copy(loading = true) }
            val entry = timelineEntryRepository.getById(id)
            if (entry != null) {
                loadEntry(entry)
            }
        }
    }

    private suspend fun loadEntry(entry: TimelineEntryModel) {
        val visibility =
            entry.visibility.let { visibility ->
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
                bodyValue = TextFieldValue(text = entry.content),
                sensitive = entry.sensitive,
                attachments = entry.attachments,
                visibility = visibility,
                spoilerValue = TextFieldValue(text = entry.spoiler.orEmpty()),
                hasSpoiler = !entry.spoiler.isNullOrEmpty(),
                titleValue = TextFieldValue(text = entry.title.orEmpty()),
                hasTitle = !entry.title.isNullOrEmpty(),
                loading = false,
            )
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
        val characterLimit = currentState.characterLimit ?: Int.MAX_VALUE
        val key = getUuid()
        val publicationType = currentState.publicationType
        val scheduleDate = (publicationType as? PublicationType.Scheduled)?.date

        screenModelScope.launch {
            if (text.isBlank() && attachmentIds.isEmpty()) {
                emitEffect(ComposerMviModel.Effect.ValidationError.TextOrImagesMandatory)
                return@launch
            }

            if (text.length > characterLimit) {
                emitEffect(ComposerMviModel.Effect.ValidationError.CharacterLimitExceeded)
                return@launch
            }

            if (visibility is Visibility.Circle && visibility.id == null) {
                emitEffect(ComposerMviModel.Effect.ValidationError.InvalidVisibility)
                return@launch
            }

            if (scheduleDate != null) {
                val timeSpan = getDurationFromNowToDate(scheduleDate) ?: Duration.ZERO
                if (timeSpan <= Duration.ZERO) {
                    emitEffect(ComposerMviModel.Effect.ValidationError.ScheduleDateInThePast)
                    return@launch
                }
            }

            updateState { it.copy(loading = true) }
            val editId = editedPostId
            try {
                val res =
                    when (publicationType) {
                        is PublicationType.Scheduled -> {
                            if (editId != null) {
                                scheduledEntryRepository.update(
                                    id = editId,
                                    date = publicationType.date,
                                )
                            } else {
                                timelineEntryRepository.create(
                                    localId = key,
                                    title = title,
                                    text = text,
                                    inReplyTo = inReplyToId,
                                    spoilerText = spoiler,
                                    sensitive = currentState.sensitive,
                                    visibility = visibility,
                                    lang = currentState.lang,
                                    mediaIds = attachmentIds,
                                    scheduled = publicationType.date,
                                )
                            }
                        }

                        PublicationType.Default -> {
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
                                    visibility = visibility,
                                    lang = currentState.lang,
                                    mediaIds = attachmentIds,
                                )
                            }
                        }

                        PublicationType.Draft -> {
                            val entry =
                                TimelineEntryModel(
                                    id = editId ?: key,
                                    content = text,
                                    title = title,
                                    updated =
                                        epochMillis().toIso8601Timestamp(withLocalTimezone = false),
                                    spoiler = spoiler,
                                    sensitive = currentState.sensitive,
                                    visibility = visibility,
                                    parentId = inReplyToId,
                                    lang = currentState.lang,
                                    attachments =
                                        attachmentIds.map {
                                            AttachmentModel(id = it, url = "")
                                        },
                                )
                            if (draftId != null) {
                                draftRepository.update(entry)
                            } else {
                                draftRepository.create(entry)
                            }
                        }
                    }
                updateState { it.copy(loading = false) }
                if (res != null) {
                    val draftToDeleteId = draftId
                    if (currentState.publicationType != PublicationType.Draft && draftToDeleteId != null) {
                        draftRepository.delete(draftToDeleteId)
                        notificationCenter.send(DraftDeletedEvent(draftToDeleteId))
                    }

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
