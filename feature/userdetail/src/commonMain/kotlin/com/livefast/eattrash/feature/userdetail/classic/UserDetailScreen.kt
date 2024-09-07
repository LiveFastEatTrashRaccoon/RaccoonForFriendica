package com.livefast.eattrash.feature.userdetail.classic

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserFields
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeaderPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toAccountSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toInt
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

class UserDetailScreen(
    private val id: String,
) : Screen {
    override val key: ScreenKey
        get() = super.key + id

    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalFoundationApi::class,
        ExperimentalMaterialApi::class,
    )
    @Composable
    override fun Content() {
        val model = getScreenModel<UserDetailMviModel>(parameters = { parametersOf(id) })
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val shareHelper = remember { getShareHelper() }
        val stickyHeaderTopOffset by animateDpAsState(
            if (lazyListState.firstVisibleItemIndex >= 2) {
                Dimensions.maxTopBarInset * topAppBarState.collapsedFraction
            } else {
                0.dp
            },
        )
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var confirmUnfollowDialogOpen by remember { mutableStateOf(false) }
        var confirmDeleteFollowRequestDialogOpen by remember { mutableStateOf(false) }
        var confirmMuteNotificationsDialogOpen by remember { mutableStateOf(false) }
        var pollErrorDialogOpened by remember { mutableStateOf(false) }
        var confirmMuteUserDialogOpen by remember { mutableStateOf(false) }
        var confirmBlockUserDialogOpen by remember { mutableStateOf(false) }

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
                    }
                }.launchIn(this)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = Spacing.s),
                            text = uiState.user?.handle.orEmpty(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        if (navigationCoordinator.canPop.value) {
                            Image(
                                modifier =
                                    Modifier.clickable {
                                        navigationCoordinator.pop()
                                    },
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            )
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
                                }
                            }
                        var optionsOffset by remember { mutableStateOf(Offset.Zero) }
                        var optionsMenuOpen by remember { mutableStateOf(false) }
                        if (options.isNotEmpty()) {
                            Box {
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
                                                    OptionId.Share ->
                                                        shareHelper.share(uiState.user?.url.orEmpty())

                                                    OptionId.Mute ->
                                                        confirmMuteUserDialogOpen = true

                                                    OptionId.Unmute -> {
                                                        model.reduce(
                                                            UserDetailMviModel.Intent.ToggleMute(
                                                                muted = false,
                                                            ),
                                                        )
                                                    }

                                                    OptionId.Block ->
                                                        confirmBlockUserDialogOpen = true

                                                    OptionId.Unblock -> {
                                                        model.reduce(
                                                            UserDetailMviModel.Intent.ToggleBlock(
                                                                blocked = false,
                                                            ),
                                                        )
                                                    }

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
                val pullRefreshState =
                    rememberPullRefreshState(
                        refreshing = uiState.refreshing,
                        onRefresh = {
                            model.reduce(UserDetailMviModel.Intent.Refresh)
                        },
                    )
                Box(
                    modifier =
                        Modifier
                            .padding(padding)
                            .fillMaxWidth()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .pullRefresh(pullRefreshState),
                ) {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        if (uiState.user != null) {
                            item {
                                UserHeader(
                                    user = uiState.user,
                                    onOpenUrl = { url ->
                                        uriHandler.openUri(url)
                                    },
                                    onOpenImage = { url ->
                                        detailOpener.openImageDetail(url)
                                    },
                                    onRelationshipClicked = { nextAction ->
                                        when (nextAction) {
                                            RelationshipStatusNextAction.AcceptRequest -> {
                                                detailOpener.openFollowRequests()
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
                                    onNotificationsClicked = { nextAction ->
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
                                        detailOpener.openFollowers(id)
                                    },
                                    onOpenFollowing = {
                                        detailOpener.openFollowing(id)
                                    },
                                    onOpenInForumMode = {
                                        detailOpener.openInForumMode(id)
                                    },
                                )
                            }
                        } else {
                            item {
                                UserHeaderPlaceholder(modifier = Modifier.fillMaxWidth())
                            }
                        }
                        item {
                            UserFields(
                                modifier =
                                    Modifier.padding(
                                        top = Spacing.m,
                                        bottom = Spacing.s,
                                    ),
                                fields =
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
                                    },
                                onOpenUrl = { url ->
                                    uriHandler.openUri(url)
                                },
                            )
                        }

                        stickyHeader {
                            SectionSelector(
                                modifier =
                                    Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(
                                            top = stickyHeaderTopOffset,
                                            bottom = Spacing.s,
                                        ),
                                titles =
                                    listOf(
                                        UserSection.Posts.toReadableName(),
                                        UserSection.All.toReadableName(),
                                        UserSection.Pinned.toReadableName(),
                                        UserSection.Media.toReadableName(),
                                    ),
                                scrollable = true,
                                currentSection = uiState.section.toInt(),
                                onSectionSelected = {
                                    val section = it.toAccountSection()
                                    model.reduce(
                                        UserDetailMviModel.Intent.ChangeSection(
                                            section,
                                        ),
                                    )
                                },
                            )
                        }

                        if (uiState.initial) {
                            val placeholderCount = 5
                            items(placeholderCount) { idx ->
                                TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                                if (idx < placeholderCount - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = Spacing.s),
                                    )
                                }
                            }
                        }

                        itemsIndexed(
                            items = uiState.entries,
                            key = { _, e -> e.safeKey },
                        ) { idx, entry ->
                            TimelineItem(
                                entry = entry,
                                blurNsfw = uiState.blurNsfw,
                                onClick = { e ->
                                    detailOpener.openEntryDetail(e.id)
                                },
                                onOpenUrl = { url ->
                                    uriHandler.openUri(url)
                                },
                                onOpenUser = { user ->
                                    if (user.id != uiState.user?.id) {
                                        detailOpener.openUserDetail(user.id)
                                    }
                                },
                                onOpenImage = { imageUrl ->
                                    detailOpener.openImageDetail(imageUrl)
                                },
                                onReblog =
                                    uiState.currentUserId?.let {
                                        { e -> model.reduce(UserDetailMviModel.Intent.ToggleReblog(e)) }
                                    },
                                onBookmark =
                                    uiState.currentUserId?.let {
                                        { e ->
                                            model.reduce(
                                                UserDetailMviModel.Intent.ToggleBookmark(e),
                                            )
                                        }
                                    },
                                onFavorite =
                                    uiState.currentUserId?.let {
                                        { e ->
                                            model.reduce(
                                                UserDetailMviModel.Intent.ToggleFavorite(e),
                                            )
                                        }
                                    },
                                onReply =
                                    uiState.currentUserId?.let {
                                        { e ->
                                            detailOpener.openComposer(
                                                inReplyToId = e.id,
                                                inReplyToHandle = e.creator?.handle,
                                                inReplyToUsername =
                                                    e.creator?.let {
                                                        it.displayName ?: it.username
                                                    },
                                            )
                                        }
                                    },
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
                                onToggleSpoilerActive = { e ->
                                    model.reduce(UserDetailMviModel.Intent.ToggleSpoilerActive(e))
                                },
                                options =
                                    buildList {
                                        if (!entry.url.isNullOrBlank()) {
                                            this += OptionId.Share.toOption()
                                            this += OptionId.CopyUrl.toOption()
                                        }
                                    },
                                onOptionSelected = { optionId ->
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

                                        else -> Unit
                                    }
                                },
                            )
                            if (idx < uiState.entries.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.s),
                                )
                            }

                            val canFetchMore =
                                !uiState.initial && !uiState.loading && uiState.canFetchMore
                            val isNearTheEnd =
                                idx == uiState.entries.lastIndex - 5 || uiState.entries.size < 5
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

                    PullRefreshIndicator(
                        refreshing = uiState.refreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        backgroundColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    )
                }
            },
        )

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
    }
}
