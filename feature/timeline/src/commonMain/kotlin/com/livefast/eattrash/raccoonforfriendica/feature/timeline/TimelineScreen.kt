package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PostDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.LocalUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.clickableWithoutFocus
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassEqualOrAbove
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldPost
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    model: TimelineMvi,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val connection = navigationCoordinator.getBottomBarScrollConnection()
    val uriHandler = LocalUriHandler.current
    val mainRouter = LocalUiDeps.current.mainRouter
    val scope = rememberCoroutineScope()
    val drawerCoordinator = LocalUiDeps.current.drawerCoordinator
    val fabNestedScrollConnection = LocalUiDeps.current.fabNestedScrollConnection
    val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
    val bottomNavigationInset =
        with(LocalDensity.current) {
            WindowInsets.navigationBars.getBottom(this).toDp()
        }
    val snackbarHostState = remember { SnackbarHostState() }
    val shareHelper = LocalUiDeps.current.shareHelper
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboardHelper = LocalUiDeps.current.getClipboardHelper(LocalClipboard.current)
    var timelineTypeSelectorOpen by remember { mutableStateOf(false) }
    var confirmDeletePostId by remember { mutableStateOf<String?>(null) }
    var confirmMutePost by remember { mutableStateOf<PostModel?>(null) }
    var confirmBlockPost by remember { mutableStateOf<PostModel?>(null) }
    var confirmReblogPost by remember { mutableStateOf<PostModel?>(null) }
    var pollErrorDialogOpened by remember { mutableStateOf(false) }
    var seeDetailsPost by remember { mutableStateOf<PostModel?>(null) }

    suspend fun goBackToTop() {
        try {
            lazyListState.scrollToItem(0)
            topAppBarState.heightOffset = 0f
            topAppBarState.contentOffset = 0f
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }

    LaunchedEffect(model) {
        model.effects
            .onEach { event ->
                when (event) {
                    TimelineMvi.Effect.BackToTop -> goBackToTop()

                    TimelineMvi.Effect.PollVoteFailure -> pollErrorDialogOpened = true

                    is TimelineMvi.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is TimelineMvi.Effect.OpenDetail -> {
                        mainRouter.openPostDetail(
                            post = event.post,
                            swipeNavigationEnabled = true,
                        )
                    }

                    is TimelineMvi.Effect.OpenUrl -> uriHandler.openExternally(event.url)
                }
            }.launchIn(this)
    }
    LaunchedEffect(navigationCoordinator) {
        navigationCoordinator.onDoubleTabSelection
            .onEach { section ->
                if (section == BottomNavigationSection.Home) {
                    goBackToTop()
                }
            }.launchIn(this)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
                modifier =
                Modifier.clickableWithoutFocus {
                    if (uiState.availableTimelineTypes.isNotEmpty()) {
                        timelineTypeSelectorOpen = true
                    }
                },
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = uiState.timelineType?.toReadableName().orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerCoordinator.toggleDrawer()
                                }
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.menu,
                                contentDescription = LocalStrings.current.actionOpenSideMenu,
                            )
                        }
                    }
                },
                actions = {
                    if (uiState.unreadAnnouncements > 0) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    mainRouter.openAnnouncements()
                                }
                            },
                        ) {
                            BadgedBox(
                                badge = {
                                    val unreadCount = uiState.unreadAnnouncements
                                    if (unreadCount > 0) {
                                        Badge(
                                            modifier = Modifier.align(Alignment.TopEnd),
                                        ) {
                                            Text(
                                                text = if (unreadCount <= 10) "$unreadCount" else "10+",
                                                style =
                                                MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 8.sp,
                                                ),
                                            )
                                        }
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = LocalResources.current.campaign,
                                    contentDescription = LocalStrings.current.announcementsTitle,
                                )
                            }
                        }
                    }
                    if (isWidthSizeClassEqualOrAbove(WindowWidthSizeClass.Expanded) && uiState.currentUserId != null) {
                        IconButton(
                            shape = MaterialTheme.shapes.small,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(),
                            onClick = {
                                mainRouter.openComposer()
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.add,
                                contentDescription = LocalStrings.current.actionAddNew,
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded) && uiState.currentUserId != null) {
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
                        modifier =
                        Modifier.padding(
                            bottom = Dimensions.floatingActionButtonBottomInset + bottomNavigationInset,
                        ),
                        onClick = {
                            mainRouter.openComposer()
                        },
                    ) {
                        Icon(
                            imageVector = LocalResources.current.add,
                            contentDescription = LocalStrings.current.actionAddNew,
                        )
                    }
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
                    if (connection != null && uiState.hideNavigationBarWhileScrolling) {
                        Modifier.nestedScroll(connection)
                    } else {
                        Modifier
                    },
                ).then(
                    if (uiState.hideNavigationBarWhileScrolling) {
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    } else {
                        Modifier
                    },
                ).nestedScroll(fabNestedScrollConnection),
            isRefreshing = uiState.refreshing,
            onRefresh = {
                model.reduce(TimelineMvi.Intent.Refresh)
            },
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                if (uiState.initial) {
                    val placeholderCount = 5
                    items(placeholderCount) { idx ->
                        TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                        if (idx < placeholderCount - 1) {
                            TimelineDivider(layout = uiState.layout)
                        }
                    }
                }

                if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.posts.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                            text = LocalStrings.current.messageEmptyList,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                itemsIndexed(
                    items = uiState.posts,
                    key = { _, post -> "timeline-${post.safeKey}" },
                ) { idx, post ->
                    TimelineItem(
                        post = post,
                        layout = uiState.layout,
                        blurNsfw = uiState.blurNsfw,
                        autoloadImages = uiState.autoloadImages,
                        maxBodyLines = uiState.maxBodyLines,
                        onClick = { post ->
                            model.reduce(TimelineMvi.Intent.WillOpenDetail(post))
                        },
                        onOpenUrl = { url, allowOpenInternal ->
                            if (allowOpenInternal) {
                                uriHandler.openUri(url)
                            } else {
                                uriHandler.openExternally(url)
                            }
                        },
                        onOpenUser = {
                            mainRouter.openUserDetail(it)
                        },
                        onOpenImage = { urls, imageIdx, videoIndices ->
                            mainRouter.openImageDetail(
                                urls = urls,
                                initialIndex = imageIdx,
                                videoIndices = videoIndices,
                            )
                        },
                        onReblog =
                        { post: PostModel ->
                            val timeSinceCreation =
                                post.created?.run {
                                    getDurationFromDateToNow(this)
                                } ?: Duration.ZERO
                            when {
                                !post.reblogged && timeSinceCreation.isOldPost ->
                                    confirmReblogPost = post

                                else ->
                                    model.reduce(
                                        TimelineMvi.Intent.ToggleReblog(post),
                                    )
                            }
                        }.takeIf { post.original.actionPermissions.canReblog },
                        onBookmark =
                        { post: PostModel ->
                            model.reduce(TimelineMvi.Intent.ToggleBookmark(post))
                        }.takeIf { post.original.actionPermissions.canBookmark },
                        onFavorite =
                        { post: PostModel ->
                            model.reduce(TimelineMvi.Intent.ToggleFavorite(post))
                        }.takeIf { post.original.actionPermissions.canFavorite },
                        onDislike =
                        { post: PostModel ->
                            model.reduce(TimelineMvi.Intent.ToggleDislike(post))
                        }.takeIf { post.original.actionPermissions.canDislike },
                        onReply =
                        { post: PostModel ->
                            mainRouter.openComposer(
                                inReplyTo = post,
                                inReplyToUser = post.creator,
                            )
                        }.takeIf { post.original.actionPermissions.canReply },
                        onPollVote =
                        uiState.currentUserId?.let {
                            { post, choices ->
                                model.reduce(
                                    TimelineMvi.Intent.SubmitPollVote(
                                        post = post,
                                        choices = choices,
                                    ),
                                )
                            }
                        },
                        onShowOriginal = {
                            model.reduce(
                                TimelineMvi.Intent.ToggleTranslation(post.original),
                            )
                        },
                        onOpenQuote = { post ->
                            mainRouter.openPostDetail(post)
                        },
                        options =
                        buildList {
                            if (post.original.actionPermissions.canShare) {
                                this += OptionId.Share.toOption()
                                this += OptionId.CopyUrl.toOption()
                            }
                            if (post.original.actionPermissions.canEdit) {
                                this += OptionId.Edit.toOption()
                            }
                            if (post.original.actionPermissions.canDelete) {
                                this += OptionId.Delete.toOption()
                            }
                            if (post.original.actionPermissions.canTogglePin) {
                                if (post.pinned) {
                                    this += OptionId.Unpin.toOption()
                                } else {
                                    this += OptionId.Pin.toOption()
                                }
                            }
                            if (post.original.actionPermissions.canMute) {
                                this += OptionId.Mute.toOption()
                            }
                            if (post.original.actionPermissions.canBlock) {
                                this += OptionId.Block.toOption()
                            }
                            if (post.original.actionPermissions.canReport) {
                                this += OptionId.ReportUser.toOption()
                                this += OptionId.ReportPost.toOption()
                            }
                            if (post.original.actionPermissions.canQuote) {
                                this += OptionId.Quote.toOption()
                            }
                            this += OptionId.ViewDetails.toOption()
                            this += OptionId.CopyToClipboard.toOption()
                            val currentLang = uiState.lang.orEmpty()
                            if (currentLang.isNotEmpty() &&
                                post.lang != currentLang &&
                                !post.isShowingTranslation
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
                            val nodeName = post.nodeName
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
                                    val urlString = post.url.orEmpty()
                                    shareHelper.share(urlString)
                                }

                                OptionId.CopyUrl -> {
                                    val urlString = post.url.orEmpty()
                                    scope.launch {
                                        clipboardHelper.setText(urlString)
                                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                    }
                                }

                                OptionId.Edit -> {
                                    post.original.also { toEdit ->
                                        mainRouter.openComposer(
                                            inReplyTo = toEdit.inReplyTo,
                                            inReplyToUser = toEdit.inReplyTo?.creator,
                                            editedPostId = toEdit.id,
                                        )
                                    }
                                }

                                OptionId.Delete -> confirmDeletePostId = post.id

                                OptionId.Mute -> confirmMutePost = post

                                OptionId.Block -> confirmBlockPost = post

                                OptionId.Pin, OptionId.Unpin ->
                                    model.reduce(TimelineMvi.Intent.TogglePin(post))

                                OptionId.ReportUser ->
                                    post.original.creator?.also { toReport ->
                                        mainRouter.openCreateReport(user = toReport)
                                    }

                                OptionId.ReportPost ->
                                    post.original.also { toReport ->
                                        toReport.creator?.also { userToReport ->
                                            mainRouter.openCreateReport(
                                                user = userToReport,
                                                post = toReport,
                                            )
                                        }
                                    }

                                OptionId.Quote -> {
                                    post.original.also { toShare ->
                                        mainRouter.openComposer(
                                            quoted = toShare,
                                            urlToShare = toShare.url,
                                        )
                                    }
                                }

                                OptionId.ViewDetails -> seeDetailsPost = post.original

                                OptionId.CopyToClipboard ->
                                    model.reduce(
                                        TimelineMvi.Intent.CopyToClipboard(post.original),
                                    )

                                OptionId.Translate ->
                                    model.reduce(
                                        TimelineMvi.Intent.ToggleTranslation(post.original),
                                    )

                                OptionId.AddShortcut ->
                                    model.reduce(
                                        TimelineMvi.Intent.AddInstanceShortcut(post.nodeName),
                                    )

                                OptionId.OpenInBrowser ->
                                    model.reduce(
                                        TimelineMvi.Intent.OpenInBrowser(post),
                                    )

                                else -> Unit
                            }
                        },
                    )
                    if (idx < uiState.posts.lastIndex) {
                        TimelineDivider(layout = uiState.layout)
                    }

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    val isNearTheEnd = idx.isNearTheEnd(uiState.posts)
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(TimelineMvi.Intent.LoadNextPage)
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

    if (timelineTypeSelectorOpen) {
        val items = uiState.availableTimelineTypes
        CustomModalBottomSheet(
            title = LocalStrings.current.feedTypeTitle,
            items =
            items.map {
                CustomModalBottomSheetItem(
                    label = it.toReadableName(),
                    trailingContent = {
                        Icon(
                            modifier = Modifier.size(IconSize.m),
                            imageVector = it.toIcon(LocalResources.current),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                )
            },
            onSelect = { index ->
                timelineTypeSelectorOpen = false
                if (index != null) {
                    val type = items[index]
                    model.reduce(TimelineMvi.Intent.ChangeType(type))
                }
            },
        )
    }

    if (confirmDeletePostId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val postId = confirmDeletePostId ?: ""
                confirmDeletePostId = null
                if (confirm && postId.isNotEmpty()) {
                    model.reduce(TimelineMvi.Intent.Delete(postId))
                }
            },
        )
    }

    if (confirmMutePost != null) {
        (confirmMutePost?.reblog?.creator ?: confirmMutePost?.creator)?.also { user ->
            ConfirmMuteUserBottomSheet(
                userHandle = user.handle.orEmpty(),
                onClose = { result ->
                    val postId = confirmMutePost?.id
                    confirmMutePost = null
                    if (result != null) {
                        val (duration, disableNotifications) = result
                        if (postId != null) {
                            model.reduce(
                                TimelineMvi.Intent.MuteUser(
                                    userId = user.id,
                                    postId = postId,
                                    duration = duration,
                                    disableNotifications = disableNotifications,
                                ),
                            )
                        }
                    }
                },
            )
        }
    }

    if (confirmBlockPost != null) {
        val creator = confirmBlockPost?.reblog?.creator ?: confirmBlockPost?.creator
        CustomConfirmDialog(
            title =
            buildString {
                append(LocalStrings.current.actionBlock)
                val handle = creator?.handle ?: ""
                if (handle.isNotEmpty()) {
                    append(" @$handle")
                }
            },
            onClose = { confirm ->
                val postId = confirmBlockPost?.id
                confirmBlockPost = null
                val creatorId = creator?.id
                confirmBlockPost = null
                if (confirm && postId != null && creatorId != null) {
                    model.reduce(
                        TimelineMvi.Intent.BlockUser(
                            userId = creatorId,
                            postId = postId,
                        ),
                    )
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

    if (confirmReblogPost != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.buttonConfirm,
            body = LocalStrings.current.messageAreYouSureReblog,
            onClose = { confirm ->
                val post = confirmReblogPost
                confirmReblogPost = null
                if (confirm && post != null) {
                    model.reduce(TimelineMvi.Intent.ToggleReblog(post))
                }
            },
        )
    }

    seeDetailsPost?.let { post ->
        PostDetailDialog(
            post = post,
            onClose = {
                seeDetailsPost = null
            },
        )
    }
}
