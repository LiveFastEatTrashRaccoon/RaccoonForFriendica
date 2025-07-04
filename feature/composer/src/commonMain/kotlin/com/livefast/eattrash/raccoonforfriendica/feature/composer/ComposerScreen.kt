package com.livefast.eattrash.raccoonforfriendica.feature.composer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.EditAttachmentDescriptionDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.InsertEmojiBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SpoilerTextField
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.epochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toEpochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di.getAttachmentCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.AttachmentsGrid
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreateInGroupInfo
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreatePostHeader
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreatePostSubHeader
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.DateTimeSelectionFlow
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.EntryPreviewDialog
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.GalleryPickerDialog
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.InReplyToInfo
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.InsertLinkDialog
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.PollForm
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.SuggestionsBar
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.UtilsBar
import com.livefast.eattrash.raccoonforfriendica.feature.composer.di.ComposerViewModelArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ComposerScreen(
    modifier: Modifier = Modifier,
    inReplyToId: String? = null,
    inReplyToUsername: String? = null,
    inReplyToHandle: String? = null,
    groupUsername: String? = null,
    groupHandle: String? = null,
    editedPostId: String? = null,
    scheduledPostId: String? = null,
    draftId: String? = null,
    urlToShare: String? = null,
    initialText: String? = null,
) {
    val model: ComposerMviModel = getViewModel<ComposerViewModel>(arg = ComposerViewModelArgs(inReplyToId.orEmpty()))
    val uiState by model.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val snackbarHostState = remember { SnackbarHostState() }
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val galleryHelper = remember { getGalleryHelper() }
    val attachmentCache = remember { getAttachmentCache() }
    val focusManager = LocalFocusManager.current
    val missingDataError = LocalStrings.current.messagePostEmptyText
    val invalidVisibilityError = LocalStrings.current.messagePostInvalidVisibility
    val characterLimitExceededError = LocalStrings.current.messageCharacterLimitExceeded
    val pastScheduleDateError = LocalStrings.current.messageScheduleDateInThePast
    val invalidPollError = LocalStrings.current.messageInvalidPollError
    val genericError = LocalStrings.current.messageGenericError
    var imagePickerRequest by remember { mutableStateOf<ImagePickerRequest?>(null) }
    if (imagePickerRequest != null) {
        galleryHelper.getImageFromGallery { bytes ->
            val originalRequest = imagePickerRequest
            imagePickerRequest = null
            if (bytes.isNotEmpty()) {
                when (originalRequest) {
                    ImagePickerRequest.Attachment ->
                        model.reduce(ComposerMviModel.Intent.AddAttachment(bytes))

                    ImagePickerRequest.InlineImage ->
                        model.reduce(ComposerMviModel.Intent.AddInlineImageStep1(bytes))

                    else -> Unit
                }
            }
        }
    }
    var photoGalleryPickerOpen by remember { mutableStateOf(false) }
    var linkDialogOpen by remember { mutableStateOf(false) }
    var selectCircleDialogOpen by remember { mutableStateOf(false) }
    var attachmentBeingEdited by remember { mutableStateOf<AttachmentModel?>(null) }
    var inlineImageBeingEdited by remember { mutableStateOf<AttachmentModel?>(null) }
    var hasSpoilerFieldFocus by remember { mutableStateOf(false) }
    var hasTitleFocus by remember { mutableStateOf(false) }
    val isBeingEdited = remember { scheduledPostId != null || editedPostId != null }
    var scheduleDateMillis by remember { mutableStateOf<Long?>(null) }
    var scheduleDatePickerOpen by remember { mutableStateOf(false) }
    var pollExpirationMillis by remember { mutableStateOf<Long?>(null) }
    var pollExpirationDatePickerOpen by remember { mutableStateOf(false) }
    var insertEmojiModalOpen by remember { mutableStateOf(false) }
    var previewEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
    var confirmBackWithUnsavedChangesDialogOpen by remember { mutableStateOf(false) }
    var publishWithoutAltTextCheckDialogOpen by remember { mutableStateOf(false) }
    val scheduleDate =
        when (val type = uiState.publicationType) {
            is PublicationType.Scheduled -> type.date
            else -> null
        }
    var confirmPublishWithVisibilityGreaterThanParentDialogOpened by remember {
        mutableStateOf(false)
    }
    var confirmChangeMarkupModeDialogOpen by remember { mutableStateOf(false) }
    var changeMarkupModeBottomSheetOpened by remember { mutableStateOf(false) }

    LaunchedEffect(model) {
        val initialAttachment = attachmentCache.get()
        attachmentCache.clear()

        when {
            draftId != null ->
                model.reduce(ComposerMviModel.Intent.LoadDraft(draftId))

            scheduledPostId != null ->
                model.reduce(ComposerMviModel.Intent.LoadScheduled(scheduledPostId))

            editedPostId != null ->
                model.reduce(ComposerMviModel.Intent.LoadEditedPost(editedPostId))

            !groupHandle.isNullOrEmpty() ->
                model.reduce(ComposerMviModel.Intent.AddGroupReference(groupHandle))

            !urlToShare.isNullOrEmpty() ->
                model.reduce(ComposerMviModel.Intent.AddShareUrl(urlToShare))

            !initialText.isNullOrEmpty() ->
                model.reduce(
                    ComposerMviModel.Intent.SetFieldValue(
                        value = TextFieldValue(text = initialText),
                        fieldType = ComposerFieldType.Body,
                    ),
                )

            initialAttachment != null ->
                model.reduce(ComposerMviModel.Intent.AddAttachment(initialAttachment))

            inReplyToId != null ->
                model.reduce(
                    ComposerMviModel.Intent.AddInitialMentions(initialHandle = inReplyToHandle),
                )

            !inReplyToHandle.isNullOrEmpty() ->
                model.reduce(ComposerMviModel.Intent.AddMention(inReplyToHandle))

            else -> Unit
        }
    }

    LaunchedEffect(model) {
        model.effects.onEach { event ->
            when (event) {
                is ComposerMviModel.Effect.Failure ->
                    snackbarHostState.showSnackbar(message = event.message ?: genericError)

                ComposerMviModel.Effect.ValidationError.TextOrImagesOrPollMandatory ->
                    snackbarHostState.showSnackbar(message = missingDataError)

                ComposerMviModel.Effect.ValidationError.InvalidVisibility ->
                    snackbarHostState.showSnackbar(message = invalidVisibilityError)

                ComposerMviModel.Effect.ValidationError.CharacterLimitExceeded ->
                    snackbarHostState.showSnackbar(message = characterLimitExceededError)

                ComposerMviModel.Effect.ValidationError.ScheduleDateInThePast ->
                    snackbarHostState.showSnackbar(message = pastScheduleDateError)

                ComposerMviModel.Effect.ValidationError.InvalidPoll ->
                    snackbarHostState.showSnackbar(message = invalidPollError)

                ComposerMviModel.Effect.ValidationError.AltTextMissing ->
                    publishWithoutAltTextCheckDialogOpen = true

                ComposerMviModel.Effect.ValidationError.VisibilityGreaterThanParent ->
                    confirmPublishWithVisibilityGreaterThanParentDialogOpened = true

                ComposerMviModel.Effect.Success -> navigationCoordinator.pop()

                is ComposerMviModel.Effect.OpenPreview -> previewEntry = event.entry
                is ComposerMviModel.Effect.TriggerAttachmentEdit ->
                    attachmentBeingEdited = event.attachment

                is ComposerMviModel.Effect.TriggerInlineImageEdit ->
                    inlineImageBeingEdited = event.attachment
            }
        }.launchIn(this)
    }

    PredictiveBackHandler(enabled = uiState.hasUnsavedChanges) {
        if (uiState.hasUnsavedChanges) {
            confirmBackWithUnsavedChangesDialogOpen = true
        }
    }

    Scaffold(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .navigationBarsPadding()
            .safeImePadding(),
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = LocalStrings.current.createPostTitle,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    if (navigationCoordinator.canPop.value) {
                        IconButton(
                            onClick = {
                                if (uiState.hasUnsavedChanges) {
                                    confirmBackWithUnsavedChangesDialogOpen = true
                                } else {
                                    navigationCoordinator.pop()
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = LocalStrings.current.actionGoBack,
                            )
                        }
                    }
                },
                actions = {
                    val options =
                        buildList {
                            when (uiState.publicationType) {
                                is PublicationType.Scheduled -> {
                                    this +=
                                        CustomOptions.SaveDraft.toOption(
                                            label = LocalStrings.current.actionSaveDraft,
                                        )
                                    this +=
                                        CustomOptions.ChangeSchedule.toOption(
                                            label = LocalStrings.current.actionUpdateScheduleDate,
                                        )
                                    if (!isBeingEdited) {
                                        this +=
                                            CustomOptions.PublishDefault.toOption(
                                                label = LocalStrings.current.actionPublishDefault,
                                            )
                                    }
                                }

                                PublicationType.Draft -> {
                                    this +=
                                        CustomOptions.SetSchedule.toOption(
                                            label = LocalStrings.current.actionSetScheduleDate,
                                        )
                                    this +=
                                        CustomOptions.PublishDefault.toOption(
                                            label = LocalStrings.current.actionPublishDefault,
                                        )
                                }

                                PublicationType.Default -> {
                                    this +=
                                        CustomOptions.SaveDraft.toOption(
                                            label = LocalStrings.current.actionSaveDraft,
                                        )
                                    this +=
                                        CustomOptions.SetSchedule.toOption(
                                            label = LocalStrings.current.actionSetScheduleDate,
                                        )
                                }
                            }

                            if (uiState.availableEmojis.isNotEmpty()) {
                                this +=
                                    CustomOptions.InsertCustomEmoji.toOption(
                                        label = LocalStrings.current.insertEmojiTitle,
                                    )
                            }

                            if (uiState.supportsRichEditing) {
                                this +=
                                    CustomOptions.OpenPreview.toOption(
                                        label = LocalStrings.current.actionOpenPreview,
                                    )
                            }

                            this +=
                                CustomOptions.ToggleSpoiler.toOption(
                                    label =
                                    if (uiState.hasSpoiler) {
                                        LocalStrings.current.actionRemoveSpoiler
                                    } else {
                                        LocalStrings.current.actionAddSpoiler
                                    },
                                )

                            if (uiState.titleFeatureSupported) {
                                this +=
                                    CustomOptions.ToggleTitle.toOption(
                                        label =
                                        if (uiState.hasTitle) {
                                            LocalStrings.current.actionRemoveTitle
                                        } else {
                                            LocalStrings.current.actionAddTitle
                                        },
                                    )
                            }

                            if (uiState.pollFeatureSupported && uiState.attachments.isEmpty()) {
                                this +=
                                    CustomOptions.TogglePoll.toOption(
                                        label =
                                        if (uiState.poll != null) {
                                            LocalStrings.current.actionRemovePoll
                                        } else {
                                            LocalStrings.current.actionAddPoll
                                        },
                                    )
                            }

                            if (uiState.supportsRichEditing) {
                                this +=
                                    CustomOptions.InsertList.toOption(
                                        label = LocalStrings.current.actionInsertList,
                                    )
                            }

                            this +=
                                CustomOptions.ChangeMarkupMode.toOption(
                                    label = LocalStrings.current.actionChangeMarkupMode,
                                )
                        }
                    Box {
                        var optionsOffset by remember { mutableStateOf(Offset.Zero) }
                        var optionsMenuOpen by remember { mutableStateOf(false) }
                        IconButton(
                            modifier =
                            Modifier.onGloballyPositioned {
                                optionsOffset = it.positionInParent()
                            },
                            onClick = {
                                optionsMenuOpen = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = LocalStrings.current.actionOpenOptions,
                            )
                        }

                        CustomDropDown(
                            expanded = optionsMenuOpen,
                            onDismiss = {
                                optionsMenuOpen = false
                            },
                            offset =
                            with(LocalDensity.current) {
                                DpOffset(
                                    x = optionsOffset.x.toDp(),
                                    y = optionsOffset.y.toDp(),
                                )
                            },
                        ) {
                            for (option in options) {
                                DropdownMenuItem(
                                    text = {
                                        Text(option.label)
                                    },
                                    onClick = {
                                        optionsMenuOpen = false
                                        when (option.id) {
                                            CustomOptions.SetSchedule -> {
                                                scheduleDateMillis = epochMillis()
                                                scheduleDatePickerOpen = true
                                            }

                                            CustomOptions.ChangeSchedule -> {
                                                scheduleDateMillis =
                                                    (uiState.publicationType as? PublicationType.Scheduled)
                                                        ?.date
                                                        ?.toEpochMillis()
                                                scheduleDatePickerOpen = true
                                            }

                                            CustomOptions.PublishDefault ->
                                                model.reduce(
                                                    ComposerMviModel.Intent.ChangePublicationType(
                                                        PublicationType.Default,
                                                    ),
                                                )

                                            CustomOptions.SaveDraft ->
                                                model.reduce(
                                                    ComposerMviModel.Intent.ChangePublicationType(
                                                        PublicationType.Draft,
                                                    ),
                                                )

                                            CustomOptions.TogglePoll ->
                                                if (uiState.poll == null) {
                                                    model.reduce(ComposerMviModel.Intent.AddPoll)
                                                } else {
                                                    model.reduce(ComposerMviModel.Intent.RemovePoll)
                                                }

                                            CustomOptions.ToggleTitle ->
                                                model.reduce(ComposerMviModel.Intent.ToggleHasTitle)

                                            CustomOptions.ToggleSpoiler ->
                                                model.reduce(ComposerMviModel.Intent.ToggleHasSpoiler)

                                            CustomOptions.InsertCustomEmoji ->
                                                insertEmojiModalOpen = true

                                            CustomOptions.OpenPreview ->
                                                model.reduce(ComposerMviModel.Intent.CreatePreview)

                                            CustomOptions.InsertList ->
                                                model.reduce(ComposerMviModel.Intent.InsertList)

                                            CustomOptions.ChangeMarkupMode ->
                                                confirmChangeMarkupModeDialogOpen = true

                                            else -> Unit
                                        }
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            ) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    snackbarData = data,
                )
            }
        },
        bottomBar = {
            Column(
                modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = Spacing.xs),
            ) {
                if (uiState.shouldShowMentionSuggestions) {
                    val users = uiState.mentionSuggestions.filter { !it.handle.isNullOrBlank() }
                    SuggestionsBar(
                        suggestions = users.map { it.handle.orEmpty() },
                        loading = uiState.mentionSuggestionsLoading,
                        loadingMessage = LocalStrings.current.messageLoadingUsers,
                        onSelect = { idx ->
                            users[idx].handle?.takeIf { it.isNotEmpty() }?.also { handle ->
                                model.reduce(ComposerMviModel.Intent.CompleteMention(handle))
                            }
                        },
                    )
                } else if (uiState.shouldShowHashtagSuggestions) {
                    val hashtags = uiState.hashtagSuggestions.filter { it.name.isNotEmpty() }
                    SuggestionsBar(
                        suggestions = hashtags.map { it.name },
                        loading = uiState.mentionSuggestionsLoading,
                        loadingMessage = LocalStrings.current.messageLoadingHashtags,
                        onSelect = { idx ->
                            hashtags[idx].name.also { name ->
                                model.reduce(ComposerMviModel.Intent.CompleteHashtag(name))
                            }
                        },
                    )
                }
                UtilsBar(
                    modifier = Modifier.fillMaxWidth(),
                    supportsRichEditing = uiState.supportsRichEditing,
                    supportsInlineImages = uiState.inlineImagesSupported,
                    hasPoll = uiState.poll != null,
                    publicationType = uiState.publicationType,
                    onClickLink = {
                        linkDialogOpen = true
                    },
                    onClickAttachment = {
                        val limit = uiState.attachmentLimit ?: Int.MAX_VALUE
                        if (uiState.attachments.size < limit) {
                            imagePickerRequest = ImagePickerRequest.Attachment
                        }
                    },
                    onClickInlineImage = {
                        imagePickerRequest = ImagePickerRequest.InlineImage
                    },
                    onClickBold = {
                        model.reduce(
                            ComposerMviModel.Intent.AddBoldFormat(
                                fieldType =
                                when {
                                    hasTitleFocus -> ComposerFieldType.Title
                                    hasSpoilerFieldFocus -> ComposerFieldType.Spoiler
                                    else -> ComposerFieldType.Body
                                },
                            ),
                        )
                    },
                    onClickItalic = {
                        model.reduce(
                            ComposerMviModel.Intent.AddItalicFormat(
                                fieldType =
                                when {
                                    hasTitleFocus -> ComposerFieldType.Title
                                    hasSpoilerFieldFocus -> ComposerFieldType.Spoiler
                                    else -> ComposerFieldType.Body
                                },
                            ),
                        )
                    },
                    onClickUnderline = {
                        model.reduce(
                            ComposerMviModel.Intent.AddUnderlineFormat(
                                fieldType =
                                when {
                                    hasTitleFocus -> ComposerFieldType.Title
                                    hasSpoilerFieldFocus -> ComposerFieldType.Spoiler
                                    else -> ComposerFieldType.Body
                                },
                            ),
                        )
                    },
                    onClickStrikethrough = {
                        model.reduce(
                            ComposerMviModel.Intent.AddStrikethroughFormat(
                                fieldType =
                                when {
                                    hasTitleFocus -> ComposerFieldType.Title
                                    hasSpoilerFieldFocus -> ComposerFieldType.Spoiler
                                    else -> ComposerFieldType.Body
                                },
                            ),
                        )
                    },
                    onClickCode = {
                        model.reduce(
                            ComposerMviModel.Intent.AddCodeFormat(
                                fieldType =
                                when {
                                    hasTitleFocus -> ComposerFieldType.Title
                                    hasSpoilerFieldFocus -> ComposerFieldType.Spoiler
                                    else -> ComposerFieldType.Body
                                },
                            ),
                        )
                    },
                    onSubmit = {
                        model.reduce(ComposerMviModel.Intent.Submit())
                    },
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
            Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                ).consumeWindowInsets(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            if (inReplyToUsername != null && uiState.inReplyTo != null) {
                InReplyToInfo(
                    modifier =
                    Modifier.padding(
                        start = Spacing.s,
                        end = Spacing.s,
                        bottom = Spacing.m,
                    ),
                    username = inReplyToUsername,
                )
            } else if (!groupUsername.isNullOrBlank()) {
                CreateInGroupInfo(
                    modifier =
                    Modifier.padding(
                        start = Spacing.s,
                        end = Spacing.s,
                        bottom = Spacing.m,
                    ),
                    username = groupUsername,
                )
            }

            CreatePostHeader(
                modifier =
                Modifier.padding(
                    horizontal = Spacing.s,
                    vertical = Spacing.xxs,
                ),
                author = uiState.author,
                autoloadImages = uiState.autoloadImages,
                visibility = uiState.visibility,
                availableVisibilities = uiState.availableVisibilities,
                // visibility change is not possible when editing a post
                changeVisibilityEnabled = !isBeingEdited,
                onChangeVisibility = { visibility ->
                    if (visibility is Visibility.Circle) {
                        selectCircleDialogOpen = true
                    } else {
                        model.reduce(ComposerMviModel.Intent.SetVisibility(visibility))
                    }
                },
            )

            if (uiState.characterLimit != null || scheduleDate != null) {
                CreatePostSubHeader(
                    date = scheduleDate,
                    characters = uiState.bodyValue.text.length,
                    characterLimit = uiState.characterLimit,
                )
            }

            // spoiler text
            if (uiState.hasSpoiler) {
                val fieldHeight =
                    with(LocalDensity.current) {
                        MaterialTheme.typography.titleMedium.lineHeight
                            .toDp() * 2
                    }
                SpoilerTextField(
                    modifier =
                    Modifier
                        .padding(horizontal = Spacing.s, vertical = Spacing.s)
                        .clip(RoundedCornerShape(CornerSize.l))
                        .height(fieldHeight)
                        .onFocusChanged {
                            hasSpoilerFieldFocus = it.hasFocus
                        },
                    hint = LocalStrings.current.createPostSpoilerPlaceholder,
                    value = uiState.spoilerValue,
                    keyboardOptions =
                    KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    keyboardActions =
                    KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                    ),
                    onValueChange = {
                        model.reduce(
                            ComposerMviModel.Intent.SetFieldValue(
                                value = it,
                                fieldType = ComposerFieldType.Spoiler,
                            ),
                        )
                    },
                )
            }

            if (uiState.hasTitle) {
                // post title
                OutlinedTextField(
                    modifier =
                    Modifier
                        .onFocusChanged {
                            hasTitleFocus = it.hasFocus
                        }.padding(top = Spacing.s, start = Spacing.xs, end = Spacing.xs)
                        .fillMaxWidth(),
                    label = {
                        Text(text = LocalStrings.current.createPostTitlePlaceholder)
                    },
                    maxLines = 1,
                    value = uiState.titleValue,
                    textStyle = MaterialTheme.typography.titleLarge,
                    keyboardOptions =
                    KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    keyboardActions =
                    KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                    ),
                    onValueChange = { value ->
                        model.reduce(
                            ComposerMviModel.Intent.SetFieldValue(
                                value = value,
                                fieldType = ComposerFieldType.Title,
                            ),
                        )
                    },
                )
            }

            // post body
            OutlinedTextField(
                modifier =
                Modifier
                    .padding(top = Spacing.s, start = Spacing.xs, end = Spacing.xs)
                    .fillMaxWidth()
                    .height(400.dp),
                placeholder = {
                    Text(text = LocalStrings.current.createPostBodyPlaceholder)
                },
                value = uiState.bodyValue,
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                onValueChange = { value ->
                    model.reduce(
                        ComposerMviModel.Intent.SetFieldValue(
                            value = value,
                            fieldType = ComposerFieldType.Body,
                        ),
                    )
                },
            )

            // sensitive switch
            SettingsSwitchRow(
                title = LocalStrings.current.postSensitive,
                value = uiState.sensitive,
                onValueChange = {
                    model.reduce(ComposerMviModel.Intent.SetSensitive(it))
                },
            )

            // attachments
            if (uiState.attachments.isNotEmpty()) {
                AttachmentsGrid(
                    modifier =
                    Modifier.padding(
                        top = Spacing.s,
                        start = Spacing.s,
                        end = Spacing.s,
                    ),
                    attachments = uiState.attachments,
                    autoloadImages = uiState.autoloadImages,
                    onDelete = { attachment ->
                        model.reduce(ComposerMviModel.Intent.RemoveAttachment(attachment))
                    },
                    onEditDescription = { attachment ->
                        attachmentBeingEdited = attachment
                    },
                )
            }

            // poll
            uiState.poll?.let { poll ->
                PollForm(
                    poll = poll,
                    optionLimit = uiState.pollOptionLimit ?: Int.MAX_VALUE,
                    onChangeMultiple = {
                        model.reduce(ComposerMviModel.Intent.SetPollMultiple(it))
                    },
                    onAddOption = {
                        model.reduce(ComposerMviModel.Intent.AddPollOption)
                    },
                    onEditOption = { idx, text ->
                        model.reduce(ComposerMviModel.Intent.EditPollOption(idx, text))
                    },
                    onRemoveOption = { idx ->
                        model.reduce(ComposerMviModel.Intent.RemovePollOption(idx))
                    },
                    onEditExpirationDate = {
                        pollExpirationMillis =
                            poll.expiresAt?.toEpochMillis() ?: epochMillis()
                        pollExpirationDatePickerOpen = true
                    },
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }

        if (uiState.loading) {
            ProgressHud()
        }

        if (linkDialogOpen) {
            InsertLinkDialog(
                initialAnchor =
                uiState.bodyValue.selection.takeIf { it.length > 0 }?.let { range ->
                    runCatching {
                        uiState.bodyValue.text.substring(range.start, range.end)
                    }.getOrNull()
                },
                onClose = { link ->
                    linkDialogOpen = false
                    if (link != null) {
                        model.reduce(ComposerMviModel.Intent.AddLink(link))
                    }
                },
            )
        }

        val editedAttachment = attachmentBeingEdited
        val editedInlineImage = inlineImageBeingEdited
        if (editedAttachment != null) {
            EditAttachmentDescriptionDialog(
                attachment = editedAttachment,
                onClose = { newValue ->
                    if (newValue != null) {
                        model.reduce(
                            ComposerMviModel.Intent.EditAttachmentDescription(
                                attachment = editedAttachment,
                                description = newValue,
                            ),
                        )
                    }
                    attachmentBeingEdited = null
                },
            )
        } else if (editedInlineImage != null) {
            EditAttachmentDescriptionDialog(
                attachment = editedInlineImage,
                onClose = { newValue ->
                    if (newValue != null) {
                        model.reduce(
                            ComposerMviModel.Intent.AddInlineImageStep2(
                                attachment = editedInlineImage,
                                description = newValue,
                            ),
                        )
                    }
                    inlineImageBeingEdited = null
                },
            )
        }

        if (selectCircleDialogOpen) {
            CustomModalBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                title = LocalStrings.current.selectCircleDialogTitle,
                items =
                uiState.availableCircles.map { value ->
                    CustomModalBottomSheetItem(
                        label = value.name,
                        trailingContent = {
                            Icon(
                                modifier = Modifier.size(IconSize.m),
                                imageVector = value.type.toIcon(),
                                contentDescription = value.type.toReadableName(),
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        },
                    )
                },
                onSelect = { index ->
                    selectCircleDialogOpen = false
                    if (index != null) {
                        val circle = uiState.availableCircles[index]
                        model.reduce(
                            ComposerMviModel.Intent.SetVisibility(
                                Visibility.Circle(
                                    id = circle.id,
                                    name = circle.name,
                                ),
                            ),
                        )
                    }
                },
            )
        }

        if (photoGalleryPickerOpen) {
            GalleryPickerDialog(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                currentAlbum = uiState.galleryCurrentAlbum,
                albums = uiState.galleryAlbums,
                autoloadImages = uiState.autoloadImages,
                canFetchMore = uiState.galleryCanFetchMore,
                loading = uiState.galleryLoading,
                photos = uiState.galleryCurrentAlbumPhotos,
                onInitialLoad = {
                    model.reduce(ComposerMviModel.Intent.GalleryInitialLoad)
                },
                onLoadMorePhotos = {
                    model.reduce(ComposerMviModel.Intent.GalleryLoadMorePhotos)
                },
                onChangeAlbum = { album ->
                    model.reduce(ComposerMviModel.Intent.GalleryAlbumSelected(album))
                },
                onClose = { attachments ->
                    photoGalleryPickerOpen = false
                    if (attachments != null) {
                        model.reduce(ComposerMviModel.Intent.AddAttachmentsFromGallery(attachments))
                    }
                },
            )
        }

        if (scheduleDatePickerOpen) {
            DateTimeSelectionFlow(
                initialDateMillis = scheduleDateMillis ?: epochMillis(),
                onClose = { date ->
                    scheduleDatePickerOpen = false
                    scheduleDateMillis = null
                    if (date != null) {
                        model.reduce(
                            ComposerMviModel.Intent.ChangePublicationType(
                                PublicationType.Scheduled(date),
                            ),
                        )
                    }
                },
            )
        }

        if (pollExpirationDatePickerOpen) {
            DateTimeSelectionFlow(
                initialDateMillis = pollExpirationMillis ?: epochMillis(),
                onClose = { date ->
                    pollExpirationDatePickerOpen = false
                    pollExpirationMillis = null
                    if (date != null) {
                        model.reduce(
                            ComposerMviModel.Intent.SetPollExpirationDate(date),
                        )
                    }
                },
            )
        }

        if (insertEmojiModalOpen) {
            InsertEmojiBottomSheet(
                emojis = uiState.availableEmojis,
                onClose = {
                    insertEmojiModalOpen = false
                },
                onInsert = { emoji ->
                    model.reduce(
                        ComposerMviModel.Intent.InsertCustomEmoji(
                            fieldType =
                            when {
                                hasTitleFocus -> ComposerFieldType.Title
                                hasSpoilerFieldFocus -> ComposerFieldType.Spoiler
                                else -> ComposerFieldType.Body
                            },
                            emoji = emoji,
                        ),
                    )
                },
            )
        }

        if (confirmBackWithUnsavedChangesDialogOpen) {
            CustomConfirmDialog(
                title =
                if (uiState.publicationType == PublicationType.Draft) {
                    LocalStrings.current.unsavedDraftTitle
                } else {
                    LocalStrings.current.unsavedChangesTitle
                },
                body = LocalStrings.current.messageAreYouSureExit,
                confirmButtonLabel =
                if (uiState.publicationType == PublicationType.Draft) {
                    LocalStrings.current.buttonDiscard
                } else {
                    LocalStrings.current.buttonConfirm
                },
                onClose = { confirm ->
                    confirmBackWithUnsavedChangesDialogOpen = false
                    if (confirm) {
                        navigationCoordinator.pop()
                    }
                },
            )
        }

        if (publishWithoutAltTextCheckDialogOpen) {
            CustomConfirmDialog(
                title = LocalStrings.current.dialogErrorTitle,
                body = LocalStrings.current.messageAltTextMissingError,
                confirmButtonLabel = LocalStrings.current.buttonPublishAnyway,
                onClose = { confirm ->
                    publishWithoutAltTextCheckDialogOpen = false
                    if (confirm) {
                        model.reduce(ComposerMviModel.Intent.Submit(enableAltTextCheck = false))
                    }
                },
            )
        }

        if (confirmPublishWithVisibilityGreaterThanParentDialogOpened) {
            CustomConfirmDialog(
                title = LocalStrings.current.dialogErrorTitle,
                body = LocalStrings.current.messageReplyVisibilityGreaterThanParentError,
                confirmButtonLabel = LocalStrings.current.buttonPublishAnyway,
                onClose = { confirm ->
                    confirmPublishWithVisibilityGreaterThanParentDialogOpened = false
                    if (confirm) {
                        model.reduce(
                            ComposerMviModel.Intent.Submit(enableParentVisibilityCheck = false),
                        )
                    }
                },
            )
        }

        previewEntry?.also { entry ->
            EntryPreviewDialog(
                entry = entry,
                autoloadImages = uiState.autoloadImages,
                onDismiss = {
                    previewEntry = null
                },
            )
        }

        if (confirmChangeMarkupModeDialogOpen) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionChangeMarkupMode,
                body = LocalStrings.current.confirmChangeMarkupMode,
                confirmButtonLabel = LocalStrings.current.buttonConfirm,
                onClose = { confirm ->
                    confirmChangeMarkupModeDialogOpen = false
                    if (confirm) {
                        changeMarkupModeBottomSheetOpened = true
                    }
                },
            )
        }

        if (changeMarkupModeBottomSheetOpened) {
            val modes = uiState.availableMarkupModes
            CustomModalBottomSheet(
                title = LocalStrings.current.settingsItemMarkupMode,
                items = modes.map { CustomModalBottomSheetItem(label = it.toReadableName()) },
                onSelect = { index ->
                    changeMarkupModeBottomSheetOpened = false
                    if (index != null) {
                        val mode = modes[index]
                        model.reduce(ComposerMviModel.Intent.ChangeMarkupMode(mode))
                    }
                },
            )
        }
    }
}

private sealed interface CustomOptions : OptionId.Custom {
    data object TogglePoll : CustomOptions

    data object ToggleTitle : CustomOptions

    data object ToggleSpoiler : CustomOptions

    data object InsertCustomEmoji : CustomOptions

    data object OpenPreview : CustomOptions

    data object SaveDraft : CustomOptions

    data object ChangeSchedule : CustomOptions

    data object SetSchedule : CustomOptions

    data object PublishDefault : CustomOptions

    data object InsertList : CustomOptions

    data object ChangeMarkupMode : CustomOptions
}

private sealed interface ImagePickerRequest {
    data object Attachment : ImagePickerRequest

    data object InlineImage : ImagePickerRequest
}
