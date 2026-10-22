package com.livefast.eattrash.raccoonforfriendica.feature.thread

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PostDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineReplyItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.LocalUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.clickableWithoutFocus
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassEqualOrAbove
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
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
fun ThreadScreen(
    postId: String,
    swipeNavigationEnabled: Boolean,
    modifier: Modifier = Modifier,
    otherInstance: String? = null,
) {
    val model: ThreadMvi = assistedMetroViewModel<ThreadViewModel>(
        extras = ThreadViewModel.getExtras(
            postId = postId,
            swipeNavigationEnabled = swipeNavigationEnabled,
        ),
    )
    val uiState by model.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    val canPopState by navigationCoordinator.canPop.collectAsState()
    val uriHandler = LocalUriHandler.current
    val mainRouter = LocalUiDeps.current.mainRouter
    val scope = rememberCoroutineScope()
    val fabNestedScrollConnection = LocalUiDeps.current.fabNestedScrollConnection
    val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val shareHelper = LocalUiDeps.current.shareHelper
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboardHelper = LocalUiDeps.current.getClipboardHelper(LocalClipboard.current)
    var confirmDeletePostId by remember { mutableStateOf<String?>(null) }
    var confirmMutePost by remember { mutableStateOf<PostModel?>(null) }
    var confirmBlockPost by remember { mutableStateOf<PostModel?>(null) }
    var confirmReblogPost by remember { mutableStateOf<PostModel?>(null) }
    var pollErrorDialogOpened by remember { mutableStateOf(false) }
    var seeDetailsPost by remember { mutableStateOf<PostModel?>(null) }
    var lazyListState = rememberLazyListState()
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
                    ThreadMvi.Effect.PollVoteFailure -> pollErrorDialogOpened = true

                    is ThreadMvi.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is ThreadMvi.Effect.OpenUrl -> uriHandler.openExternally(event.url)
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
                        text = LocalStrings.current.threadTitle,
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
                    if (isWidthSizeClassEqualOrAbove(WindowWidthSizeClass.Expanded) && uiState.currentUserId != null) {
                        IconButton(
                            shape = MaterialTheme.shapes.small,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(),
                            onClick = {
                                val mainPost = uiState.mainPosts.getOrNull(uiState.currentIndex)
                                if (mainPost != null) {
                                    mainRouter.openComposer(
                                        inReplyTo = mainPost,
                                        inReplyToUser = mainPost.creator,
                                    )
                                }
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.reply,
                                contentDescription = LocalStrings.current.actionReply,
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
                        onClick = {
                            val mainPost = uiState.mainPosts.getOrNull(uiState.currentIndex)
                            if (mainPost != null) {
                                mainRouter.openComposer(
                                    inReplyTo = mainPost,
                                    inReplyToUser = mainPost.creator,
                                )
                            }
                        },
                    ) {
                        Icon(
                            imageVector = LocalResources.current.reply,
                            contentDescription = LocalStrings.current.actionReply,
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
        val pageCount = uiState.mainPosts.size
        val pagerState =
            remember(pageCount) {
                PagerState(
                    currentPage = uiState.currentIndex,
                    currentPageOffsetFraction = 0f,
                    pageCount = { pageCount },
                )
            }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .onEach {
                    model.reduce(ThreadMvi.Intent.ChangeNavigationIndex(it))
                    goBackToTop()
                }.launchIn(this)
        }
        HorizontalPager(
            modifier =
            Modifier
                .padding(padding)
                .fillMaxSize(),
            state = pagerState,
            beyondViewportPageCount = 1,
        ) { index ->
            val mainPost = uiState.mainPosts.getOrNull(index)
            val replies = uiState.replies.getOrNull(index).orEmpty()
            lazyListState = rememberLazyListState()
            PullToRefreshBox(
                modifier =
                Modifier
                    .then(
                        if (uiState.hideNavigationBarWhileScrolling) {
                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        } else {
                            Modifier
                        },
                    ).nestedScroll(fabNestedScrollConnection),
                isRefreshing = uiState.refreshing,
                onRefresh = {
                    model.reduce(ThreadMvi.Intent.Refresh)
                },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                ) {
                    // original post
                    mainPost?.also { post ->
                        item {
                            TimelineItem(
                                modifier =
                                Modifier
                                    // since the main post is forced to "full", recreates card appearance
                                    .padding(horizontal = Spacing.xs)
                                    .padding(bottom = Spacing.xs)
                                    .shadow(
                                        elevation = 5.dp,
                                        shape = RoundedCornerShape(CornerSize.l),
                                    ).background(
                                        color =
                                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                                            5.dp,
                                        ),
                                        shape = RoundedCornerShape(CornerSize.l),
                                    ).padding(
                                        vertical = Spacing.xs,
                                        horizontal = Spacing.xxxs,
                                    ),
                                post = post,
                                layout = TimelineLayout.Full,
                                reshareAndReplyVisible = false,
                                followedHashtagsVisible = false,
                                extendedSocialInfoEnabled = true,
                                blurNsfw = uiState.blurNsfw,
                                autoloadImages = uiState.autoloadImages,
                                pollEnabled = isHomeInstance,
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
                                onBookmark =
                                { post: PostModel ->
                                    model.reduce(ThreadMvi.Intent.ToggleBookmark(post))
                                }.takeIf { post.original.actionPermissions.canBookmark && isHomeInstance },
                                onFavorite =
                                { post: PostModel ->
                                    model.reduce(ThreadMvi.Intent.ToggleFavorite(post))
                                }.takeIf { post.original.actionPermissions.canFavorite && isHomeInstance },
                                onDislike =
                                { post: PostModel ->
                                    model.reduce(ThreadMvi.Intent.ToggleDislike(post))
                                }.takeIf { post.original.actionPermissions.canDislike && isHomeInstance },
                                onOpenUsersFavorite = { post ->
                                    mainRouter.openPostUsersFavorite(
                                        postId = post.id,
                                        count = post.favoriteCount,
                                        otherInstance = otherInstance,
                                    )
                                },
                                onOpenUsersReblog = { post ->
                                    mainRouter.openPostUsersReblog(
                                        postId = post.id,
                                        count = post.reblogCount,
                                        otherInstance = otherInstance,
                                    )
                                },
                                onPollVote =
                                uiState.currentUserId?.let {
                                    { post, choices ->
                                        model.reduce(
                                            ThreadMvi.Intent.SubmitPollVote(
                                                post = post,
                                                choices = choices,
                                            ),
                                        )
                                    }
                                },
                                onShowOriginal = {
                                    model.reduce(
                                        ThreadMvi.Intent.ToggleTranslation(post.original),
                                    )
                                },

                                options =
                                buildList {
                                    if (post.original.actionPermissions.canShare) {
                                        this += OptionId.Share.toOption()
                                        this += OptionId.CopyUrl.toOption()
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

                                        OptionId.ViewDetails ->
                                            seeDetailsPost =
                                                post.original

                                        OptionId.Quote -> {
                                            post.original.also { toShare ->
                                                mainRouter.openComposer(
                                                    quoted = toShare,
                                                    urlToShare = toShare.url,
                                                )
                                            }
                                        }

                                        OptionId.CopyToClipboard ->
                                            post.original.also { post ->
                                                model.reduce(ThreadMvi.Intent.CopyToClipboard(post))
                                            }

                                        OptionId.Translate ->
                                            post.original.also { post ->
                                                model.reduce(ThreadMvi.Intent.ToggleTranslation(post))
                                            }

                                        OptionId.AddShortcut ->
                                            model.reduce(
                                                ThreadMvi.Intent.AddInstanceShortcut(post.nodeName),
                                            )

                                        OptionId.OpenInBrowser ->
                                            model.reduce(
                                                ThreadMvi.Intent.OpenInBrowser(post),
                                            )

                                        else -> Unit
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.height(Spacing.s))
                        }
                    }

                    // here to show the initial post from cache at startup
                    if (uiState.initial) {
                        val placeholderCount = 5
                        items(placeholderCount) { idx ->
                            TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                            if (idx < placeholderCount - 1) {
                                TimelineDivider(layout = uiState.layout)
                            }
                        }
                    }

                    // replies
                    itemsIndexed(
                        items = replies,
                        key = { _, post -> "thread-${post.safeKey}" },
                    ) { idx, post ->
                        TimelineReplyItem(
                            post = post,
                            layout = uiState.layout,
                            autoloadImages = uiState.autoloadImages,
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
                                            ThreadMvi.Intent.ToggleReblog(post),
                                        )
                                }
                            }.takeIf { post.original.actionPermissions.canReblog && isHomeInstance },
                            onBookmark =
                            { post: PostModel ->
                                model.reduce(ThreadMvi.Intent.ToggleBookmark(post))
                            }.takeIf { post.original.actionPermissions.canBookmark && isHomeInstance },
                            onFavorite =
                            { post: PostModel ->
                                model.reduce(ThreadMvi.Intent.ToggleFavorite(post))
                            }.takeIf { post.original.actionPermissions.canFavorite && isHomeInstance },
                            onDislike =
                            { post: PostModel ->
                                model.reduce(ThreadMvi.Intent.ToggleDislike(post))
                            }.takeIf { post.original.actionPermissions.canDislike && isHomeInstance },
                            onReply =
                            { post: PostModel ->
                                mainRouter.openComposer(
                                    inReplyTo = post,
                                    inReplyToUser = post.creator,
                                )
                            }.takeIf { post.original.actionPermissions.canReply && isHomeInstance },
                            onShowOriginal = {
                                model.reduce(
                                    ThreadMvi.Intent.ToggleTranslation(post.original),
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

                                    OptionId.ViewDetails -> seeDetailsPost = post.original

                                    OptionId.Quote -> {
                                        post.original.also { toShare ->
                                            mainRouter.openComposer(
                                                quoted = toShare,
                                            )
                                        }
                                    }

                                    OptionId.CopyToClipboard ->
                                        model.reduce(ThreadMvi.Intent.CopyToClipboard(post.original))

                                    OptionId.Translate ->
                                        model.reduce(
                                            ThreadMvi.Intent.ToggleTranslation(post.original),
                                        )

                                    OptionId.AddShortcut ->
                                        model.reduce(
                                            ThreadMvi.Intent.AddInstanceShortcut(post.nodeName),
                                        )

                                    OptionId.OpenInBrowser ->
                                        model.reduce(
                                            ThreadMvi.Intent.OpenInBrowser(post),
                                        )

                                    else -> Unit
                                }
                            },
                        )

                        // load more button
                        if (post.loadMoreButtonVisible) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Button(
                                    onClick = {
                                        model.reduce(
                                            ThreadMvi.Intent.LoadMoreReplies(post),
                                        )
                                    },
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        if (post.loadMoreButtonLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(IconSize.s),
                                                color = MaterialTheme.colorScheme.onPrimary,
                                            )
                                        }
                                        Text(
                                            text =
                                            buildString {
                                                append(LocalStrings.current.buttonLoadMoreReplies)
                                                post.replyCount
                                                    .takeIf { it > 0 }
                                                    ?.also { count ->
                                                        append(" (")
                                                        append(count)
                                                        append(")")
                                                    }
                                            },
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                        if (idx < replies.lastIndex) {
                            TimelineDivider(layout = uiState.layout)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(Spacing.xxxl))
                    }
                }
            }
        }
    }

    if (confirmDeletePostId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val deletePostId = confirmDeletePostId
                confirmDeletePostId = null
                if (confirm && deletePostId != null) {
                    model.reduce(ThreadMvi.Intent.Delete(deletePostId))
                }
            },
        )
    }

    if (confirmMutePost != null) {
        (confirmMutePost?.reblog?.creator ?: confirmMutePost?.creator)?.also { user ->
            ConfirmMuteUserBottomSheet(
                userHandle = user.handle.orEmpty(),
                onClose = { result ->
                    val mutePostId = confirmMutePost?.id
                    confirmMutePost = null
                    if (result != null) {
                        val (duration, disableNotifications) = result
                        if (mutePostId != null) {
                            model.reduce(
                                ThreadMvi.Intent.MuteUser(
                                    userId = user.id,
                                    postId = mutePostId,
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
                val blockPostId = confirmBlockPost?.id
                val creatorId = creator?.id
                confirmBlockPost = null
                if (confirm && blockPostId != null && creatorId != null) {
                    model.reduce(
                        ThreadMvi.Intent.BlockUser(
                            userId = creatorId,
                            postId = blockPostId,
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
                    model.reduce(ThreadMvi.Intent.ToggleReblog(e))
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
