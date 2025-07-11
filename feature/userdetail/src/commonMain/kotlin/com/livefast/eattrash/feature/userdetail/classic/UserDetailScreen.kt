package com.livefast.eattrash.feature.userdetail.classic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.feature.userdetail.components.UserNoteField
import com.livefast.eattrash.feature.userdetail.di.UserDetailViewModelArgs
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.EntryDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserFields
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeaderPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldEntry
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(id: String, modifier: Modifier = Modifier) {
    val model: UserDetailMviModel = getViewModel<UserDetailViewModel>(
        arg = UserDetailViewModelArgs(id),
    )
    val uiState by model.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val uriHandler = LocalUriHandler.current
    val mainRouter = remember { getMainRouter() }
    val lazyListState = rememberLazyListState()
    val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
    val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val shareHelper = remember { getShareHelper() }
    val actionRepository = remember { getEntryActionRepository() }
    val stickyHeaderTopOffset by animateDpAsState(
        if (lazyListState.firstVisibleItemIndex >= 2) {
            Dimensions.maxTopBarInset * topAppBarState.collapsedFraction
        } else {
            0.dp
        },
    )
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboardManager = LocalClipboardManager.current
    val genericError = LocalStrings.current.messageGenericError
    val focusManager = LocalFocusManager.current
    var confirmUnfollowDialogOpen by remember { mutableStateOf(false) }
    var confirmDeleteFollowRequestDialogOpen by remember { mutableStateOf(false) }
    var confirmMuteNotificationsDialogOpen by remember { mutableStateOf(false) }
    var pollErrorDialogOpened by remember { mutableStateOf(false) }
    var confirmMuteUserDialogOpen by remember { mutableStateOf(false) }
    var confirmBlockUserDialogOpen by remember { mutableStateOf(false) }
    var confirmReblogEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
    var seeDetailsEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
    var changeRateLimitBottomSheetOpen by remember { mutableStateOf(false) }

    suspend fun goBackToTop() {
        runCatching {
            if (lazyListState.firstVisibleItemIndex > 0) {
                if (uiState.entries.isEmpty()) {
                    lazyListState.scrollToItem(1)
                } else {
                    lazyListState.scrollToItem(2)
                }
            } else {
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
                    UserDetailMviModel.Effect.BackToTop -> goBackToTop()
                    UserDetailMviModel.Effect.PollVoteFailure -> pollErrorDialogOpened = true
                    UserDetailMviModel.Effect.Failure ->
                        snackbarHostState.showSnackbar(genericError)

                    is UserDetailMviModel.Effect.TriggerCopy -> {
                        clipboardManager.setText(AnnotatedString(event.text))
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is UserDetailMviModel.Effect.OpenDetail -> {
                        mainRouter.openEntryDetail(
                            entry = event.entry,
                            swipeNavigationEnabled = true,
                        )
                    }

                    is UserDetailMviModel.Effect.OpenUrl -> uriHandler.openExternally(event.url)
                }
            }.launchIn(this)
    }

    Scaffold(
        modifier = modifier.safeImePadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                windowInsets = topAppBarState.toWindowInsets(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = uiState.user?.handle.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                                contentDescription = LocalStrings.current.actionGoBack,
                            )
                        }
                    }
                },
                actions = {
                    val options =
                        buildList {
                            uiState.user?.also { user ->
                                if (!user.url.isNullOrEmpty()) {
                                    this += OptionId.Share.toOption()
                                }
                                if (user.muted) {
                                    this += OptionId.Unmute.toOption()
                                } else {
                                    this += OptionId.Mute.toOption()
                                }
                                if (user.blocked) {
                                    this += OptionId.Unblock.toOption()
                                } else {
                                    this += OptionId.Block.toOption()
                                }
                                if (uiState.currentUserId != null) {
                                    this +=
                                        CustomOptions.ChangeRateLimit.toOption(
                                            label = LocalStrings.current.actionChangeRateLimit,
                                        )
                                }
                                this += OptionId.ReportUser.toOption()
                                this +=
                                    Option(
                                        id = OptionId.Edit,
                                        label =
                                        if (uiState.personalNoteEditEnabled) {
                                            LocalStrings.current.actionCancelEditPersonalNote
                                        } else {
                                            LocalStrings.current.actionEditPersonalNote
                                        },
                                    )
                                if (user.group) {
                                    this +=
                                        CustomOptions.SwitchToForumMode.toOption(
                                            label = LocalStrings.current.actionSwitchToForumMode,
                                        )
                                }
                                val nodeName = user.handle.nodeName
                                if (nodeName != null && nodeName != uiState.currentNode) {
                                    this +=
                                        OptionId.AddShortcut.toOption(
                                            LocalStrings.current.actionShortcut(nodeName),
                                        )
                                }
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
                                    modifier = Modifier.size(IconSize.s),
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
                                                OptionId.Share ->
                                                    shareHelper.share(uiState.user?.url.orEmpty())

                                                OptionId.Mute ->
                                                    confirmMuteUserDialogOpen = true

                                                OptionId.Unmute ->
                                                    model.reduce(
                                                        UserDetailMviModel.Intent.ToggleMute(
                                                            muted = false,
                                                        ),
                                                    )

                                                OptionId.Block ->
                                                    confirmBlockUserDialogOpen = true

                                                OptionId.Unblock ->
                                                    model.reduce(
                                                        UserDetailMviModel.Intent.ToggleBlock(
                                                            blocked = false,
                                                        ),
                                                    )

                                                OptionId.Edit ->
                                                    model.reduce(
                                                        UserDetailMviModel.Intent.TogglePersonalNoteEditMode,
                                                    )

                                                OptionId.ReportUser ->
                                                    uiState.user?.also { userToReport ->
                                                        mainRouter.openCreateReport(user = userToReport)
                                                    }

                                                CustomOptions.SwitchToForumMode -> {
                                                    uiState.user?.also { user ->
                                                        mainRouter.switchUserDetailToForumMode(user)
                                                    }
                                                }

                                                CustomOptions.ChangeRateLimit -> {
                                                    changeRateLimitBottomSheetOpen = true
                                                }

                                                OptionId.AddShortcut ->
                                                    model.reduce(
                                                        UserDetailMviModel.Intent.AddInstanceShortcut(
                                                            uiState.user
                                                                ?.handle
                                                                ?.nodeName
                                                                .orEmpty(),
                                                        ),
                                                    )

                                                else -> Unit
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter =
                slideInVertically(
                    initialOffsetY = { it * 2 },
                ),
                exit =
                slideOutVertically(
                    targetOffsetY = { it * 2 },
                ),
            ) {
                FloatingActionButton(
                    onClick = {
                        mainRouter.openComposer(
                            inReplyToUser = uiState.user,
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Reply,
                        contentDescription = LocalStrings.current.actionReply,
                    )
                }
            }
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
        PullToRefreshBox(
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
                ).nestedScroll(fabNestedScrollConnection),
            isRefreshing = uiState.refreshing,
            onRefresh = {
                model.reduce(UserDetailMviModel.Intent.Refresh)
            },
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                if (uiState.user != null) {
                    item {
                        UserHeader(
                            user = uiState.user,
                            autoloadImages = uiState.autoloadImages,
                            onOpenUrl = { url, allowOpenInternal ->
                                if (allowOpenInternal) {
                                    uriHandler.openUri(url)
                                } else {
                                    uriHandler.openExternally(url)
                                }
                            },
                            onOpenImage = { url ->
                                mainRouter.openImageDetail(url)
                            },
                            onRelationshipClick = { nextAction ->
                                when (nextAction) {
                                    RelationshipStatusNextAction.AcceptRequest -> {
                                        mainRouter.openFollowRequests()
                                    }

                                    RelationshipStatusNextAction.ConfirmUnfollow -> {
                                        confirmUnfollowDialogOpen = true
                                    }

                                    RelationshipStatusNextAction.ConfirmDeleteFollowRequest -> {
                                        confirmDeleteFollowRequestDialogOpen = true
                                    }

                                    RelationshipStatusNextAction.Follow -> {
                                        model.reduce(UserDetailMviModel.Intent.Follow)
                                    }

                                    RelationshipStatusNextAction.Unfollow -> {
                                        model.reduce(UserDetailMviModel.Intent.Unfollow)
                                    }
                                }
                            },
                            onNotificationsClick = { nextAction ->
                                when (nextAction) {
                                    NotificationStatusNextAction.Disable -> {
                                        confirmMuteNotificationsDialogOpen = true
                                    }

                                    NotificationStatusNextAction.Enable -> {
                                        model.reduce(UserDetailMviModel.Intent.EnableNotifications)
                                    }
                                }
                            },
                            onOpenFollowers = {
                                uiState.user?.also { user ->
                                    mainRouter.openFollowers(user)
                                }
                            },
                            onOpenFollowing = {
                                uiState.user?.also { user ->
                                    mainRouter.openFollowing(user)
                                }
                            },
                        )
                    }
                } else {
                    item {
                        UserHeaderPlaceholder(modifier = Modifier.fillMaxWidth())
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                }
                item {
                    val note = uiState.personalNote.orEmpty()
                    if (note.isNotEmpty() || uiState.personalNoteEditEnabled) {
                        UserNoteField(
                            modifier =
                            Modifier.fillMaxWidth().padding(
                                top = Spacing.s,
                                bottom = Spacing.s,
                                start = Spacing.xs,
                                end = Spacing.xs,
                            ),
                            editEnabled = uiState.personalNoteEditEnabled,
                            note = note,
                            onChangeNote = {
                                model.reduce(UserDetailMviModel.Intent.SetPersonalNote(it))
                            },
                            onSave = {
                                focusManager.clearFocus()
                                model.reduce(UserDetailMviModel.Intent.SubmitPersonalNote)
                            },
                        )
                    }
                }
                item {
                    val fields =
                        buildList {
                            uiState.user?.created?.also { date ->
                                add(
                                    FieldModel(
                                        key = LocalStrings.current.accountAge,
                                        value = date.prettifyDate(),
                                    ),
                                )
                            }
                            addAll(uiState.user?.fields.orEmpty())
                        }
                    if (fields.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )
                        UserFields(
                            fields = fields,
                            autoloadImages = uiState.autoloadImages,
                            onOpenUrl = { url, allowOpenInternal ->
                                if (allowOpenInternal) {
                                    uriHandler.openUri(url)
                                } else {
                                    uriHandler.openExternally(url)
                                }
                            },
                        )
                    }
                }

                stickyHeader {
                    val titles =
                        listOf(
                            UserSection.Posts,
                            UserSection.All,
                            UserSection.Pinned,
                            UserSection.Media,
                        )
                    SectionSelector(
                        modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(
                                top = stickyHeaderTopOffset,
                                bottom = Spacing.s,
                            ),
                        titles = titles.map { it.toReadableName() },
                        scrollable = true,
                        currentSection = titles.indexOf(uiState.section),
                        onSelectSection = {
                            model.reduce(
                                UserDetailMviModel.Intent.ChangeSection(titles[it]),
                            )
                        },
                    )
                }

                if (uiState.initial) {
                    val placeholderCount = 5
                    items(placeholderCount) { idx ->
                        TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                        if (idx < placeholderCount - 1) {
                            TimelineDivider(layout = uiState.layout)
                        }
                    }
                }

                itemsIndexed(
                    items = uiState.entries,
                    key = { _, e -> "user-detail-${e.safeKey}" },
                ) { idx, entry ->
                    TimelineItem(
                        entry = entry,
                        layout = uiState.layout,
                        blurNsfw = uiState.blurNsfw,
                        autoloadImages = uiState.autoloadImages,
                        maxBodyLines = uiState.maxBodyLines,
                        onClick = { e ->
                            model.reduce(UserDetailMviModel.Intent.WillOpenDetail(e))
                        },
                        onOpenUrl = { url, allowOpenInternal ->
                            if (allowOpenInternal) {
                                uriHandler.openUri(url)
                            } else {
                                uriHandler.openExternally(url)
                            }
                        },
                        onOpenUser = { user ->
                            if (user.id != uiState.user?.id) {
                                mainRouter.openUserDetail(user)
                            }
                        },
                        onOpenImage = { urls, imageIdx, videoIndices ->
                            mainRouter.openImageDetail(
                                urls = urls,
                                initialIndex = imageIdx,
                                videoIndices = videoIndices,
                            )
                        },
                        onReblog =
                        { e: TimelineEntryModel ->
                            val timeSinceCreation =
                                e.created?.run {
                                    getDurationFromDateToNow(this)
                                } ?: Duration.ZERO
                            when {
                                !e.reblogged && timeSinceCreation.isOldEntry ->
                                    confirmReblogEntry = e

                                else ->
                                    model.reduce(
                                        UserDetailMviModel.Intent.ToggleReblog(e),
                                    )
                            }
                        }.takeIf { actionRepository.canReblog(entry.original) },
                        onBookmark =
                        { e: TimelineEntryModel ->
                            model.reduce(UserDetailMviModel.Intent.ToggleBookmark(e))
                        }.takeIf { actionRepository.canBookmark(entry.original) },
                        onFavorite =
                        { e: TimelineEntryModel ->
                            model.reduce(UserDetailMviModel.Intent.ToggleFavorite(e))
                        }.takeIf { actionRepository.canFavorite(entry.original) },
                        onDislike =
                        { e: TimelineEntryModel ->
                            model.reduce(UserDetailMviModel.Intent.ToggleDislike(e))
                        }.takeIf { actionRepository.canDislike(entry.original) },
                        onReply =
                        { e: TimelineEntryModel ->
                            mainRouter.openComposer(
                                inReplyTo = e,
                                inReplyToUser = e.creator,
                            )
                        }.takeIf { actionRepository.canReply(entry.original) },
                        onPollVote =
                        uiState.currentUserId?.let {
                            { e, choices ->
                                model.reduce(
                                    UserDetailMviModel.Intent.SubmitPollVote(
                                        entry = e,
                                        choices = choices,
                                    ),
                                )
                            }
                        },
                        onShowOriginal = {
                            model.reduce(
                                UserDetailMviModel.Intent.ToggleTranslation(entry.original),
                            )
                        },
                        options =
                        buildList {
                            if (actionRepository.canShare(entry.original)) {
                                this += OptionId.Share.toOption()
                                this += OptionId.CopyUrl.toOption()
                            }
                            if (actionRepository.canReport(entry.original)) {
                                this += OptionId.ReportEntry.toOption()
                            }
                            if (actionRepository.canQuote(entry.original)) {
                                this += OptionId.Quote.toOption()
                            }
                            this += OptionId.ViewDetails.toOption()
                            this += OptionId.CopyToClipboard.toOption()
                            val currentLang = uiState.lang.orEmpty()
                            if (currentLang.isNotEmpty() &&
                                entry.lang != currentLang &&
                                !entry.isShowingTranslation
                            ) {
                                this +=
                                    Option(
                                        id = OptionId.Translate,
                                        label =
                                        buildString {
                                            append(
                                                LocalStrings.current.actionTranslateTo(
                                                    currentLang,
                                                ),
                                            )
                                            append(" (")
                                            append(LocalStrings.current.experimental)
                                            append(")")
                                        },
                                    )
                            }
                            val nodeName = entry.nodeName
                            if (nodeName.isNotEmpty() && nodeName != uiState.currentNode) {
                                this +=
                                    OptionId.AddShortcut.toOption(
                                        LocalStrings.current.actionShortcut(nodeName),
                                    )
                            }
                            this += OptionId.OpenInBrowser.toOption()
                        },
                        onSelectOption = { optionId ->
                            when (optionId) {
                                OptionId.Share -> {
                                    val urlString = entry.url.orEmpty()
                                    shareHelper.share(urlString)
                                }

                                OptionId.CopyUrl -> {
                                    val urlString = entry.url.orEmpty()
                                    clipboardManager.setText(AnnotatedString(urlString))
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            copyToClipboardSuccess,
                                        )
                                    }
                                }

                                OptionId.ReportEntry ->
                                    entry.original.also { entryToReport ->
                                        entryToReport.creator?.also { userToReport ->
                                            mainRouter.openCreateReport(
                                                user = userToReport,
                                                entry = entryToReport,
                                            )
                                        }
                                    }

                                OptionId.ViewDetails -> seeDetailsEntry = entry.original
                                OptionId.Quote -> {
                                    entry.original.also { entryToShare ->
                                        mainRouter.openComposer(
                                            urlToShare = entryToShare.url,
                                        )
                                    }
                                }

                                OptionId.CopyToClipboard ->
                                    model.reduce(UserDetailMviModel.Intent.CopyToClipboard(entry.original))

                                OptionId.Translate ->
                                    model.reduce(
                                        UserDetailMviModel.Intent.ToggleTranslation(entry.original),
                                    )

                                OptionId.AddShortcut ->
                                    model.reduce(
                                        UserDetailMviModel.Intent.AddInstanceShortcut(entry.nodeName),
                                    )

                                OptionId.OpenInBrowser ->
                                    model.reduce(
                                        UserDetailMviModel.Intent.OpenInBrowser(entry),
                                    )

                                else -> Unit
                            }
                        },
                    )
                    if (idx < uiState.entries.lastIndex) {
                        TimelineDivider(layout = uiState.layout)
                    }

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    val isNearTheEnd = idx.isNearTheEnd(uiState.entries)
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(UserDetailMviModel.Intent.LoadNextPage)
                    }
                }

                if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.entries.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                            text = LocalStrings.current.messageEmptyList,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                item {
                    if (uiState.loading && !uiState.refreshing && uiState.canFetchMore) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            ListLoadingIndicator()
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xxxl))
                }
            }
        }
    }

    if (confirmUnfollowDialogOpen) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionUnfollow,
            onClose = { confirm ->
                confirmUnfollowDialogOpen = false
                if (confirm) {
                    model.reduce(UserDetailMviModel.Intent.Unfollow)
                }
            },
        )
    }

    if (confirmDeleteFollowRequestDialogOpen) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDeleteFollowRequest,
            onClose = { confirm ->
                confirmDeleteFollowRequestDialogOpen = false
                if (confirm) {
                    model.reduce(UserDetailMviModel.Intent.Unfollow)
                }
            },
        )
    }

    if (confirmMuteNotificationsDialogOpen) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionMuteNotifications,
            onClose = { confirm ->
                confirmMuteNotificationsDialogOpen = false
                if (confirm) {
                    model.reduce(UserDetailMviModel.Intent.DisableNotifications)
                }
            },
        )
    }

    if (confirmMuteUserDialogOpen) {
        ConfirmMuteUserBottomSheet(
            userHandle = uiState.user?.handle.orEmpty(),
            onClose = { pair ->
                if (pair != null) {
                    confirmMuteUserDialogOpen = false
                    val (duration, disableNotifications) = pair
                    model.reduce(
                        UserDetailMviModel.Intent.ToggleMute(
                            muted = true,
                            duration = duration,
                            disableNotifications = disableNotifications,
                        ),
                    )
                }
            },
        )
    }

    if (confirmBlockUserDialogOpen) {
        CustomConfirmDialog(
            title =
            buildString {
                append(LocalStrings.current.actionBlock)
                val handle = uiState.user?.handle ?: ""
                if (handle.isNotEmpty()) {
                    append(" @$handle")
                }
            },
            onClose = { confirm ->
                confirmBlockUserDialogOpen = false
                if (confirm) {
                    model.reduce(UserDetailMviModel.Intent.ToggleBlock(blocked = true))
                }
            },
        )
    }

    if (pollErrorDialogOpened) {
        PollVoteErrorDialog(
            onDismissRequest = {
                pollErrorDialogOpened = false
            },
        )
    }

    if (confirmReblogEntry != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.buttonConfirm,
            body = LocalStrings.current.messageAreYouSureReblog,
            onClose = { confirm ->
                val e = confirmReblogEntry
                confirmReblogEntry = null
                if (confirm && e != null) {
                    model.reduce(UserDetailMviModel.Intent.ToggleReblog(e))
                }
            },
        )
    }

    if (changeRateLimitBottomSheetOpen) {
        val availableRates =
            listOf(
                0.1,
                0.25,
                0.5,
                1.0,
            )
        CustomModalBottomSheet(
            title = LocalStrings.current.actionChangeRateLimit,
            items =
            availableRates.map { rate ->
                CustomModalBottomSheetItem(
                    label =
                    if (rate < 1.0) {
                        "${rate * 100} %"
                    } else {
                        LocalStrings.current.settingsOptionUnlimited
                    },
                    trailingContent = {
                        val selected =
                            rate == uiState.rateLimit?.rate || (rate >= 1 && uiState.rateLimit == null)
                        if (selected) {
                            Icon(
                                imageVector = Icons.Default.RadioButtonChecked,
                                contentDescription = LocalStrings.current.itemSelected,
                            )
                        }
                    },
                )
            },
            onSelect = { index ->
                changeRateLimitBottomSheetOpen = false
                if (index != null) {
                    val newRate = availableRates[index]
                    model.reduce(UserDetailMviModel.Intent.SetRateLimit(newRate))
                }
            },
        )
    }

    seeDetailsEntry?.let { entry ->
        EntryDetailDialog(
            entry = entry,
            onClose = {
                seeDetailsEntry = null
            },
        )
    }
}

private sealed interface CustomOptions : OptionId.Custom {
    data object SwitchToForumMode : CustomOptions

    data object ChangeRateLimit : CustomOptions
}
