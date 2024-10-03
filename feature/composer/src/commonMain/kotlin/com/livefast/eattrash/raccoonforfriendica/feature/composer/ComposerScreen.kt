package com.livefast.eattrash.raccoonforfriendica.feature.composer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.EditTextualInfoDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.InsertEmojiBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SelectUserDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SpoilerTextField
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.epochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getFormattedDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toEpochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.AttachmentsGrid
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreateInGroupInfo
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreatePostHeader
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.DateTimeSelectionFlow
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.GalleryPickerDialog
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.InReplyToInfo
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.InsertLinkDialog
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.PollForm
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.SelectCircleDialog
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.UtilsBar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.parameter.parametersOf

class ComposerScreen(
    private val inReplyToId: String? = null,
    private val inReplyToUsername: String? = null,
    private val inReplyToHandle: String? = null,
    private val groupUsername: String? = null,
    private val groupHandle: String? = null,
    private val editedPostId: String? = null,
    private val scheduledPostId: String? = null,
    private val draftId: String? = null,
    private val urlToShare: String? = null,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model =
            getScreenModel<ComposerMviModel>(parameters = { parametersOf(inReplyToId) })
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val snackbarHostState = remember { SnackbarHostState() }
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val galleryHelper = remember { getGalleryHelper() }
        val focusManager = LocalFocusManager.current
        val missingDataError = LocalStrings.current.messagePostEmptyText
        val invalidVisibilityError = LocalStrings.current.messagePostInvalidVisibility
        val characterLimitExceededError = LocalStrings.current.messageCharacterLimitExceeded
        val pastScheduleDateError = LocalStrings.current.messageScheduleDateInThePast
        val invalidPollError = LocalStrings.current.messageInvalidPollError
        val genericError = LocalStrings.current.messageGenericError
        var openImagePicker by remember { mutableStateOf(false) }
        if (openImagePicker) {
            galleryHelper.getImageFromGallery { bytes ->
                openImagePicker = false
                if (bytes.isNotEmpty()) {
                    model.reduce(ComposerMviModel.Intent.AddAttachment(bytes))
                }
            }
        }
        var photoGalleryPickerOpen by remember { mutableStateOf(false) }
        var linkDialogOpen by remember { mutableStateOf(false) }
        var mentionDialogOpen by remember { mutableStateOf(false) }
        var selectCircleDialogOpen by remember { mutableStateOf(false) }
        var attachmentWithDescriptionBeingEdited by remember { mutableStateOf<AttachmentModel?>(null) }
        var hasSpoilerFieldFocus by remember { mutableStateOf(false) }
        var hasTitleFocus by remember { mutableStateOf(false) }
        val isBeingEdited = remember { scheduledPostId != null || editedPostId != null }
        var scheduleDateMillis by remember { mutableStateOf<Long?>(null) }
        var scheduleDatePickerOpen by remember { mutableStateOf(false) }
        var pollExpirationMillis by remember { mutableStateOf<Long?>(null) }
        var pollExpirationDatePickerOpen by remember { mutableStateOf(false) }
        var insertEmojiModalOpen by remember { mutableStateOf(false) }
        var confirmBackWithUnsavedChangesDialog by remember { mutableStateOf(false) }

        LaunchedEffect(model) {
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
            model.effects
                .onEach { event ->
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

                        ComposerMviModel.Effect.Success -> navigationCoordinator.pop()
                    }
                }.launchIn(this)
        }
        DisposableEffect(key) {
            navigationCoordinator.setCanGoBackCallback {
                if (uiState.hasUnsavedChanges) {
                    confirmBackWithUnsavedChangesDialog = true
                    return@setCanGoBackCallback false
                }
                true
            }
            onDispose {
                navigationCoordinator.setCanGoBackCallback(null)
            }
        }

        Scaffold(
            modifier =
                Modifier
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
                                        confirmBackWithUnsavedChangesDialog = true
                                    } else {
                                        navigationCoordinator.pop()
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    actions = {
                        val options =
                            buildList {
                                when (uiState.publicationType) {
                                    is PublicationType.Scheduled -> {
                                        this += OptionId.SaveDraft.toOption()
                                        this += OptionId.ChangeSchedule.toOption()
                                        if (!isBeingEdited) {
                                            this += OptionId.PublishDefault.toOption()
                                        }
                                    }

                                    PublicationType.Draft -> {
                                        this += OptionId.SetSchedule.toOption()
                                        this += OptionId.PublishDefault.toOption()
                                    }

                                    PublicationType.Default -> {
                                        this += OptionId.SaveDraft.toOption()
                                        this += OptionId.SetSchedule.toOption()
                                    }
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

                                if (uiState.availableEmojis.isNotEmpty()) {
                                    this +=
                                        CustomOptions.InsertCustomEmoji.toOption(
                                            label = LocalStrings.current.insertEmojiTitle,
                                        )
                                }

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

                                if (uiState.galleryFeatureSupported && uiState.poll == null) {
                                    this +=
                                        CustomOptions.SelectFromGallery.toOption(
                                            label = LocalStrings.current.actionAddImageFromGallery,
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
                                    contentDescription = null,
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
                                                OptionId.SetSchedule -> {
                                                    scheduleDateMillis = epochMillis()
                                                    scheduleDatePickerOpen = true
                                                }

                                                OptionId.ChangeSchedule -> {
                                                    scheduleDateMillis =
                                                        (uiState.publicationType as? PublicationType.Scheduled)
                                                            ?.date
                                                            ?.toEpochMillis()
                                                    scheduleDatePickerOpen = true
                                                }

                                                OptionId.PublishDefault ->
                                                    model.reduce(
                                                        ComposerMviModel.Intent.ChangePublicationType(
                                                            PublicationType.Default,
                                                        ),
                                                    )

                                                OptionId.SaveDraft ->
                                                    model.reduce(
                                                        ComposerMviModel.Intent.ChangePublicationType(
                                                            PublicationType.Draft,
                                                        ),
                                                    )

                                                CustomOptions.SelectFromGallery -> {
                                                    val limit =
                                                        uiState.attachmentLimit ?: Int.MAX_VALUE
                                                    if (uiState.attachments.size < limit) {
                                                        photoGalleryPickerOpen = true
                                                    }
                                                }

                                                CustomOptions.TogglePoll -> {
                                                    if (uiState.poll == null) {
                                                        model.reduce(ComposerMviModel.Intent.AddPoll)
                                                    } else {
                                                        model.reduce(ComposerMviModel.Intent.RemovePoll)
                                                    }
                                                }

                                                CustomOptions.ToggleTitle -> {
                                                    model.reduce(ComposerMviModel.Intent.ToggleHasTitle)
                                                }

                                                CustomOptions.ToggleSpoiler -> {
                                                    model.reduce(ComposerMviModel.Intent.ToggleHasSpoiler)
                                                }

                                                CustomOptions.InsertCustomEmoji -> {
                                                    insertEmojiModalOpen = true
                                                }

                                                else -> Unit
                                            }
                                        },
                                    )
                                }
                            }
                        }

                        FilledIconButton(
                            onClick = {
                                model.reduce(ComposerMviModel.Intent.Submit)
                            },
                        ) {
                            Icon(
                                imageVector =
                                    when (uiState.publicationType) {
                                        PublicationType.Draft -> Icons.Default.Save
                                        is PublicationType.Scheduled -> Icons.Default.Schedule
                                        else -> Icons.AutoMirrored.Default.Send
                                    },
                                contentDescription = null,
                            )
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
                UtilsBar(
                    modifier =
                        Modifier.fillMaxWidth(),
                    onAttachmentClicked = {
                        val limit = uiState.attachmentLimit ?: Int.MAX_VALUE
                        if (uiState.attachments.size < limit) {
                            openImagePicker = true
                        }
                    },
                    hasPoll = uiState.poll != null,
                    onLinkClicked = {
                        linkDialogOpen = true
                    },
                    onMentionClicked = {
                        mentionDialogOpen = true
                    },
                    onBoldClicked = {
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
                    onItalicClicked = {
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
                    onUnderlineClicked = {
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
                    onStrikethroughClicked = {
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
                    onCodeClicked = {
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
                )
            },
            content = { padding ->
                Column(
                    modifier =
                        Modifier
                            .padding(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding(),
                            ).consumeWindowInsets(padding)
                            .verticalScroll(rememberScrollState()),
                ) {
                    if (!inReplyToUsername.isNullOrBlank() && !inReplyToId.isNullOrBlank()) {
                        InReplyToInfo(
                            modifier = Modifier.padding(horizontal = Spacing.s),
                            username = inReplyToUsername,
                        )
                    } else if (!groupUsername.isNullOrBlank()) {
                        CreateInGroupInfo(
                            modifier = Modifier.padding(horizontal = Spacing.s),
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
                        visibility = uiState.visibility,
                        availableVisibilities = uiState.availableVisibilities,
                        onChangeVisibility = { visibility ->
                            if (visibility is Visibility.Circle) {
                                selectCircleDialogOpen = true
                            } else {
                                model.reduce(ComposerMviModel.Intent.SetVisibility(visibility))
                            }
                        },
                    )

                    // schedule date and character count
                    if (uiState.characterLimit != null) {
                        Row(
                            modifier =
                                Modifier.padding(
                                    top = Spacing.s,
                                    start = Spacing.s,
                                    end = Spacing.s,
                                ),
                        ) {
                            val date =
                                when (val type = uiState.publicationType) {
                                    is PublicationType.Scheduled -> type.date
                                    else -> null
                                }
                            if (date != null) {
                                Text(
                                    text =
                                        buildString {
                                            append(LocalStrings.current.scheduleDateIndication)
                                            append(" ")
                                            append(
                                                getFormattedDate(
                                                    iso8601Timestamp = date,
                                                    format = "dd/MM/yy HH:mm",
                                                ),
                                            )
                                        },
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text =
                                    buildString {
                                        append(uiState.bodyValue.text.length)
                                        append("/")
                                        append(uiState.characterLimit)
                                    },
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
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
                                    autoCorrect = true,
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
                                    autoCorrect = true,
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
                                autoCorrect = true,
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
                        onValueChanged = {
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
                            onDelete = { attachment ->
                                model.reduce(ComposerMviModel.Intent.RemoveAttachment(attachment))
                            },
                            onEditDescription = { attachment ->
                                attachmentWithDescriptionBeingEdited = attachment
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
            },
        )

        if (uiState.loading) {
            ProgressHud()
        }

        if (linkDialogOpen) {
            InsertLinkDialog(
                initialAnchor =
                    uiState.bodyValue.selection.takeIf { it.length > 0 }?.let { range ->
                        uiState.bodyValue.text.substring(range.start, range.end)
                    },
                onClose = { link ->
                    linkDialogOpen = false
                    if (link != null) {
                        model.reduce(ComposerMviModel.Intent.AddLink(link))
                    }
                },
            )
        }

        if (mentionDialogOpen) {
            SelectUserDialog(
                query = uiState.userSearchQuery,
                users = uiState.userSearchUsers,
                loading = uiState.userSearchLoading,
                canFetchMore = uiState.userSearchCanFetchMore,
                onSearchChanged = {
                    model.reduce(ComposerMviModel.Intent.UserSearchSetQuery(it))
                },
                onLoadMoreUsers = {
                    model.reduce(ComposerMviModel.Intent.UserSearchLoadNextPage)
                },
                onClose = { user ->
                    model.reduce(ComposerMviModel.Intent.UserSearchSetQuery(""))
                    model.reduce(ComposerMviModel.Intent.UserSearchClear)
                    mentionDialogOpen = false
                    val handle = user?.handle
                    if (handle != null) {
                        model.reduce(ComposerMviModel.Intent.AddMention(handle))
                    }
                },
            )
        }

        if (attachmentWithDescriptionBeingEdited != null) {
            EditTextualInfoDialog(
                label = LocalStrings.current.pictureDescriptionPlaceholder,
                value = attachmentWithDescriptionBeingEdited?.description.orEmpty(),
                minLines = 3,
                onClose = { newValue ->
                    val attachment = attachmentWithDescriptionBeingEdited
                    if (attachment != null && newValue != null) {
                        model.reduce(
                            ComposerMviModel.Intent.EditAttachmentDescription(
                                attachment = attachment,
                                description = newValue,
                            ),
                        )
                    }
                    attachmentWithDescriptionBeingEdited = null
                },
            )
        }

        if (selectCircleDialogOpen) {
            SelectCircleDialog(
                circles = uiState.availableCircles,
                onClose = { circle ->
                    selectCircleDialogOpen = false
                    if (circle != null) {
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
                canFetchMore = uiState.galleryCanFetchMore,
                loading = uiState.galleryLoading,
                photos = uiState.galleryCurrentAlbumPhotos,
                onInitialLoad = {
                    model.reduce(ComposerMviModel.Intent.GalleryInitialLoad)
                },
                onLoadMorePhotos = {
                    model.reduce(ComposerMviModel.Intent.GalleryLoadMorePhotos)
                },
                onAlbumChanged = { album ->
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

        if (confirmBackWithUnsavedChangesDialog) {
            CustomConfirmDialog(
                title = LocalStrings.current.unsavedChangesTitle,
                body = LocalStrings.current.messageAreYouSureExit,
                onClose = { confirm ->
                    confirmBackWithUnsavedChangesDialog = false
                    if (confirm) {
                        navigationCoordinator.pop()
                    }
                },
            )
        }
    }
}

private sealed interface CustomOptions : OptionId.Custom {
    data object SelectFromGallery : CustomOptions

    data object TogglePoll : CustomOptions

    data object ToggleTitle : CustomOptions

    data object ToggleSpoiler : CustomOptions

    data object InsertCustomEmoji : CustomOptions
}
