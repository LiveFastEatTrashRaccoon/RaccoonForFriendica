package com.livefast.eattrash.raccoonforfriendica.feature.profile.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.EditFieldItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.InsertEmojiBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsImageInfo
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getGalleryHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class EditProfileScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<EditProfileMviModel>()
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val galleryHelper = remember { getGalleryHelper() }
        val genericError = LocalStrings.current.messageGenericError
        val messageSuccess = LocalStrings.current.messageSuccess
        var confirmBackWithUnsavedChangesDialog by remember { mutableStateOf(false) }
        var openAvatarPicker by remember { mutableStateOf(false) }
        var openHeaderPicker by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        var hasDisplayNameFocus by remember { mutableStateOf(false) }
        var hasBioFocus by remember { mutableStateOf(false) }
        var insertEmojiModalOpen by remember { mutableStateOf(false) }

        fun goBackToTop() {
            runCatching {
                scope.launch {
                    lazyListState.scrollToItem(0)
                    topAppBarState.heightOffset = 0f
                    topAppBarState.contentOffset = 0f
                }
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        EditProfileMviModel.Effect.Failure ->
                            snackbarHostState.showSnackbar(
                                genericError,
                            )

                        EditProfileMviModel.Effect.Success ->
                            snackbarHostState.showSnackbar(
                                messageSuccess,
                            )
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
            modifier = Modifier.safeImePadding(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.editProfileTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
                                if (uiState.availableEmojis.isNotEmpty()) {
                                    this +=
                                        CustomOptions.InsertCustomEmoji.toOption(
                                            label = LocalStrings.current.insertEmojiTitle,
                                        )
                                }
                            }
                        if (options.isNotEmpty()) {
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
                        }

                        Button(
                            enabled = uiState.hasUnsavedChanges,
                            onClick = {
                                model.reduce(EditProfileMviModel.Intent.Submit)
                            },
                        ) {
                            Text(text = LocalStrings.current.buttonSave)
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
        ) { padding ->
            LazyColumn(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .then(
                            if (uiState.hideNavigationBarWhileScrolling) {
                                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            } else {
                                Modifier
                            },
                        ),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                item {
                    SettingsHeader(
                        icon = Icons.Default.AccountCircle,
                        title = LocalStrings.current.editProfileSectionPersonal,
                    )
                }
                item {
                    OutlinedTextField(
                        modifier =
                            Modifier
                                .padding(horizontal = Spacing.s)
                                .fillMaxWidth()
                                .onFocusChanged {
                                    hasDisplayNameFocus = it.hasFocus
                                },
                        label = {
                            Text(text = LocalStrings.current.editProfileItemDisplayName)
                        },
                        value = uiState.displayName,
                        maxLines = 1,
                        keyboardOptions =
                            KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text,
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                },
                            ),
                        onValueChange = {
                            model.reduce(EditProfileMviModel.Intent.ChangeDisplayName(it))
                        },
                    )
                }
                item {
                    OutlinedTextField(
                        modifier =
                            Modifier
                                .padding(horizontal = Spacing.s)
                                .fillMaxWidth()
                                .height(200.dp)
                                .onFocusChanged {
                                    hasBioFocus = it.hasFocus
                                },
                        label = {
                            Text(text = LocalStrings.current.editProfileItemBio)
                        },
                        value = uiState.bio,
                        keyboardOptions =
                            KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text,
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                },
                            ),
                        onValueChange = {
                            model.reduce(EditProfileMviModel.Intent.ChangeBio(it))
                        },
                    )
                }

                item {
                    SettingsHeader(
                        icon = Icons.Default.Camera,
                        title = LocalStrings.current.editProfileSectionImages,
                    )
                }
                item {
                    val avatarSize = IconSize.xxl
                    SettingsImageInfo(
                        title = LocalStrings.current.editProfileItemAvatar,
                        imageModifier =
                            Modifier
                                .size(avatarSize)
                                .clip(RoundedCornerShape(avatarSize / 2)),
                        url = uiState.avatar,
                        autoloadImages = uiState.autoloadImages,
                        bytes = uiState.avatarBytes,
                        onEdit = {
                            openAvatarPicker = true
                        },
                    )
                }
                item {
                    SettingsImageInfo(
                        title = LocalStrings.current.editProfileItemHeader,
                        imageModifier = Modifier.fillMaxWidth().aspectRatio(3.5f),
                        contentScale = ContentScale.Crop,
                        url = uiState.header,
                        autoloadImages = uiState.autoloadImages,
                        bytes = uiState.headerBytes,
                        onEdit = {
                            openHeaderPicker = true
                        },
                    )
                }

                item {
                    SettingsHeader(
                        icon = Icons.Default.ViewAgenda,
                        title = LocalStrings.current.editProfileSectionFields,
                        rightButton = {
                            IconButton(
                                enabled = uiState.canAddFields,
                                onClick = {
                                    model.reduce(EditProfileMviModel.Intent.AddField)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                )
                            }
                        },
                    )
                }
                itemsIndexed(uiState.fields) { idx, field ->
                    EditFieldItem(
                        modifier = Modifier.padding(horizontal = Spacing.s),
                        field = field,
                        onValueChange = { key, value ->
                            model.reduce(
                                EditProfileMviModel.Intent.EditField(
                                    index = idx,
                                    key = key,
                                    value = value,
                                ),
                            )
                        },
                        onDelete = {
                            model.reduce(EditProfileMviModel.Intent.RemoveField(idx))
                        },
                    )
                }
                item {
                    SettingsHeader(
                        icon = Icons.Default.BuildCircle,
                        title = LocalStrings.current.editProfileSectionFlags,
                    )
                }
                item {
                    SettingsSwitchRow(
                        title = LocalStrings.current.editProfileItemBot,
                        value = uiState.bot,
                        onValueChanged = {
                            model.reduce(EditProfileMviModel.Intent.ChangeBot(it))
                        },
                    )
                }
                item {
                    SettingsSwitchRow(
                        title = LocalStrings.current.editProfileItemLocked,
                        value = uiState.locked,
                        onValueChanged = {
                            model.reduce(EditProfileMviModel.Intent.ChangeLocked(it))
                        },
                    )
                }
                item {
                    SettingsSwitchRow(
                        title = LocalStrings.current.editProfileItemDiscoverable,
                        value = uiState.discoverable,
                        onValueChanged = {
                            model.reduce(EditProfileMviModel.Intent.ChangeDiscoverable(it))
                        },
                    )
                }
                item {
                    SettingsSwitchRow(
                        title = LocalStrings.current.editProfileItemHideCollections,
                        value = uiState.hideCollections,
                        onValueChanged = {
                            model.reduce(EditProfileMviModel.Intent.ChangeHideCollections(it))
                        },
                    )
                }
                item {
                    SettingsSwitchRow(
                        title = LocalStrings.current.editProfileItemNoIndex,
                        value = uiState.noIndex,
                        onValueChanged = {
                            model.reduce(EditProfileMviModel.Intent.ChangeNoIndex(it))
                        },
                    )
                }
            }
        }

        if (uiState.loading) {
            ProgressHud()
        }

        if (openAvatarPicker) {
            galleryHelper.getImageFromGallery { bytes ->
                openAvatarPicker = false
                if (bytes.isNotEmpty()) {
                    model.reduce(EditProfileMviModel.Intent.AvatarSelected(bytes))
                }
            }
        }
        if (openHeaderPicker) {
            galleryHelper.getImageFromGallery { bytes ->
                openHeaderPicker = false
                if (bytes.isNotEmpty()) {
                    model.reduce(EditProfileMviModel.Intent.HeaderSelected(bytes))
                }
            }
        }

        if (insertEmojiModalOpen) {
            InsertEmojiBottomSheet(
                emojis = uiState.availableEmojis,
                onClose = {
                    insertEmojiModalOpen = false
                },
                onInsert = { emoji ->
                    model.reduce(
                        EditProfileMviModel.Intent.InsertCustomEmoji(
                            fieldType =
                                when {
                                    hasDisplayNameFocus -> EditProfilerFieldType.DisplayName
                                    else -> EditProfilerFieldType.Bio
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
    data object InsertCustomEmoji : CustomOptions
}
