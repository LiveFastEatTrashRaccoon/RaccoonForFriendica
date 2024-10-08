package com.livefast.eattrash.raccoonforfriendica.feature.composer

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.DraftDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryCreatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.epochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromNowToDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toIso8601Timestamp
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toVisibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.MediaRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val mediaRepository: MediaRepository,
    private val albumRepository: PhotoAlbumRepository,
    private val albumPhotoPaginationManager: AlbumPhotoPaginationManager,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
    private val scheduledEntryRepository: ScheduledEntryRepository,
    private val draftRepository: DraftRepository,
    private val settingsRepository: SettingsRepository,
    private val emojiRepository: EmojiRepository,
    private val userRepository: UserRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val notificationCenter: NotificationCenter,
) : DefaultMviModel<ComposerMviModel.Intent, ComposerMviModel.State, ComposerMviModel.Effect>(
        initialState = ComposerMviModel.State(),
    ),
    ComposerMviModel {
    private var uploadJobs = mutableMapOf<String, Job>()
    private var editedPostId: String? = null
    private var draftId: String? = null
    private val useBBCode: Boolean get() = supportedFeatureRepository.features.value.supportsBBCode
    private var mentionSuggestionJob: Job? = null

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
            val currentSettings = settingsRepository.current.value
            val nodeInfo = nodeInfoRepository.getInfo()
            supportedFeatureRepository.features
                .onEach { features ->
                    updateState {
                        it.copy(
                            titleFeatureSupported = features.supportsEntryTitles,
                            galleryFeatureSupported = features.supportsPhotoGallery,
                            pollFeatureSupported = features.supportsPolls,
                            availableVisibilities =
                                buildList {
                                    this += Visibility.Public
                                    this += Visibility.Unlisted
                                    if (features.supportsPrivateVisibility) {
                                        this += Visibility.Private
                                    }
                                    this += Visibility.Direct
                                    if (features.supportsCustomCircles) {
                                        this += Visibility.Circle()
                                    }
                                },
                        )
                    }
                }.launchIn(this)
            updateState {
                it.copy(
                    characterLimit = nodeInfo?.characterLimit,
                    attachmentLimit = nodeInfo?.attachmentLimit,
                    visibility =
                        if (inReplyToId != null) {
                            currentSettings?.defaultReplyVisibility
                        } else {
                            currentSettings?.defaultPostVisibility
                        }?.toVisibility() ?: Visibility.Public,
                )
            }

            loadAvailableCircles()

            val customEmojis = emojiRepository.getAll().orEmpty()
            updateState {
                it.copy(availableEmojis = customEmojis)
            }
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

            is ComposerMviModel.Intent.AddShareUrl -> addShareUrl(intent.url)

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
                    when (intent.fieldType) {
                        ComposerFieldType.Body ->
                            updateBody(intent.value)

                        ComposerFieldType.Spoiler ->
                            updateState {
                                it.copy(
                                    spoilerValue = intent.value,
                                    hasUnsavedChanges = true,
                                )
                            }

                        ComposerFieldType.Title ->
                            updateState {
                                it.copy(
                                    titleValue = intent.value,
                                    hasUnsavedChanges = true,
                                )
                            }
                    }
                }

            ComposerMviModel.Intent.ToggleHasSpoiler ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            hasSpoiler = !it.hasSpoiler,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            ComposerMviModel.Intent.ToggleHasTitle ->
                screenModelScope.launch {
                    updateState { it.copy(hasTitle = !it.hasTitle) }
                }

            is ComposerMviModel.Intent.SetVisibility ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            visibility = intent.visibility,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is ComposerMviModel.Intent.AddAttachment -> uploadAttachment(intent.byteArray)
            is ComposerMviModel.Intent.EditAttachmentDescription ->
                updateAttachmentDescription(intent.attachment, intent.description)

            is ComposerMviModel.Intent.RemoveAttachment -> removeAttachment(intent.attachment)
            is ComposerMviModel.Intent.AddLink ->
                addLink(
                    anchor = intent.link.first,
                    url = intent.link.second,
                )

            is ComposerMviModel.Intent.AddMention -> addMention(handle = intent.handle)

            is ComposerMviModel.Intent.CompleteMention -> completeMention(handle = intent.handle)

            is ComposerMviModel.Intent.AddInitialMentions -> {
                val mentions =
                    buildList {
                        val initialValue = intent.initialHandle.orEmpty()
                        if (initialValue.isNotEmpty()) {
                            this += initialValue
                        }
                        val currentUserHandle =
                            identityRepository.currentUser.value
                                ?.handle
                                .orEmpty()
                        val mentions =
                            inReplyToId
                                ?.let { entryCache.get(it) }
                                ?.mentions
                                .orEmpty()
                                .mapNotNull { it.handle }
                                .filterNot { it == initialValue }
                                .filterNot { it == currentUserHandle }
                        addAll(mentions)
                    }

                for (mention in mentions) {
                    addMention(handle = mention)
                }
            }

            is ComposerMviModel.Intent.AddGroupReference ->
                addMention(
                    handle = intent.handle,
                    privateToGroup = true,
                )

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
                    updateState {
                        it.copy(
                            sensitive = intent.sensitive,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is ComposerMviModel.Intent.AddBoldFormat -> addBoldFormat(intent.fieldType)
            is ComposerMviModel.Intent.AddItalicFormat -> addItalicFormat(intent.fieldType)
            is ComposerMviModel.Intent.AddUnderlineFormat -> addUnderlineFormat(intent.fieldType)
            is ComposerMviModel.Intent.AddStrikethroughFormat -> addStrikethroughFormat(intent.fieldType)
            is ComposerMviModel.Intent.AddCodeFormat -> addCodeFormat(intent.fieldType)
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

            ComposerMviModel.Intent.AddPoll ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            poll =
                                PollModel(
                                    id = "",
                                    options =
                                        buildList {
                                            this += PollOptionModel(title = "")
                                            this += PollOptionModel(title = "")
                                        },
                                ),
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is ComposerMviModel.Intent.SetPollMultiple ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            poll = it.poll?.copy(multiple = intent.multiple),
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is ComposerMviModel.Intent.SetPollExpirationDate ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            poll = it.poll?.copy(expiresAt = intent.date),
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is ComposerMviModel.Intent.AddPollOption ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            poll =
                                it.poll?.let { p ->
                                    p.copy(
                                        options =
                                            buildList {
                                                addAll(p.options)
                                                this += PollOptionModel(title = "")
                                            },
                                    )
                                },
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is ComposerMviModel.Intent.RemovePollOption ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            poll =
                                it.poll?.let { p ->
                                    p.copy(
                                        options = p.options.filterIndexed { idx, _ -> idx != intent.index },
                                    )
                                },
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is ComposerMviModel.Intent.EditPollOption ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            poll =
                                it.poll?.let { p ->
                                    p.copy(
                                        options =
                                            p.options.mapIndexed { idx, option ->
                                                if (idx != intent.index) {
                                                    option
                                                } else {
                                                    option.copy(title = intent.title)
                                                }
                                            },
                                    )
                                },
                            hasUnsavedChanges = true,
                        )
                    }
                }

            ComposerMviModel.Intent.RemovePoll ->
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            poll = null,
                            hasUnsavedChanges = true,
                        )
                    }
                }

            is ComposerMviModel.Intent.InsertCustomEmoji ->
                insertCustomEmoji(intent.fieldType, intent.emoji)

            ComposerMviModel.Intent.CreatePreview ->
                screenModelScope.launch {
                    createPreview()
                }

            ComposerMviModel.Intent.Submit -> submit()
        }
    }

    private suspend fun updateBody(value: TextFieldValue) {
        val wasShowingSuggestions = uiState.value.shouldShowMentionSuggestions
        val currentText = value.text
        val currentPosition =
            if (value.selection.collapsed) {
                value.selection.start - 1
            } else {
                null
            }
        val matches = USER_MENTION_REGEX.findAll(currentText).toList()
        val currentMention = matches.firstOrNull { it.range.contains(currentPosition) }
        val shouldShowSuggestions = currentMention != null
        if (currentMention != null) {
            val handlePrefix = currentMention.groups["handlePrefix"]?.value.orEmpty()
            refreshMentionSuggestions(handlePrefix)
        }
        updateState {
            it.copy(
                bodyValue = value,
                hasUnsavedChanges = true,
                shouldShowMentionSuggestions = shouldShowSuggestions,
                mentionSuggestions =
                    if (shouldShowSuggestions && !wasShowingSuggestions) {
                        emptyList()
                    } else {
                        it.mentionSuggestions
                    },
            )
        }
    }

    private suspend fun refreshMentionSuggestions(prefix: String) {
        updateState { it.copy(mentionSuggestionsLoading = true) }
        if (prefix.isNotEmpty()) {
            mentionSuggestionJob?.cancel()
            mentionSuggestionJob =
                screenModelScope.launch {
                    launch {
                        delay(750)
                        val users = userRepository.search(prefix, 0).orEmpty()
                        updateState {
                            it.copy(
                                mentionSuggestions = users,
                                mentionSuggestionsLoading = users.isEmpty(),
                            )
                        }
                        mentionSuggestionJob = null
                    }
                }
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

    private fun addLink(
        anchor: String,
        url: String,
    ) {
        screenModelScope.launch {
            val before =
                if (useBBCode) {
                    "[url=$url]"
                } else {
                    "<a href='$url'>"
                }
            val after =
                if (useBBCode) {
                    "[/url]"
                } else {
                    "</a>"
                }
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
            updateState { it.copy(bodyValue = newValue, hasUnsavedChanges = true) }
        }
    }

    private fun addMention(
        handle: String,
        privateToGroup: Boolean = false,
    ) {
        screenModelScope.launch {
            val additionalPart =
                buildString {
                    if (privateToGroup) {
                        append("!")
                    } else {
                        append("@")
                    }
                    append(handle)
                    append(" ")
                }
            val newValue =
                getNewTextFieldValue(
                    value = uiState.value.bodyValue,
                    additionalPart = additionalPart,
                    offsetAfter = additionalPart.length,
                )
            updateState { it.copy(bodyValue = newValue, hasUnsavedChanges = true) }
        }
    }

    private fun completeMention(handle: String) {
        screenModelScope.launch {
            val additionalPart =
                buildString {
                    append("@")
                    append(handle)
                }
            val value = uiState.value.bodyValue
            val text = value.text
            val currentPosition =
                if (value.selection.collapsed) {
                    value.selection.start - 1
                } else {
                    null
                }
            val matches = USER_MENTION_REGEX.findAll(text).toList()
            val currentMention = matches.firstOrNull { it.range.contains(currentPosition) }
            if (currentMention != null) {
                val indexOfDelimiter = currentMention.range.first
                val endIndex = currentMention.range.last + 1
                val newText =
                    buildString {
                        append(text.substring(0, indexOfDelimiter))
                        append(additionalPart)
                        if (endIndex < text.length) {
                            append(text.substring(endIndex, text.length))
                        }
                    }

                val newValue =
                    uiState.value.bodyValue.copy(
                        text = newText,
                        selection = TextRange(newText.length),
                    )
                mentionSuggestionJob?.cancel()
                mentionSuggestionJob = null
                updateState {
                    it.copy(
                        bodyValue = newValue,
                        hasUnsavedChanges = true,
                        shouldShowMentionSuggestions = false,
                    )
                }
            }
        }
    }

    private fun addShareUrl(url: String) {
        screenModelScope.launch {
            val additionalPart =
                buildString {
                    append("\n")
                    append("[share]$url[/share]")
                }
            val newValue =
                getNewTextFieldValue(
                    value = uiState.value.bodyValue,
                    additionalPart = additionalPart,
                    offsetAfter = additionalPart.length,
                )
            updateState { it.copy(bodyValue = newValue, hasUnsavedChanges = true) }
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
            val before =
                if (useBBCode) {
                    "[b]"
                } else {
                    "<b>"
                }
            val after =
                if (useBBCode) {
                    "[/b]"
                } else {
                    "</b>"
                }
            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart =
                        buildString {
                            append(before)
                            append(selectedText)
                            append(after)
                        },
                    offsetAfter = before.length,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body ->
                        it.copy(
                            bodyValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Spoiler ->
                        it.copy(
                            spoilerValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Title ->
                        it.copy(
                            titleValue = newValue,
                            hasUnsavedChanges = true,
                        )
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
            val before =
                if (useBBCode) {
                    "[i]"
                } else {
                    "<i>"
                }
            val after =
                if (useBBCode) {
                    "[/i]"
                } else {
                    "</i>"
                }
            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart =
                        buildString {
                            append(before)
                            append(selectedText)
                            append(after)
                        },
                    offsetAfter = before.length,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body ->
                        it.copy(
                            bodyValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Spoiler ->
                        it.copy(
                            spoilerValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Title ->
                        it.copy(
                            titleValue = newValue,
                            hasUnsavedChanges = true,
                        )
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
            val before =
                if (useBBCode) {
                    "[u]"
                } else {
                    "<u>"
                }
            val after =
                if (useBBCode) {
                    "[/u]"
                } else {
                    "</u>"
                }
            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart =
                        buildString {
                            append(before)
                            append(selectedText)
                            append(after)
                        },
                    offsetAfter = before.length,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body ->
                        it.copy(
                            bodyValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Spoiler ->
                        it.copy(
                            spoilerValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Title ->
                        it.copy(
                            titleValue = newValue,
                            hasUnsavedChanges = true,
                        )
                }
            }
        }
    }

    private fun addStrikethroughFormat(fieldType: ComposerFieldType) {
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
            val before =
                if (useBBCode) {
                    "[s]"
                } else {
                    "<s>"
                }
            val after =
                if (useBBCode) {
                    "[/s]"
                } else {
                    "</s>"
                }
            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart =
                        buildString {
                            append(before)
                            append(selectedText)
                            append(after)
                        },
                    offsetAfter = before.length,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body ->
                        it.copy(
                            bodyValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Spoiler ->
                        it.copy(
                            spoilerValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Title ->
                        it.copy(
                            titleValue = newValue,
                            hasUnsavedChanges = true,
                        )
                }
            }
        }
    }

    private fun addCodeFormat(fieldType: ComposerFieldType) {
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
            val before =
                if (useBBCode) {
                    "[code]"
                } else {
                    "<code>"
                }
            val after =
                if (useBBCode) {
                    "[/code]"
                } else {
                    "</code>"
                }
            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart =
                        buildString {
                            append(before)
                            append(selectedText)
                            append(after)
                        },
                    offsetAfter = before.length,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body ->
                        it.copy(
                            bodyValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Spoiler ->
                        it.copy(
                            spoilerValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Title ->
                        it.copy(
                            titleValue = newValue,
                            hasUnsavedChanges = true,
                        )
                }
            }
        }
    }

    private fun insertCustomEmoji(
        fieldType: ComposerFieldType,
        emoji: EmojiModel,
    ) {
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
            val additionalPart =
                buildString {
                    append(":")
                    append(emoji.code)
                    append(":")
                }
            val newValue =
                getNewTextFieldValue(
                    value = value,
                    additionalPart = additionalPart,
                    offsetAfter = additionalPart.length,
                )
            updateState {
                when (fieldType) {
                    ComposerFieldType.Body ->
                        it.copy(
                            bodyValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Spoiler ->
                        it.copy(
                            spoilerValue = newValue,
                            hasUnsavedChanges = true,
                        )

                    ComposerFieldType.Title ->
                        it.copy(
                            titleValue = newValue,
                            hasUnsavedChanges = true,
                        )
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
                if (supportedFeatureRepository.features.value.supportsPhotoGallery) {
                    photoRepository.create(byteArray)
                } else {
                    mediaRepository.create(byteArray)
                }
            if (attachment != null) {
                updateState {
                    it.copy(
                        attachments = it.attachments.filter { a -> a.id != PLACEHOLDER_ID } + attachment,
                        hasUnsavedChanges = true,
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
                hasUnsavedChanges = true,
            )
        }
    }

    private suspend fun removeAttachmentFromState(attachmentId: String) {
        updateState {
            it.copy(
                attachments = it.attachments.filter { attachment -> attachment.id != attachmentId },
                hasUnsavedChanges = true,
            )
        }
    }

    private fun updateAttachmentDescription(
        attachment: AttachmentModel,
        description: String,
    ) {
        screenModelScope.launch {
            val successful =
                if (supportedFeatureRepository.features.value.supportsPhotoGallery) {
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
                    if (supportedFeatureRepository.features.value.supportsPhotoGallery) {
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
                poll = entry.poll,
                loading = false,
            )
        }
    }

    private suspend fun createPreview() {
        val currentState = uiState.value
        val inReplyTo = inReplyToId?.let { entryCache.get(it) }
        val localId = getUuid()
        val entry =
            TimelineEntryModel(
                creator = currentState.author,
                spoiler =
                    currentState.spoilerValue.text
                        .takeIf { currentState.hasSpoiler }
                        ?.prepareForPreview(useBBCode),
                title =
                    currentState.titleValue.text
                        .takeIf { currentState.hasTitle }
                        ?.prepareForPreview(useBBCode),
                content = currentState.bodyValue.text.prepareForPreview(useBBCode),
                poll = currentState.poll,
                attachments = currentState.attachments,
                id = localId,
                inReplyTo = inReplyTo,
            )
        emitEffect(ComposerMviModel.Effect.OpenPreview(entry))
    }

    private fun submit() {
        val currentState = uiState.value
        if (currentState.loading) {
            return
        }

        val spoiler = currentState.spoilerValue.text.takeIf { currentState.hasSpoiler }
        val title = currentState.titleValue.text.takeIf { it.isNotBlank() && currentState.hasTitle }
        val text = currentState.bodyValue.text
        val poll = currentState.poll
        // use the mediaId for this call otherwise the backend returns a 500
        val attachmentIds = currentState.attachments.map { it.mediaId }
        val visibility = currentState.visibility
        val characterLimit = currentState.characterLimit ?: Int.MAX_VALUE
        val key = getUuid()
        val publicationType = currentState.publicationType
        val scheduleDate = (publicationType as? PublicationType.Scheduled)?.date

        screenModelScope.launch {
            if (text.isBlank() && attachmentIds.isEmpty() && poll == null) {
                emitEffect(ComposerMviModel.Effect.ValidationError.TextOrImagesOrPollMandatory)
                return@launch
            }

            if (poll != null) {
                val timeSpan = poll.expiresAt?.let { getDurationFromNowToDate(it) } ?: Duration.ZERO
                val optionsPopulated = poll.options.all { it.title.isNotBlank() }
                if (poll.options.size < 2 || !optionsPopulated || timeSpan <= Duration.ZERO) {
                    emitEffect(ComposerMviModel.Effect.ValidationError.InvalidPoll)
                    return@launch
                }
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
                                    pollMultiple = poll?.multiple,
                                    pollOptions = poll?.options?.map { it.title },
                                    pollExpirationDate = poll?.expiresAt,
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
                                    pollMultiple = poll?.multiple,
                                    pollOptions = poll?.options?.map { it.title },
                                    pollExpirationDate = poll?.expiresAt,
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
                                    pollMultiple = poll?.multiple,
                                    pollOptions = poll?.options?.map { it.title },
                                    pollExpirationDate = poll?.expiresAt,
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
                                    poll = poll,
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
                    when (currentState.publicationType) {
                        is PublicationType.Scheduled -> {
                            val draftToDeleteId = draftId
                            if (draftToDeleteId != null) {
                                draftRepository.delete(draftToDeleteId)
                                notificationCenter.send(DraftDeletedEvent(draftToDeleteId))
                            }

                            if (editId != null) {
                                notificationCenter.send(TimelineEntryUpdatedEvent(res))
                            } else {
                                notificationCenter.send(TimelineEntryCreatedEvent(res))
                            }
                        }
                        PublicationType.Draft -> {
                            if (draftId != null) {
                                notificationCenter.send(TimelineEntryUpdatedEvent(res))
                            } else {
                                notificationCenter.send(TimelineEntryCreatedEvent(res))
                            }
                        }
                        PublicationType.Default -> {
                            val draftToDeleteId = draftId
                            if (draftToDeleteId != null) {
                                draftRepository.delete(draftToDeleteId)
                                notificationCenter.send(DraftDeletedEvent(draftToDeleteId))
                            }

                            if (editId != null) {
                                notificationCenter.send(TimelineEntryUpdatedEvent(res))
                            } else {
                                notificationCenter.send(TimelineEntryCreatedEvent(res))
                            }
                        }
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

    private fun String.prepareForPreview(withBBCode: Boolean): String =
        run {
            if (withBBCode) {
                convertBBCodeToHtml()
            } else {
                this
            }
        }.withMentions().withHashtags()

    private fun String.convertBBCodeToHtml(): String =
        replace("[u]", "<u>")
            .replace("[/u]", "</u>")
            .replace("[s]", "<s>")
            .replace("[/s]", "</s>")
            .replace("[i]", "<i>")
            .replace("[/i]", "</i>")
            .replace("[b]", "<b>")
            .replace("[/b]", "</b>")
            .replace("[code]", "<code>")
            .replace("[/code]", "</code>")
            .also { original ->
                val matches = SHARE_REGEX.findAll(original).toList()
                buildString {
                    var index = 0
                    for (match in matches) {
                        val range = match.range
                        append(original.substring(index, range.first))
                        val url = match.groupValues.firstOrNull().orEmpty()
                        append("<a href=\"$url\">#$url</a>")
                        index = range.last + 1
                    }
                    if (index < original.length) {
                        append(original.substring(index, original.length))
                    }
                }
            }

    private fun String.withMentions(): String =
        also { original ->
            val matches = USER_MENTION_REGEX.findAll(original).toList()
            buildString {
                var index = 0
                for (match in matches) {
                    val range = match.range
                    append(original.substring(index, range.first))
                    val handle = match.groupValues.firstOrNull().orEmpty()
                    val currentNode = apiConfigurationRepository.node.value
                    val node = handle.nodeName ?: currentNode
                    val name = handle.substringBefore("@")
                    val url =
                        if (node == currentNode) {
                            "https://$node/profile/$name"
                        } else {
                            "https://$node/users/$name"
                        }
                    append("<a href=\"$url\">#$handle</a>")
                    index = range.last + 1
                }
                if (index < original.length) {
                    append(original.substring(index, original.length))
                }
            }
        }

    private fun String.withHashtags(): String =
        also { original ->
            val matches = HASHTAG_REGEX.findAll(original).toList()
            buildString {
                var index = 0
                for (match in matches) {
                    val range = match.range
                    append(original.substring(index, range.first))
                    val tag = match.groupValues.firstOrNull().orEmpty()
                    val node = apiConfigurationRepository.node.value
                    val url = "https://$node/search?tag=$tag"
                    append("<a href=\"$url\">#$tag</a>")
                    index = range.last + 1
                }
                if (index < original.length) {
                    append(original.substring(index, original.length))
                }
            }
        }

    companion object {
        private val USER_MENTION_REGEX =
            Regex("@(?<handlePrefix>[a-zA-Z0-9-_.]+?(@[a-zA-Z0-9_.]+)?)(?=\\b)")
        private val SHARE_REGEX =
            Regex("\\[share](?<url>.*?)\\[share]")
        private val HASHTAG_REGEX =
            Regex("#(?<tag>.*?)(?=\\b)")
    }
}