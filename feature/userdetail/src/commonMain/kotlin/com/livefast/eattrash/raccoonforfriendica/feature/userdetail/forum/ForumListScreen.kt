package com.livefast.eattrash.raccoonforfriendica.feature.userdetail.forum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GroupHeader
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
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.clickableWithoutFocus
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassEqualOrAbove
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.ellipsize
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldPost
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumListScreen(id: String, modifier: Modifier = Modifier, otherInstance: String? = null) {
    val model: ForumListMvi = assistedMetroViewModel<ForumListViewModel>(
        extras = ForumListViewModel.getExtras(id),
    )
    val uiState by model.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    val canPopState by navigationCoordinator.canPop.collectAsState()
    val uriHandler = LocalUriHandler.current
    val mainRouter = LocalUiDeps.current.mainRouter
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val fabNestedScrollConnection = LocalUiDeps.current.fabNestedScrollConnection
    val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val shareHelper = LocalUiDeps.current.shareHelper
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboardHelper = LocalUiDeps.current.getClipboardHelper(LocalClipboard.current)
    var confirmUnfollowDialogOpen by remember { mutableStateOf(false) }
    var confirmDeleteFollowRequestDialogOpen by remember { mutableStateOf(false) }
    var confirmMuteNotificationsDialogOpen by remember { mutableStateOf(false) }
    var confirmDeletePostId by remember { mutableStateOf<String?>(null) }
    var confirmMutePost by remember { mutableStateOf<PostModel?>(null) }
    var confirmBlockPost by remember { mutableStateOf<PostModel?>(null) }
    var confirmReblogPost by remember { mutableStateOf<PostModel?>(null) }
    var pollErrorDialogOpened by remember { mutableStateOf(false) }
    var seeDetailsPost by remember { mutableStateOf<PostModel?>(null) }
    val isHomeInstance = otherInstance.isNullOrEmpty()

    fun goBackToTop() {
        try {
            scope.launch {
                lazyListState.scrollToItem(0)
                topAppBarState.heightOffset = 0f
                topAppBarState.contentOffset = 0f
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }

    LaunchedEffect(model) {
        model.effects
            .onEach { event ->
                when (event) {
                    ForumListMvi.Effect.PollVoteFailure -> pollErrorDialogOpened = true

                    is ForumListMvi.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is ForumListMvi.Effect.OpenDetail -> {
                        mainRouter.openThread(
                            post = event.post,
                            swipeNavigationEnabled = true,
                            otherInstance = otherInstance,
                        )
                    }

                    is ForumListMvi.Effect.OpenUrl -> uriHandler.openExternally(event.url)
                }
            }.launchIn(this)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.clickableWithoutFocus { scope.launch { goBackToTop() } },
                windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text =
                        buildString {
                            append(LocalStrings.current.topicTitle)
                            val name = (uiState.user?.displayName ?: uiState.user?.username ?: "").ellipsize(30)
                            if (name.isNotBlank()) {
                                append(": ")
                                append(name)
                            }
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    if (canPopState) {
                        IconButton(
                            onClick = {
                                navigationCoordinator.pop()
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.arrowBack,
                                contentDescription = LocalStrings.current.actionGoBack,
                            )
                        }
                    }
                },
                actions = {
                    val options =
                        buildList {
                            this +=
                                CustomOptions.SwitchToClassicMode.toOption(
                                    label = LocalStrings.current.actionSwitchToClassicMode,
                                )
                            val nodeName = uiState.user?.handle.nodeName
                            if (nodeName != null && nodeName != uiState.currentNode) {
                                this +=
                                    OptionId.AddShortcut.toOption(
                                        LocalStrings.current.actionShortcut(nodeName),
                                    )
                            }
                            uiState.user?.notificationStatus?.also { notificationStatus ->
                                when (notificationStatus) {
                                    NotificationStatus.Disabled -> {
                                        this += CustomOptions.EnableNotifications.toOption(
                                            label = LocalStrings.current.actionEnableNotifications,
                                        )
                                    }

                                    NotificationStatus.Enabled -> {
                                        this += CustomOptions.DisableNotifications.toOption(
                                            label = LocalStrings.current.actionDisableNotifications,
                                        )
                                    }
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
                                imageVector = LocalResources.current.moreVert,
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
                                            CustomOptions.SwitchToClassicMode -> {
                                                uiState.user?.also { user ->
                                                    mainRouter.switchUserDetailToClassicMode(
                                                        user = user,
                                                        otherInstance = otherInstance,
                                                    )
                                                }
                                            }

                                            OptionId.AddShortcut ->
                                                model.reduce(
                                                    ForumListMvi.Intent.AddInstanceShortcut(
                                                        uiState.user
                                                            ?.handle.nodeName
                                                            .orEmpty(),
                                                    ),
                                                )

                                            CustomOptions.EnableNotifications -> {
                                                model.reduce(ForumListMvi.Intent.EnableNotifications)
                                            }

                                            CustomOptions.DisableNotifications -> {
                                                confirmMuteNotificationsDialogOpen = true
                                            }

                                            else -> Unit
                                        }
                                    },
                                )
                            }
                        }
                    }

                    if (isWidthSizeClassEqualOrAbove(WindowWidthSizeClass.Expanded) &&
                        uiState.currentUserId != null &&
                        isHomeInstance
                    ) {
                        IconButton(
                            shape = MaterialTheme.shapes.small,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(),
                            onClick = {
                                mainRouter.openComposer(
                                    inReplyToUser = uiState.user,
                                    inGroup = true,
                                )
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
            if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded) &&
                uiState.currentUserId != null &&
                isHomeInstance
            ) {
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
                                inGroup = true,
                            )
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
                    if (uiState.hideNavigationBarWhileScrolling) {
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    } else {
                        Modifier
                    },
                ).nestedScroll(fabNestedScrollConnection),
            isRefreshing = uiState.refreshing,
            onRefresh = {
                model.reduce(ForumListMvi.Intent.Refresh)
            },
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                if (uiState.user != null) {
                    item {
                        GroupHeader(
                            user = uiState.user?.let { u ->
                                u.copy(relationshipStatus = u.relationshipStatus.takeIf { isHomeInstance })
                            },
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
                            onRelationshipClick = { nextAction: RelationshipStatusNextAction ->
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
                                        model.reduce(ForumListMvi.Intent.Follow)
                                    }

                                    RelationshipStatusNextAction.Unfollow -> {
                                        model.reduce(ForumListMvi.Intent.Unfollow)
                                    }
                                }
                            }.takeIf { isHomeInstance },
                            onOpenFollowers = {
                                uiState.user?.also { user ->
                                    mainRouter.openFollowers(user = user, otherInstance = otherInstance)
                                }
                            },
                        )
                    }
                } else {
                    item {
                        GenericPlaceholder(modifier = Modifier.fillMaxWidth().height(100.dp))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(Spacing.xs))
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
                    items = uiState.posts,
                    key = { _, post -> "forum-list-${post.safeKey}" },
                ) { idx, post ->
                    TimelineItem(
                        post = post,
                        layout = uiState.layout,
                        reshareAndReplyVisible = false,
                        followedHashtagsVisible = false,
                        blurNsfw = uiState.blurNsfw,
                        autoloadImages = uiState.autoloadImages,
                        maxBodyLines = uiState.maxBodyLines,
                        pollEnabled = isHomeInstance,
                        onClick = {
                            model.reduce(ForumListMvi.Intent.WillOpenDetail(post))
                        },
                        onOpenUrl = { url, allowOpenInternal ->
                            if (allowOpenInternal) {
                                uriHandler.openUri(url)
                            } else {
                                uriHandler.openExternally(url)
                            }
                        },
                        onOpenUser = { user ->
                            mainRouter.openUserDetail(user = user, otherInstance = otherInstance)
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
                                        ForumListMvi.Intent.ToggleReblog(post),
                                    )
                            }
                        }.takeIf { post.original.actionPermissions.canReblog && isHomeInstance },
                        onBookmark =
                        { post: PostModel ->
                            model.reduce(ForumListMvi.Intent.ToggleBookmark(post))
                        }.takeIf { post.original.actionPermissions.canBookmark && isHomeInstance },
                        onFavorite =
                        { post: PostModel ->
                            model.reduce(ForumListMvi.Intent.ToggleFavorite(post))
                        }.takeIf { post.original.actionPermissions.canFavorite && isHomeInstance },
                        onDislike =
                        { post: PostModel ->
                            model.reduce(ForumListMvi.Intent.ToggleDislike(post))
                        }.takeIf { post.original.actionPermissions.canDislike && isHomeInstance },
                        onReply =
                        { post: PostModel ->
                            mainRouter.openComposer(
                                inReplyTo = post,
                                inReplyToUser = post.creator,
                            )
                        }.takeIf { post.original.actionPermissions.canReply && isHomeInstance },
                        onPollVote =
                        uiState.currentUserId?.let {
                            { post, choices ->
                                model.reduce(
                                    ForumListMvi.Intent.SubmitPollVote(
                                        post = post,
                                        choices = choices,
                                    ),
                                )
                            }
                        },
                        onShowOriginal = {
                            model.reduce(
                                ForumListMvi.Intent.ToggleTranslation(post.original),
                            )
                        },
                        onOpenQuote = { post ->
                            mainRouter.openPostDetail(post)
                        },
                        options =
                        buildList {
                            if (post.original.actionPermissions.canShare && isHomeInstance) {
                                this += OptionId.Share.toOption()
                                this += OptionId.CopyUrl.toOption()
                            }
                            if (post.original.actionPermissions.canEdit && isHomeInstance) {
                                this += OptionId.Edit.toOption()
                            }
                            if (post.original.actionPermissions.canDelete && isHomeInstance) {
                                this += OptionId.Delete.toOption()
                            }
                            if (post.original.actionPermissions.canMute && isHomeInstance) {
                                this += OptionId.Mute.toOption()
                            }
                            if (post.original.actionPermissions.canBlock && isHomeInstance) {
                                this += OptionId.Block.toOption()
                            }
                            if (post.original.actionPermissions.canReport && isHomeInstance) {
                                this += OptionId.ReportUser.toOption()
                                this += OptionId.ReportPost.toOption()
                            }
                            if (post.original.actionPermissions.canQuote && isHomeInstance) {
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
                                    mainRouter.openComposer(
                                        inReplyToUser = post.creator,
                                        editedPostId = post.reblog?.id,
                                        inGroup = true,
                                    )
                                }

                                OptionId.Delete -> confirmDeletePostId = post.id

                                OptionId.Mute -> confirmMutePost = post

                                OptionId.Block -> confirmBlockPost = post

                                OptionId.ReportUser ->
                                    post.original.creator?.also { toReport ->
                                        mainRouter.openCreateReport(user = toReport)
                                    }

                                OptionId.ReportPost ->
                                    post.original.also { postToReport ->
                                        postToReport.creator?.also { userToReport ->
                                            mainRouter.openCreateReport(
                                                user = userToReport,
                                                post = postToReport,
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
                                    model.reduce(ForumListMvi.Intent.CopyToClipboard(post.original))

                                OptionId.Translate ->
                                    model.reduce(
                                        ForumListMvi.Intent.ToggleTranslation(post.original),
                                    )

                                OptionId.AddShortcut ->
                                    model.reduce(
                                        ForumListMvi.Intent.AddInstanceShortcut(post.nodeName),
                                    )

                                OptionId.OpenInBrowser ->
                                    model.reduce(
                                        ForumListMvi.Intent.OpenInBrowser(post),
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
                        model.reduce(ForumListMvi.Intent.LoadNextPage)
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
                    model.reduce(ForumListMvi.Intent.Unfollow)
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
                    model.reduce(ForumListMvi.Intent.Unfollow)
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
                    model.reduce(ForumListMvi.Intent.DisableNotifications)
                }
            },
        )
    }

    if (confirmDeletePostId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val postId = confirmDeletePostId
                confirmDeletePostId = null
                if (confirm && postId != null) {
                    model.reduce(ForumListMvi.Intent.Delete(postId))
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
                                ForumListMvi.Intent.MuteUser(
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
                val creatorId = creator?.id
                confirmBlockPost = null
                if (confirm && postId != null && creatorId != null) {
                    model.reduce(
                        ForumListMvi.Intent.BlockUser(
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
                val e = confirmReblogPost
                confirmReblogPost = null
                if (confirm && e != null) {
                    model.reduce(ForumListMvi.Intent.ToggleReblog(e))
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

private sealed interface CustomOptions : OptionId.Custom {
    data object SwitchToClassicMode : CustomOptions
    data object EnableNotifications : CustomOptions
    data object DisableNotifications : CustomOptions
}
