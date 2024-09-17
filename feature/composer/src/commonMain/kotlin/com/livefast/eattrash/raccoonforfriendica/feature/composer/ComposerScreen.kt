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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
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
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SelectUserDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SpoilerTextField
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.concatDateWithTime
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.epochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getFormattedDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toEpochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toIso8601Timestamp
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.AttachmentsGrid
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreateInGroupInfo
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreatePostHeader
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.GalleryPickerDialog
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.InReplyToInfo
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.InsertLinkDialog
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
        val missingDataError = LocalStrings.current.messagePostEmptyText
        val invalidVisibilityError = LocalStrings.current.messagePostInvalidVisibility
        val characterLimitExceededError = LocalStrings.current.messageCharacterLimitExceeded
        val pastScheduleDateError = LocalStrings.current.messageScheduleDateInThePast
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
        var schedulePickerDateMillis by remember { mutableStateOf<Long?>(null) }
        var datePickerOpen by remember { mutableStateOf(false) }
        var timePickerOpen by remember { mutableStateOf(false) }
        var scheduleDateSelected by remember { mutableStateOf<Long?>(null) }

        LaunchedEffect(model) {
            when {
                draftId != null ->
                    model.reduce(ComposerMviModel.Intent.LoadDraft(draftId))
                scheduledPostId != null ->
                    model.reduce(ComposerMviModel.Intent.LoadScheduled(scheduledPostId))

                editedPostId != null ->
                    model.reduce(ComposerMviModel.Intent.LoadEditedPost(editedPostId))

                !inReplyToHandle.isNullOrEmpty() ->
                    model.reduce(ComposerMviModel.Intent.AddMention(inReplyToHandle))

                !groupHandle.isNullOrEmpty() ->
                    model.reduce(ComposerMviModel.Intent.AddGroupReference(groupHandle))

                else -> Unit
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        is ComposerMviModel.Effect.Failure ->
                            snackbarHostState.showSnackbar(message = event.message ?: genericError)

                        ComposerMviModel.Effect.ValidationError.TextOrImagesMandatory ->
                            snackbarHostState.showSnackbar(message = missingDataError)

                        ComposerMviModel.Effect.ValidationError.InvalidVisibility ->
                            snackbarHostState.showSnackbar(message = invalidVisibilityError)

                        ComposerMviModel.Effect.ValidationError.CharacterLimitExceeded ->
                            snackbarHostState.showSnackbar(message = characterLimitExceededError)

                        ComposerMviModel.Effect.ValidationError.ScheduleDateInThePast ->
                            snackbarHostState.showSnackbar(message = pastScheduleDateError)

                        ComposerMviModel.Effect.Success -> navigationCoordinator.pop()
                    }
                }.launchIn(this)
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
                                    navigationCoordinator.pop()
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
                                                    schedulePickerDateMillis = epochMillis()
                                                    datePickerOpen = true
                                                }

                                                OptionId.ChangeSchedule -> {
                                                    schedulePickerDateMillis =
                                                        (uiState.publicationType as? PublicationType.Scheduled)
                                                            ?.date
                                                            ?.toEpochMillis()
                                                    datePickerOpen = true
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
                    hasGallery = uiState.hasGallery,
                    onAttachmentFromGalleryClicked = {
                        val limit = uiState.attachmentLimit ?: Int.MAX_VALUE
                        if (uiState.attachments.size < limit) {
                            photoGalleryPickerOpen = true
                        }
                    },
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
                    onSpoilerClicked = {
                        model.reduce(ComposerMviModel.Intent.ToggleHasSpoiler)
                    },
                    onTitleClicked = {
                        model.reduce(ComposerMviModel.Intent.ToggleHasTitle)
                    },
                )
            },
            content = { padding ->
                Column(
                    modifier =
                        Modifier
                            .padding(
                                top = padding.calculateTopPadding(),
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
                                    keyboardType = KeyboardType.Text,
                                    autoCorrect = true,
                                    capitalization = KeyboardCapitalization.Sentences,
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
                        Text(
                            modifier =
                                Modifier.padding(
                                    top = Spacing.m,
                                    start = Spacing.s,
                                    end = Spacing.s,
                                ),
                            text = LocalStrings.current.createPostAttachmentsSection,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
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

        if (datePickerOpen) {
            val datePickerState =
                rememberDatePickerState(
                    initialSelectedDateMillis = schedulePickerDateMillis,
                )
            DatePickerDialog(
                onDismissRequest = {
                    datePickerOpen = false
                    scheduleDateSelected = null
                },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerOpen = false
                            scheduleDateSelected = datePickerState.selectedDateMillis
                            if (scheduleDateSelected != null) {
                                timePickerOpen = true
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            datePickerOpen = false
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
            ) {
                DatePicker(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    state = datePickerState,
                )
            }
        }

        if (timePickerOpen) {
            val timePickerState =
                rememberTimePickerState(
                    initialMinute = 0,
                    initialHour = 0,
                )
            DatePickerDialog(
                onDismissRequest = {
                    timePickerOpen = false
                    scheduleDateSelected = null
                },
                confirmButton = {
                    Button(
                        onClick = {
                            timePickerOpen = false
                            val hour = timePickerState.hour
                            val minute = timePickerState.minute
                            if (scheduleDateSelected != null) {
                                val resultingScheduleDate =
                                    scheduleDateSelected
                                        ?.concatDateWithTime(hour, minute, 0)
                                        ?.toIso8601Timestamp(withLocalTimezone = false)
                                if (resultingScheduleDate != null) {
                                    model.reduce(
                                        ComposerMviModel.Intent.ChangePublicationType(
                                            PublicationType.Scheduled(resultingScheduleDate),
                                        ),
                                    )
                                }
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            timePickerOpen = false
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
            ) {
                TimePicker(
                    modifier =
                        Modifier
                            .padding(top = Spacing.s)
                            .align(Alignment.CenterHorizontally),
                    state = timePickerState,
                )
            }
        }
    }
}
