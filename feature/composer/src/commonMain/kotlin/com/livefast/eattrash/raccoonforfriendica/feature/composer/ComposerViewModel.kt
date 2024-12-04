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
import com.livefast.eattrash.raccoonforfriendica.core.utils.substituteAllOccurrences
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.compareTo
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
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feature.composer.converters.BBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.PrepareForPreviewUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.StripMarkupUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.composer.utils.ComposerRegexes
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
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import kotlin.time.Duration

private const val PLACEHOLDER_ID = "placeholder"

@Factory(binds = [ComposerMviModel::class])
@OptIn(FlowPreview::class)
class ComposerViewModel(
    @InjectedParam private val inReplyToId: String?,
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
    private val prepareForPreview: PrepareForPreviewUseCase,
    private val stripMarkup: StripMarkupUseCase,
    private val notificationCenter: NotificationCenter,
    private val bbCodeConverter: BBCodeConverter,
) : DefaultMviModel<ComposerMviModel.Intent, ComposerMviModel.State, ComposerMviModel.Effect>(
        initialState = ComposerMviModel.State(),
    ),
    ComposerMviModel {
    private var uploadJobs = mutableMapOf<String, Job>()
    private var editedPostId: String? = null
    private var draftId: String? = null
    private var mentionSuggestionJob: Job? = null

    private val shouldUserPhotoRepository: Boolean by lazy {
        supportedFeatureRepository.features.value.supportsPhotoGallery
    }

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
            val circles =
                circlesRepository.getAll()?.filter { it.type == CircleType.UserDefined }.orEmpty()
            updateState { it.copy(availableCircles = circles) }
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
                                    this += Visibility.Private
                                    this += Visibility.Direct
                                    if (features.supportsCustomCircles && circles.isNotEmpty()) {
                                        this += Visibility.Circle()
                                    }
                                },
                            availableMarkupModes =
                                buildList {
                                    this += MarkupMode.PlainText
                                    if (features.supportsBBCode) {
                                        this += MarkupMode.BBCode
                                    }
                                    this += MarkupMode.HTML
                                    if (features.supportsMarkdown) {
                                        this += MarkupMode.Markdown
                                    }
                                },
                        )
                    }
                }.launchIn(this)
            val parent = inReplyToId?.let { e -> entryCache.get(e) }
            val initialVisibility =
                if (inReplyToId != null) {
                    currentSettings
                        ?.defaultReplyVisibility
                        ?.toVisibility()
                        // make sure to have a visibility less than or equal to the parent
                        ?.takeIf { it <= (parent?.visibility ?: Visibility.Unlisted) }
                        ?: parent?.visibility
                } else {
                    currentSettings?.defaultPostVisibility?.toVisibility()
                } ?: Visibility.Unlisted

            updateState {
                it.copy(
                    characterLimit = nodeInfo?.characterLimit,
                    attachmentLimit = nodeInfo?.attachmentLimit,
                    visibility = initialVisibility,
                    inReplyTo = parent,
                    markupMode = currentSettings?.markupMode ?: MarkupMode.PlainText,
                )
            }

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

            is ComposerMviModel.Intent.AddInitialMentions ->
                screenModelScope.launch {
                    val mentions =
                        buildList {
                            val currentUserHandle =
                                identityRepository.currentUser.value
                                    ?.handle
                                    .orEmpty()
                            val initialValue = intent.initialHandle.orEmpty()
                            if (initialValue.isNotEmpty() && initialValue != currentUserHandle) {
                                this += initialValue
                            }
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

            is ComposerMviModel.Intent.InsertList -> insertList()

            is ComposerMviModel.Intent.Submit ->
                submit(
                    enableAltTextCheck = intent.enableAltTextCheck,
                    enableParentVisibilityCheck = intent.enableParentVisibilityCheck,
                )

            is ComposerMviModel.Intent.ChangeMarkupMode -> changeMarkupMode(intent.mode)
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
        val matches = ComposerRegexes.USER_MENTION.findAll(currentText).toList()
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
        val markupMode = uiState.value.markupMode
        screenModelScope.launch {
            val before =
                when (markupMode) {
                    MarkupMode.HTML -> "<a href='$url'>"
                    MarkupMode.BBCode -> "[url=$url]"
                    MarkupMode.Markdown -> "["
                    else -> url
                }
            val after =
                when (markupMode) {
                    MarkupMode.HTML -> "</a>"
                    MarkupMode.BBCode -> "[/url]"
                    MarkupMode.Markdown -> "]($url)"
                    else -> ""
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
            updateState {
                it.copy(
                    bodyValue = newValue,
                    hasUnsavedChanges = true,
                )
            }
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
            updateState {
                it.copy(
                    bodyValue = newValue,
                    hasUnsavedChanges = true,
                )
            }
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
            val newText =
                ComposerRegexes.USER_MENTION.substituteAllOccurrences(text) { match ->
                    val isCurrentMention = match.range.contains(currentPosition)
                    if (!isCurrentMention) {
                        // skips occurrence
                        append(match.value)
                    } else {
                        append(additionalPart)
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
            updateState {
                it.copy(
                    bodyValue = newValue,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun addBoldFormat(fieldType: ComposerFieldType) {
        val markupMode = uiState.value.markupMode
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
                when (markupMode) {
                    MarkupMode.HTML -> "<b>"
                    MarkupMode.BBCode -> "[b]"
                    MarkupMode.Markdown -> "**"
                    else -> ""
                }
            val after =
                when (markupMode) {
                    MarkupMode.HTML -> "</b>"
                    MarkupMode.BBCode -> "[/b]"
                    MarkupMode.Markdown -> "**"
                    else -> ""
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
        val markupMode = uiState.value.markupMode
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
                when (markupMode) {
                    MarkupMode.HTML -> "<i>"
                    MarkupMode.BBCode -> "[i]"
                    MarkupMode.Markdown -> "_"
                    else -> ""
                }
            val after =
                when (markupMode) {
                    MarkupMode.HTML -> "</i>"
                    MarkupMode.BBCode -> "[/i]"
                    MarkupMode.Markdown -> "_"
                    else -> ""
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
        val markupMode = uiState.value.markupMode
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
                when (markupMode) {
                    MarkupMode.HTML -> "<u>"
                    MarkupMode.BBCode -> "[u]"
                    MarkupMode.Markdown -> "<u>"
                    else -> ""
                }
            val after =
                when (markupMode) {
                    MarkupMode.HTML -> "</u>"
                    MarkupMode.BBCode -> "[/u]"
                    MarkupMode.Markdown -> "</u>"
                    else -> ""
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
        val markupMode = uiState.value.markupMode
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
                when (markupMode) {
                    MarkupMode.HTML -> "<s>"
                    MarkupMode.BBCode -> "[s]"
                    MarkupMode.Markdown -> "~~"
                    else -> ""
                }
            val after =
                when (markupMode) {
                    MarkupMode.HTML -> "</s>"
                    MarkupMode.BBCode -> "[/s]"
                    MarkupMode.Markdown -> "~~"
                    else -> ""
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
        val markupMode = uiState.value.markupMode
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
                when (markupMode) {
                    MarkupMode.HTML -> "<code>"
                    MarkupMode.BBCode -> "[code]"
                    MarkupMode.Markdown -> "`"
                    else -> ""
                }
            val after =
                when (markupMode) {
                    MarkupMode.HTML -> "</code>"
                    MarkupMode.BBCode -> "[/code]"
                    MarkupMode.Markdown -> "`"
                    else -> ""
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

    private fun insertList() {
        val markupMode = uiState.value.markupMode
        screenModelScope.launch {
            val before =
                when (markupMode) {
                    MarkupMode.BBCode -> "\n[ul]\n[li]"
                    MarkupMode.HTML -> "\n<ul>\n<li>"
                    MarkupMode.Markdown -> "\n\n* "
                    else -> "\n- "
                }
            val after =
                when (markupMode) {
                    MarkupMode.HTML -> "</li></ul>"
                    MarkupMode.BBCode -> "[/li]\n[/ul]"
                    MarkupMode.Markdown -> "\n"
                    else -> "\n"
                }
            val newValue =
                getNewTextFieldValue(
                    value = uiState.value.bodyValue,
                    additionalPart =
                        buildString {
                            append(before)
                            append(after)
                        },
                    offsetAfter = before.length,
                )
            updateState {
                it.copy(
                    bodyValue = newValue,
                    hasUnsavedChanges = true,
                )
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
                if (shouldUserPhotoRepository) {
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
                if (shouldUserPhotoRepository) {
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
            removeAttachmentFromState(attachment.id)
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
        // workaround to make sure Friendica-specific circle visibility is preserved
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
        // workaround to make sure IDs are preserved
        val attachments =
            entry.attachments.map { attachment ->
                if (attachment.mediaId.isNotEmpty()) {
                    attachment
                } else {
                    attachment.copy(mediaId = attachment.id)
                }
            }

        val markupMode = settingsRepository.current.value?.markupMode ?: MarkupMode.PlainText
        val reference =
            when (markupMode) {
                // attempt to reconstruct the original BBCode syntax
                MarkupMode.BBCode -> {
                    val bbCode = bbCodeConverter.fromHtml(entry.content)
                    entry.copy(content = bbCode)
                }
                // make the server strip off all the HTML
                MarkupMode.HTML -> timelineEntryRepository.getSource(entry.id) ?: entry
                // Markdown or PlainText are good to go
                else -> entry
            }

        updateState {
            it.copy(
                bodyValue = TextFieldValue(reference.content),
                sensitive = entry.sensitive,
                attachments = attachments,
                visibility = visibility,
                spoilerValue = TextFieldValue(reference.spoiler.orEmpty()),
                hasSpoiler = !reference.spoiler.isNullOrEmpty(),
                titleValue = TextFieldValue(reference.title.orEmpty()),
                hasTitle = !reference.title.isNullOrEmpty(),
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
                        ?.let {
                            prepareForPreview(text = it, mode = currentState.markupMode)
                        },
                title =
                    currentState.titleValue.text
                        .takeIf { currentState.hasTitle }
                        ?.let {
                            prepareForPreview(text = it, mode = currentState.markupMode)
                        },
                content =
                    currentState.bodyValue.text.let {
                        prepareForPreview(text = it, mode = currentState.markupMode)
                    },
                poll = currentState.poll,
                attachments = currentState.attachments,
                id = localId,
                inReplyTo = inReplyTo,
            )
        emitEffect(ComposerMviModel.Effect.OpenPreview(entry))
    }

    private suspend fun validate(
        enableAltTextCheck: Boolean,
        enableParentVisibilityCheck: Boolean,
    ): Boolean {
        val currentState = uiState.value
        val visibility = currentState.visibility
        val text = currentState.bodyValue.text
        val attachments = currentState.attachments
        val poll = currentState.poll
        val characterLimit = currentState.characterLimit ?: Int.MAX_VALUE
        val publicationType = currentState.publicationType
        val scheduleDate = (publicationType as? PublicationType.Scheduled)?.date

        // either body or image or poll must be present
        if (text.isBlank() && attachments.isEmpty() && poll == null) {
            emitEffect(ComposerMviModel.Effect.ValidationError.TextOrImagesOrPollMandatory)
            return false
        }

        // all images should have a description for a11y
        if (enableAltTextCheck && attachments.any { it.description.isNullOrEmpty() }) {
            emitEffect(ComposerMviModel.Effect.ValidationError.AltTextMissing)
            return false
        }

        if (poll != null) {
            // poll must have at least 2 options and an expiration date in the future
            val timeSpan = poll.expiresAt?.let { getDurationFromNowToDate(it) } ?: Duration.ZERO
            val optionsPopulated = poll.options.all { it.title.isNotBlank() }
            if (poll.options.size < 2 || !optionsPopulated || timeSpan <= Duration.ZERO) {
                emitEffect(ComposerMviModel.Effect.ValidationError.InvalidPoll)
                return false
            }
        }

        // character limit should not be exceeded
        if (text.length > characterLimit) {
            emitEffect(ComposerMviModel.Effect.ValidationError.CharacterLimitExceeded)
            return false
        }

        // Circle visibility should be valid
        if (visibility is Visibility.Circle && visibility.id == null) {
            emitEffect(ComposerMviModel.Effect.ValidationError.InvalidVisibility)
            return false
        }

        // Replies should not have higher visibility than parent
        if (enableParentVisibilityCheck && currentState.inReplyTo != null && visibility > currentState.inReplyTo.visibility) {
            emitEffect(ComposerMviModel.Effect.ValidationError.VisibilityGreaterThanParent)
            return false
        }

        // schedule date must be in the future
        if (scheduleDate != null) {
            val timeSpan = getDurationFromNowToDate(scheduleDate) ?: Duration.ZERO
            if (timeSpan <= Duration.ZERO) {
                emitEffect(ComposerMviModel.Effect.ValidationError.ScheduleDateInThePast)
                return false
            }
        }

        return true
    }

    private fun changeMarkupMode(mode: MarkupMode) {
        val currentState = uiState.value
        val oldMode = currentState.markupMode
        if (mode == oldMode) {
            return
        }
        screenModelScope.launch {
            val newTitle =
                currentState.titleValue.text.let { stripMarkup(text = it, mode = oldMode) }
            val newSpoiler =
                currentState.spoilerValue.text.let { stripMarkup(text = it, mode = oldMode) }
            val newBody =
                currentState.bodyValue.text.let { stripMarkup(text = it, mode = oldMode) }
            updateState {
                it.copy(
                    titleValue = TextFieldValue(newTitle),
                    spoilerValue = TextFieldValue(newSpoiler),
                    bodyValue = TextFieldValue(newBody),
                    markupMode = mode,
                )
            }
        }
    }

    private fun submit(
        enableAltTextCheck: Boolean,
        enableParentVisibilityCheck: Boolean,
    ) {
        val currentState = uiState.value
        if (currentState.loading) {
            return
        }

        val spoiler = currentState.spoilerValue.text.takeIf { currentState.hasSpoiler }
        val title = currentState.titleValue.text.takeIf { it.isNotBlank() && currentState.hasTitle }
        val text = currentState.bodyValue.text
        val poll = currentState.poll
        val attachmentIds =
            currentState.attachments.map {
                if (shouldUserPhotoRepository) {
                    // use the mediaId for this call otherwise the backend returns a 500
                    it.mediaId
                } else {
                    // otherwise the plain ID should be used
                    it.id
                }
            }
        val visibility = currentState.visibility

        val key = getUuid()
        val publicationType = currentState.publicationType

        screenModelScope.launch {
            if (
                !validate(
                    enableAltTextCheck = enableAltTextCheck,
                    enableParentVisibilityCheck = enableParentVisibilityCheck,
                )
            ) {
                return@launch
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
}
