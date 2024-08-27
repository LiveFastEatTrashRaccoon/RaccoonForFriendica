package com.livefast.eattrash.raccoonforfriendica.feature.composer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.EditTextualInfoDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SpoilerTextField
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.AttachmentsGrid
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreateInGroupInfo
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.CreatePostHeader
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.InReplyToInfo
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.InsertLinkDialog
import com.livefast.eattrash.raccoonforfriendica.feature.composer.components.MentionDialog
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
        val genericError = LocalStrings.current.messageGenericError
        var openImagePicker by remember { mutableStateOf(false) }
        if (openImagePicker) {
            galleryHelper.getImageFromGallery { bytes ->
                openImagePicker = false
                model.reduce(ComposerMviModel.Intent.AddAttachment(bytes))
            }
        }
        var linkDialogOpen by remember { mutableStateOf(false) }
        var mentionDialogOpen by remember { mutableStateOf(false) }
        var selectCircleDialogOpen by remember { mutableStateOf(false) }
        var attachmentWithDescriptionBeingEdited by remember { mutableStateOf<AttachmentModel?>(null) }
        var hasSpoilerFieldFocus by remember { mutableStateOf(false) }
        var hasTitleFocus by remember { mutableStateOf(false) }

        LaunchedEffect(model) {
            if (editedPostId == null) {
                if (!inReplyToHandle.isNullOrEmpty()) {
                    model.reduce(ComposerMviModel.Intent.AddMention(inReplyToHandle))
                } else if (!groupHandle.isNullOrEmpty()) {
                    model.reduce(ComposerMviModel.Intent.AddGroupReference(groupHandle))
                }
            } else {
                model.reduce(ComposerMviModel.Intent.LoadEditedPost(editedPostId))
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        is ComposerMviModel.Effect.Failure -> {
                            snackbarHostState.showSnackbar(message = event.message ?: genericError)
                        }

                        ComposerMviModel.Effect.ValidationError.TextOrImagesMandatory -> {
                            snackbarHostState.showSnackbar(message = missingDataError)
                        }

                        ComposerMviModel.Effect.ValidationError.InvalidVisibility -> {
                            snackbarHostState.showSnackbar(message = invalidVisibilityError)
                        }

                        ComposerMviModel.Effect.Success -> {
                            navigationCoordinator.pop()
                        }
                    }
                }.launchIn(this)
        }

        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
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
                        Image(
                            modifier =
                                Modifier.clickable {
                                    navigationCoordinator.pop()
                                },
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                model.reduce(ComposerMviModel.Intent.Submit)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Send,
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
            content = { padding ->
                Box(
                    modifier =
                        Modifier
                            .padding(
                                top = padding.calculateTopPadding(),
                            ).consumeWindowInsets(padding)
                            .safeImePadding()
                            .fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (!inReplyToUsername.isNullOrBlank()) {
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
                            modifier = Modifier.padding(horizontal = Spacing.s),
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

                        // post body
                        OutlinedTextField(
                            modifier =
                                Modifier
                                    .padding(top = Spacing.s, start = Spacing.xs, end = Spacing.xs)
                                    .fillMaxWidth()
                                    .heightIn(200.dp, 300.dp),
                            placeholder = {
                                Text(text = LocalStrings.current.createPostBodyPlaceholder)
                            },
                            value = uiState.bodyValue,
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    autoCorrect = true,
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
                                modifier = Modifier.padding(top = Spacing.m),
                                text = LocalStrings.current.createPostAttachmentsSection,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        AttachmentsGrid(
                            attachments = uiState.attachments,
                            onDelete = { attachment ->
                                model.reduce(ComposerMviModel.Intent.RemoveAttachment(attachment.id))
                            },
                            onEditDescription = { attachment ->
                                attachmentWithDescriptionBeingEdited = attachment
                            },
                        )
                    }

                    UtilsBar(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                        onAttachmentClicked = {
                            openImagePicker = true
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
                    )
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
            MentionDialog(
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
    }
}
