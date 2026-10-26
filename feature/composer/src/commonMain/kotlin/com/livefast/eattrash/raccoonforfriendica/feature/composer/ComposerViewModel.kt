package com.livefast.eattrash.raccoonforfriendica.feature.composer

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.DraftDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostCreatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.epochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromNowToDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toIso8601Timestamp
import com.livefast.eattrash.raccoonforfriendica.core.utils.substituteAllOccurrences
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ContentRegexes
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.QuotePolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.compareTo
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toVisibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.MediaRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledPostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.AttachmentCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.StripMarkupUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.BBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.PrepareForPreviewUseCase
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
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@AssistedInject
class ComposerViewModel(
    @Assisted private val inReplyToId: String?,
    @Assisted private val quotedId: String?,
    private val identityRepository: IdentityRepository,
    private val postRepository: PostRepository,
    private val photoRepository: PhotoRepository,
    private val circlesRepository: CirclesRepository,
    private val nodeInfoRepository: NodeInfoRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val mediaRepository: MediaRepository,
    private val postCache: LocalItemCache<PostModel>,
    private val scheduledPostRepository: ScheduledPostRepository,
    private val draftRepository: DraftRepository,
    private val settingsRepository: SettingsRepository,
    private val emojiRepository: EmojiRepository,
    private val userRepository: UserRepository,
    private val searchRepository: SearchRepository,
    private val prepareForPreview: PrepareForPreviewUseCase,
    private val stripMarkup: StripMarkupUseCase,
    private val bbCodeConverter: BBCodeConverter,
    private val notificationCenter: NotificationCenter,
    private val attachmentCache: AttachmentCache,
) : ViewModel(),
    MviDelegate<ComposerMvi.Intent, ComposerMvi.State, ComposerMvi.Effect>
    by DefaultMviDelegate(initialState = ComposerMvi.State()),
    ComposerMvi {
    private var uploadJobs = mutableMapOf<String, Job>()
    private var editedPostId: String? = null
    private var draftId: String? = null
    private var mentionSuggestionJob: Job? = null
    private var hashtagSuggestionJob: Job? = null
    private var newAttachmentIds = mutableListOf<String>()

    private val shouldUserPhotoRepository: Boolean by lazy {
        supportedFeatureRepository.features.value.supportsPhotoGallery
    }

    init {
        viewModelScope.launch {
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
                            inlineImagesSupported = features.supportsInlineImages,
                            quotePoliciesSupported = features.supportsQuotePolicies,
                            availableVisibilities =
                            buildList {
                                this += Visibility.Public
                                if (features.supportsLocalVisibility) {
                                    this += Visibility.LocalPublic
                                }
                                this += Visibility.Unlisted
                                if (features.supportsLocalVisibility) {
                                    this += Visibility.LocalUnlisted
                                }
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
            val parent = inReplyToId?.let { id -> postCache.get(id) }
            val initialVisibility =
                when {
                    editedPostId != null -> uiState.value.visibility

                    parent != null ->
                        currentSettings
                            ?.defaultReplyVisibility
                            ?.toVisibility()
                            // make sure to have a visibility less than or equal to the parent
                            ?.takeIf { it <= (parent.visibility) }
                            ?: parent.visibility

                    else -> currentSettings?.defaultPostVisibility?.toVisibility()
                } ?: Visibility.Unlisted
            val quoted = quotedId?.let { id -> postCache.get(id) ?: PostModel(id = id, content = "") }
            val initialQuotePolicy = userRepository.getCurrent()?.quotePolicy ?: QuotePolicy.Public

            updateState {
                it.copy(
                    characterLimit = nodeInfo?.characterLimit,
                    attachmentLimit = nodeInfo?.attachmentLimit,
                    visibility = initialVisibility,
                    inReplyTo = parent,
                    quoted = quoted,
                    markupMode = currentSettings?.markupMode ?: MarkupMode.PlainText,
                    quotePolicy = initialQuotePolicy,
                )
            }

            val customEmojis = emojiRepository.getAll().orEmpty()
            updateState {
                it.copy(availableEmojis = customEmojis)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        uploadJobs.forEach { job ->
            job.value.cancel()
        }
        uploadJobs.clear()
    }

    override fun reduce(intent: ComposerMvi.Intent) {
        when (intent) {
            is ComposerMvi.Intent.LoadEditedPost -> handleLoadEditedPost(intent.id)

            is ComposerMvi.Intent.AddShareUrl -> handleAddShareUrl(intent.url)

            is ComposerMvi.Intent.LoadScheduled -> handleLoadScheduled(intent.id)

            is ComposerMvi.Intent.LoadDraft -> handleLoadDraft(intent.id)

            is ComposerMvi.Intent.SetFieldValue -> handleSetFieldValue(type = intent.fieldType, value = intent.value)

            ComposerMvi.Intent.ToggleHasSpoiler -> handleToggleHasSpoiler()

            ComposerMvi.Intent.ToggleHasTitle -> handleToggleHasTitle()

            is ComposerMvi.Intent.SetVisibility -> handleSetVisibility(intent.visibility)

            ComposerMvi.Intent.AddInitialAttachment -> handleAddInitialAttachment()

            is ComposerMvi.Intent.AddAttachment -> handleAddAttachment(intent.byteArray)

            is ComposerMvi.Intent.EditAttachmentDescription -> handleEditAttachmentDescription(
                attachment = intent.attachment,
                description = intent.description,
            )

            is ComposerMvi.Intent.RemoveAttachment -> handleRemoveAttachment(intent.attachment)

            is ComposerMvi.Intent.AddLink -> handleAddLink(anchor = intent.link.anchor, url = intent.link.url)

            is ComposerMvi.Intent.AddMention -> handleAddMention(intent.handle)

            is ComposerMvi.Intent.CompleteMention -> handleCompleteMention(intent.handle)

            is ComposerMvi.Intent.CompleteHashtag -> handleCompleteHashtag(intent.name)

            is ComposerMvi.Intent.AddInitialMentions -> handleAddInitialMentions(intent.initialHandle)

            is ComposerMvi.Intent.AddGroupReference -> handleAddGroupReference(intent.handle)

            is ComposerMvi.Intent.SetSensitive -> handleSetSensitive(intent.sensitive)

            is ComposerMvi.Intent.AddBoldFormat -> handleAddBoldFormat(intent.fieldType)

            is ComposerMvi.Intent.AddItalicFormat -> handleAddItalicFormat(intent.fieldType)

            is ComposerMvi.Intent.AddUnderlineFormat -> handleAddUnderlineFormat(intent.fieldType)

            is ComposerMvi.Intent.AddStrikethroughFormat -> handleAddStrikethroughFormat(intent.fieldType)

            is ComposerMvi.Intent.AddCodeFormat -> handleAddCodeFormat(intent.fieldType)

            is ComposerMvi.Intent.AddAttachmentsFromGallery -> handleAddAttachmentsFromGallery(intent.attachments)

            is ComposerMvi.Intent.ChangePublicationType -> handleChangePublicationType(intent.type)

            ComposerMvi.Intent.AddPoll -> handleAddPoll()

            is ComposerMvi.Intent.SetPollMultiple -> handleSetPollMultiple(intent.multiple)

            is ComposerMvi.Intent.SetPollExpirationDate -> handleSetPollExpirationDate(intent.date)

            is ComposerMvi.Intent.AddPollOption -> handleAddPollOption()

            is ComposerMvi.Intent.RemovePollOption -> handleRemovePollOption(intent.index)

            is ComposerMvi.Intent.EditPollOption -> handleEditPollOption(index = intent.index, title = intent.title)

            ComposerMvi.Intent.RemovePoll -> handleRemovePoll()

            is ComposerMvi.Intent.InsertCustomEmoji -> handleInsertCustomEmoji(
                fieldType = intent.fieldType,
                emoji = intent.emoji,
            )

            ComposerMvi.Intent.CreatePreview -> handleCreatePreview()

            is ComposerMvi.Intent.InsertList -> handleInsertList()

            is ComposerMvi.Intent.Submit -> handleSubmit(
                enableAltTextCheck = intent.enableAltTextCheck,
                enableParentVisibilityCheck = intent.enableParentVisibilityCheck,
            )

            is ComposerMvi.Intent.ChangeMarkupMode -> handleChangeMarkupMode(intent.mode)

            is ComposerMvi.Intent.AddInlineImageStep1 -> handleAddInlineImageStep1(intent.byteArray)

            is ComposerMvi.Intent.AddInlineImageStep2 -> handleAddInlineImageStep2(
                intent.attachment,
                intent.description,
            )

            is ComposerMvi.Intent.ChangeQuotePolicy -> handleChangeQuotePolicy(intent.policy)
        }
    }

    private fun handleLoadScheduled(id: String) {
        viewModelScope.launch {
            editedPostId = id
            val post = postCache.get(id)
            if (post != null) {
                post.scheduled?.also { scheduleDate ->
                    updateState {
                        it.copy(publicationType = PublicationType.Scheduled(scheduleDate))
                    }
                }
                loadPost(post)
            }
        }
    }

    private fun handleLoadDraft(id: String) {
        viewModelScope.launch {
            draftId = id
            val post = postCache.get(id)
            if (post != null) {
                updateState {
                    it.copy(publicationType = PublicationType.Draft)
                }
                loadPost(post)
            }
        }
    }

    private fun handleSetFieldValue(type: ComposerFieldType, value: TextFieldValue) {
        viewModelScope.launch {
            when (type) {
                ComposerFieldType.Body ->
                    updateBody(value)

                ComposerFieldType.Spoiler ->
                    updateState {
                        it.copy(
                            spoilerValue = value,
                            hasUnsavedChanges = true,
                        )
                    }

                ComposerFieldType.Title ->
                    updateState {
                        it.copy(
                            titleValue = value,
                            hasUnsavedChanges = true,
                        )
                    }
            }
        }
    }

    private fun handleToggleHasSpoiler() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    hasSpoiler = !it.hasSpoiler,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleToggleHasTitle() {
        viewModelScope.launch {
            updateState { it.copy(hasTitle = !it.hasTitle) }
        }
    }

    private fun handleSetVisibility(visibility: Visibility) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    visibility = visibility,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleAddInitialAttachment() {
        viewModelScope.launch {
            val attachmentBytes = attachmentCache.get()
            attachmentCache.clear()
            if (attachmentBytes != null) {
                uploadAttachment(
                    byteArray = attachmentBytes,
                    isInlineImage = false,
                )
            }
        }
    }

    private fun handleAddAttachment(byteArray: ByteArray) {
        uploadAttachment(
            byteArray = byteArray,
            isInlineImage = false,
        )
    }

    private fun handleAddInitialMentions(initialHandle: String?) {
        viewModelScope.launch {
            val mentions =
                buildList {
                    val currentUserHandle =
                        identityRepository.currentUser.value
                            ?.handle
                            .orEmpty()
                    val initialValue = initialHandle.orEmpty()
                    if (initialValue.isNotEmpty() && initialValue != currentUserHandle) {
                        this += initialValue
                    }
                    val mentions =
                        inReplyToId
                            ?.let { postCache.get(it) }
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
    }

    private fun handleAddMention(handle: String) {
        addMention(handle = handle)
    }

    private fun handleAddGroupReference(handle: String) {
        addMention(handle = handle)
    }

    private fun handleSetSensitive(sensitive: Boolean) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    sensitive = sensitive,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleChangePublicationType(type: PublicationType) {
        viewModelScope.launch {
            updateState { it.copy(publicationType = type) }
        }
    }

    private fun handleAddPoll() {
        viewModelScope.launch {
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
    }

    private fun handleSetPollMultiple(multiple: Boolean) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    poll = it.poll?.copy(multiple = multiple),
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleSetPollExpirationDate(date: String) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    poll = it.poll?.copy(expiresAt = date),
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleAddPollOption() {
        viewModelScope.launch {
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
    }

    private fun handleRemovePollOption(index: Int) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    poll =
                    it.poll?.let { p ->
                        p.copy(
                            options = p.options.filterIndexed { idx, _ -> idx != index },
                        )
                    },
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleEditPollOption(index: Int, title: String) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    poll =
                    it.poll?.let { p ->
                        p.copy(
                            options =
                            p.options.mapIndexed { idx, option ->
                                if (idx != index) {
                                    option
                                } else {
                                    option.copy(title = title)
                                }
                            },
                        )
                    },
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleRemovePoll() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    poll = null,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun handleChangeQuotePolicy(policy: QuotePolicy) {
        viewModelScope.launch {
            updateState { it.copy(quotePolicy = policy) }
        }
    }

    private suspend fun updateBody(value: TextFieldValue) {
        val wasShowingMentionSuggestions = uiState.value.shouldShowMentionSuggestions
        val wasShowingHashtagSuggestions = uiState.value.shouldShowHashtagSuggestions
        val currentText = value.text
        val currentPosition =
            if (value.selection.collapsed) {
                value.selection.start - 1
            } else {
                null
            }

        val mentionMatches = ContentRegexes.USER_MENTION.findAll(currentText).toList()
        val currentMention = mentionMatches.firstOrNull { it.range.contains(currentPosition) }
        val shouldShowMentionSuggestions = currentMention != null
        if (currentMention != null) {
            val handlePrefix = currentMention.groups["handlePrefix"]?.value.orEmpty()
            refreshMentionSuggestions(handlePrefix)
        }

        val hashtagMatches = ContentRegexes.HASHTAG.findAll(currentText).toList()
        val currentHashtag = hashtagMatches.firstOrNull { it.range.contains(currentPosition) }
        val shouldShowHashtagSuggestions = currentHashtag != null
        if (currentHashtag != null) {
            val hashtagPrefix = currentHashtag.groups["hashtag"]?.value.orEmpty()
            refreshHashtagSuggestions(hashtagPrefix)
        }

        updateState {
            it.copy(
                bodyValue = value,
                hasUnsavedChanges = true,
                shouldShowMentionSuggestions = shouldShowMentionSuggestions,
                mentionSuggestions =
                if (shouldShowMentionSuggestions && !wasShowingMentionSuggestions) {
                    emptyList()
                } else {
                    it.mentionSuggestions
                },
                shouldShowHashtagSuggestions = shouldShowHashtagSuggestions,
                hashtagSuggestions =
                if (shouldShowHashtagSuggestions && !wasShowingHashtagSuggestions) {
                    emptyList()
                } else {
                    it.hashtagSuggestions
                },
            )
        }
    }

    private suspend fun refreshMentionSuggestions(prefix: String) {
        updateState { it.copy(mentionSuggestionsLoading = true) }
        if (prefix.isNotEmpty()) {
            mentionSuggestionJob?.cancel()
            mentionSuggestionJob =
                viewModelScope.launch {
                    launch {
                        delay(SUGGESTION_DELAY)
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

    private suspend fun refreshHashtagSuggestions(prefix: String) {
        updateState { it.copy(mentionSuggestionsLoading = true) }
        if (prefix.isNotEmpty()) {
            hashtagSuggestionJob?.cancel()
            hashtagSuggestionJob =
                viewModelScope.launch {
                    launch {
                        delay(SUGGESTION_DELAY)
                        val hashtags =
                            searchRepository
                                .search(
                                    query = prefix,
                                    type = SearchResultType.Hashtags,
                                )?.filterIsInstance<ExploreItemModel.HashTag>()
                                ?.map { it.hashtag }
                                .orEmpty()
                        updateState {
                            it.copy(
                                hashtagSuggestions = hashtags,
                                hashtagSuggestionsLoading = hashtags.isEmpty(),
                            )
                        }
                        mentionSuggestionJob = null
                    }
                }
        }
    }

    private fun handleAddAttachmentsFromGallery(attachments: List<AttachmentModel>) {
        viewModelScope.launch {
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

    private fun handleAddLink(anchor: String, url: String) {
        val markupMode = uiState.value.markupMode
        viewModelScope.launch {
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

    private fun addMention(handle: String, privateToGroup: Boolean = false) {
        viewModelScope.launch {
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

    private fun handleCompleteMention(handle: String) {
        viewModelScope.launch {
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
                ContentRegexes.USER_MENTION.substituteAllOccurrences(text) { match ->
                    val isCurrentOccurrence = match.range.contains(currentPosition)
                    if (!isCurrentOccurrence) {
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

    private fun handleCompleteHashtag(name: String) {
        viewModelScope.launch {
            val additionalPart =
                buildString {
                    append("#")
                    append(name)
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
                ContentRegexes.HASHTAG.substituteAllOccurrences(text) { match ->
                    val isCurrentOccurrence = match.range.contains(currentPosition)
                    if (!isCurrentOccurrence) {
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
            hashtagSuggestionJob?.cancel()
            hashtagSuggestionJob = null
            updateState {
                it.copy(
                    bodyValue = newValue,
                    hasUnsavedChanges = true,
                    shouldShowMentionSuggestions = false,
                )
            }
        }
    }

    private fun handleAddShareUrl(url: String) {
        // share via URL is only supported on Friendica
        if (supportedFeatureRepository.features.value.supportsEntryShare) {
            viewModelScope.launch {
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
    }

    private fun handleAddBoldFormat(fieldType: ComposerFieldType) {
        val markupMode = uiState.value.markupMode
        viewModelScope.launch {
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

    private fun handleAddItalicFormat(fieldType: ComposerFieldType) {
        val markupMode = uiState.value.markupMode
        viewModelScope.launch {
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
                    MarkupMode.Markdown -> "*"
                    else -> ""
                }
            val after =
                when (markupMode) {
                    MarkupMode.HTML -> "</i>"
                    MarkupMode.BBCode -> "[/i]"
                    MarkupMode.Markdown -> "*"
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

    private fun handleAddUnderlineFormat(fieldType: ComposerFieldType) {
        val markupMode = uiState.value.markupMode
        viewModelScope.launch {
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

    private fun handleAddStrikethroughFormat(fieldType: ComposerFieldType) {
        val markupMode = uiState.value.markupMode
        viewModelScope.launch {
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

    private fun handleAddCodeFormat(fieldType: ComposerFieldType) {
        val markupMode = uiState.value.markupMode
        viewModelScope.launch {
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

    private fun handleInsertCustomEmoji(fieldType: ComposerFieldType, emoji: EmojiModel) {
        viewModelScope.launch {
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

    private fun handleInsertList() {
        val markupMode = uiState.value.markupMode
        viewModelScope.launch {
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

    private fun uploadAttachment(byteArray: ByteArray, isInlineImage: Boolean) {
        viewModelScope.launch {
            if (isInlineImage) {
                updateState { it.copy(loading = true) }
            } else {
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
            }
            val attachment =
                if (shouldUserPhotoRepository) {
                    photoRepository.create(byteArray)
                } else {
                    mediaRepository.create(byteArray)
                }
            if (attachment != null) {
                if (isInlineImage) {
                    updateState { it.copy(loading = false) }
                    emitEffect(ComposerMvi.Effect.TriggerInlineImageEdit(attachment))
                } else {
                    newAttachmentIds += attachment.id
                    updateState {
                        it.copy(
                            attachments = it.attachments.filter { a -> a.id != PLACEHOLDER_ID } + attachment,
                            hasUnsavedChanges = true,
                        )
                    }
                    emitEffect(ComposerMvi.Effect.TriggerAttachmentEdit(attachment))
                }
            } else {
                emitEffect(ComposerMvi.Effect.Failure())
            }
        }
    }

    private suspend fun updateAttachmentInState(attachmentId: String, block: (AttachmentModel) -> AttachmentModel) {
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

    private fun handleEditAttachmentDescription(attachment: AttachmentModel, description: String) {
        viewModelScope.launch {
            val successful =
                /*
                 * Before even thinking about "optimizing" this, have a look at:
                 * - https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica/issues/640
                 * - https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica/issues/707
                 *
                 * If you decide to proceed anyway and you fail (most surely), please increment
                 * the counter below as a warning for the next person:
                 *
                 * TOTAL_HOURS_WASTED_HERE: 4
                 */
                if (shouldUserPhotoRepository && newAttachmentIds.contains(attachment.id)) {
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

    private fun handleRemoveAttachment(attachment: AttachmentModel) {
        viewModelScope.launch {
            removeAttachmentFromState(attachment.id)
        }
    }

    private fun getNewTextFieldValue(value: TextFieldValue, additionalPart: String, offsetAfter: Int): TextFieldValue {
        val text = value.text
        val selection = value.selection
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

    private fun handleLoadEditedPost(id: String) {
        viewModelScope.launch {
            editedPostId = id
            updateState { it.copy(loading = true) }
            val post = postRepository.getById(id)
            if (post != null) {
                loadPost(post)
            }
        }
    }

    private suspend fun loadPost(post: PostModel) {
        // workaround to make sure Friendica-specific circle visibility is preserved
        val visibility =
            post.visibility.let { visibility ->
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
            post.attachments.map { attachment ->
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
                    val bbCode = bbCodeConverter.fromHtml(post.content)
                    post.copy(content = bbCode)
                }

                // make the server strip off all the HTML
                else -> postRepository.getSource(post.id) ?: post
            }

        updateState {
            it.copy(
                bodyValue = TextFieldValue(reference.content),
                sensitive = post.sensitive,
                attachments = attachments,
                visibility = visibility,
                spoilerValue = TextFieldValue(reference.spoiler.orEmpty()),
                hasSpoiler = !reference.spoiler.isNullOrEmpty(),
                titleValue = TextFieldValue(reference.title.orEmpty()),
                hasTitle = !reference.title.isNullOrEmpty(),
                poll = post.poll,
                loading = false,
            )
        }
    }

    private fun handleCreatePreview() {
        viewModelScope.launch {
            val currentState = uiState.value
            val inReplyTo = inReplyToId?.let { postCache.get(it) }
            val localId = getUuid()
            val post =
                PostModel(
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
            emitEffect(ComposerMvi.Effect.OpenPreview(post))
        }
    }

    private suspend fun validate(enableAltTextCheck: Boolean, enableParentVisibilityCheck: Boolean): Boolean {
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
            emitEffect(ComposerMvi.Effect.ValidationError.TextOrImagesOrPollMandatory)
            return false
        }

        // all images should have a description for a11y
        if (enableAltTextCheck && attachments.any { it.description.isNullOrEmpty() }) {
            emitEffect(ComposerMvi.Effect.ValidationError.AltTextMissing)
            return false
        }

        if (poll != null) {
            // poll must have at least 2 options and an expiration date in the future
            val timeSpan = poll.expiresAt?.let { getDurationFromNowToDate(it) } ?: Duration.ZERO
            val optionsPopulated = poll.options.all { it.title.isNotBlank() }
            if (poll.options.size < 2 || !optionsPopulated || timeSpan <= Duration.ZERO) {
                emitEffect(ComposerMvi.Effect.ValidationError.InvalidPoll)
                return false
            }
        }

        // character limit should not be exceeded
        if (text.length > characterLimit) {
            emitEffect(ComposerMvi.Effect.ValidationError.CharacterLimitExceeded)
            return false
        }

        // Circle visibility should be valid
        if (visibility is Visibility.Circle && visibility.id == null) {
            emitEffect(ComposerMvi.Effect.ValidationError.InvalidVisibility)
            return false
        }

        // Replies should not have higher visibility than parent
        if (enableParentVisibilityCheck &&
            currentState.inReplyTo != null &&
            visibility > currentState.inReplyTo.visibility
        ) {
            emitEffect(ComposerMvi.Effect.ValidationError.VisibilityGreaterThanParent)
            return false
        }

        // schedule date must be in the future
        if (scheduleDate != null) {
            val timeSpan = getDurationFromNowToDate(scheduleDate) ?: Duration.ZERO
            if (timeSpan <= Duration.ZERO) {
                emitEffect(ComposerMvi.Effect.ValidationError.ScheduleDateInThePast)
                return false
            }
        }

        return true
    }

    private fun handleChangeMarkupMode(mode: MarkupMode) {
        val currentState = uiState.value
        val oldMode = currentState.markupMode
        if (mode == oldMode) {
            return
        }

        viewModelScope.launch {
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

    private fun handleAddInlineImageStep1(byteArray: ByteArray) {
        uploadAttachment(
            byteArray = byteArray,
            isInlineImage = true,
        )
    }

    private fun handleAddInlineImageStep2(attachment: AttachmentModel, description: String) {
        viewModelScope.launch {
            val primaryUrl = attachment.url
            val secondaryUrl = attachment.thumbnail ?: attachment.previewUrl ?: primaryUrl
            val additionalPart =
                buildString {
                    append("\n")
                    append("[url=$primaryUrl]")
                    append("[img=$secondaryUrl]")
                    append(description)
                    append("[/img]")
                    append("[/url]")
                    append("\n")
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

    private fun handleSubmit(enableAltTextCheck: Boolean, enableParentVisibilityCheck: Boolean) {
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
        val quotePolicy = currentState.quotePolicy

        viewModelScope.launch {
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
            val defaultQuotePolicy = userRepository.getCurrent()?.quotePolicy
            try {
                val res =
                    when (publicationType) {
                        is PublicationType.Scheduled -> {
                            if (editId != null) {
                                scheduledPostRepository.update(
                                    id = editId,
                                    date = publicationType.date,
                                )
                            } else {
                                postRepository.create(
                                    localId = key,
                                    title = title,
                                    text = text,
                                    inReplyTo = inReplyToId,
                                    quoted = quotedId,
                                    quotePolicy = if (quotePolicy != defaultQuotePolicy) {
                                        quotePolicy
                                    } else {
                                        null
                                    },
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
                                postRepository.update(
                                    id = editId,
                                    title = title,
                                    text = text,
                                    inReplyTo = inReplyToId,
                                    quoted = quotedId,
                                    quotePolicy = if (quotePolicy != defaultQuotePolicy) {
                                        quotePolicy
                                    } else {
                                        null
                                    },
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
                                postRepository.create(
                                    localId = key,
                                    title = title,
                                    text = text,
                                    inReplyTo = inReplyToId,
                                    quoted = quotedId,
                                    quotePolicy = if (quotePolicy != defaultQuotePolicy) {
                                        quotePolicy
                                    } else {
                                        null
                                    },
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
                            val post =
                                PostModel(
                                    id = editId ?: key,
                                    content = text,
                                    title = title,
                                    updated = epochMillis().toIso8601Timestamp(withLocalTimezone = false),
                                    spoiler = spoiler,
                                    sensitive = currentState.sensitive,
                                    visibility = visibility,
                                    parentId = inReplyToId,
                                    quoted = quotedId?.let { PostModel(id = it, content = "") },
                                    lang = currentState.lang,
                                    attachments =
                                    attachmentIds.map {
                                        AttachmentModel(id = it, url = "")
                                    },
                                    poll = poll,
                                )
                            if (draftId != null) {
                                draftRepository.update(post)
                            } else {
                                draftRepository.create(post)
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
                                notificationCenter.send(PostUpdatedEvent(res))
                            } else {
                                notificationCenter.send(PostCreatedEvent(res))
                            }
                        }

                        PublicationType.Draft -> {
                            if (draftId != null) {
                                notificationCenter.send(PostUpdatedEvent(res))
                            } else {
                                notificationCenter.send(PostCreatedEvent(res))
                            }
                        }

                        PublicationType.Default -> {
                            val draftToDeleteId = draftId
                            if (draftToDeleteId != null) {
                                draftRepository.delete(draftToDeleteId)
                                notificationCenter.send(DraftDeletedEvent(draftToDeleteId))
                            }

                            if (editId != null) {
                                notificationCenter.send(PostUpdatedEvent(res))
                            } else {
                                notificationCenter.send(PostCreatedEvent(res))
                            }
                        }
                    }

                    emitEffect(ComposerMvi.Effect.Success)
                } else {
                    emitEffect(ComposerMvi.Effect.Failure(null))
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                updateState { it.copy(loading = false) }
                emitEffect(ComposerMvi.Effect.Failure(message = e.message))
            }
        }
    }

    companion object {
        fun getExtras(inReplyToId: String?, quotedId: String?) = CreationExtras {
            this[KEY_IN_REPLY_TO_ID] = inReplyToId
            this[KEY_QUOTED_ID] = quotedId
        }

        private const val PLACEHOLDER_ID = "placeholder"
        private val SUGGESTION_DELAY = 750.milliseconds
    }
}

private val KEY_IN_REPLY_TO_ID = CreationExtras.Key<String?>()
private val KEY_QUOTED_ID = CreationExtras.Key<String?>()

@AssistedFactory
@ViewModelAssistedFactoryKey(ComposerViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface ComposerViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ComposerViewModel = create(
        inReplyToId = extras[KEY_IN_REPLY_TO_ID],
        quotedId = extras[KEY_QUOTED_ID],
    )

    fun create(@Assisted inReplyToId: String?, @Assisted quotedId: String?): ComposerViewModel
}
