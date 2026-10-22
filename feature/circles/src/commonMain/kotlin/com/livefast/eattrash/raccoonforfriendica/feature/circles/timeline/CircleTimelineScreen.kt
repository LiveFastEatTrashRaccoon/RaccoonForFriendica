package com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
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
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.clickableWithoutFocus
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
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
fun CircleTimelineScreen(id: String, modifier: Modifier = Modifier) {
    val model: CircleTimelineMvi = assistedMetroViewModel<CircleTimelineViewModel>(
        extras = CircleTimelineViewModel.getExtras(id),
    )
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    val canPopState by navigationCoordinator.canPop.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val connection = navigationCoordinator.getBottomBarScrollConnection()
    val uriHandler = LocalUriHandler.current
    val mainRouter = LocalUiDeps.current.mainRouter
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
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
                    CircleTimelineMvi.Effect.BackToTop -> goBackToTop()

                    CircleTimelineMvi.Effect.PollVoteFailure ->
                        pollErrorDialogOpened =
                            true

                    is CircleTimelineMvi.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is CircleTimelineMvi.Effect.OpenDetail -> {
                        mainRouter.openPostDetail(
                            post = event.post,
                            swipeNavigationEnabled = true,
                        )
                    }

                    is CircleTimelineMvi.Effect.OpenUrl ->
                        uriHandler.openExternally(event.url)
                }
            }.launchIn(this)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
                modifier =
                Modifier.clickableWithoutFocus { scope.launch { goBackToTop() } },
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = uiState.circle?.name.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                ),
            isRefreshing = uiState.refreshing,
            onRefresh = {
                model.reduce(CircleTimelineMvi.Intent.Refresh)
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
                            model.reduce(CircleTimelineMvi.Intent.WillOpenDetail(post))
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
                                        CircleTimelineMvi.Intent.ToggleReblog(post),
                                    )
                            }
                        }.takeIf { post.original.actionPermissions.canReblog },
                        onBookmark =
                        { post: PostModel ->
                            model.reduce(CircleTimelineMvi.Intent.ToggleBookmark(post))
                        }.takeIf { post.original.actionPermissions.canBookmark },
                        onFavorite =
                        { post: PostModel ->
                            model.reduce(CircleTimelineMvi.Intent.ToggleFavorite(post))
                        }.takeIf { post.original.actionPermissions.canFavorite },
                        onDislike =
                        { post: PostModel ->
                            model.reduce(CircleTimelineMvi.Intent.ToggleDislike(post))
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
                                    CircleTimelineMvi.Intent.SubmitPollVote(
                                        post = post,
                                        choices = choices,
                                    ),
                                )
                            }
                        },
                        onShowOriginal = {
                            model.reduce(
                                CircleTimelineMvi.Intent.ToggleTranslation(post.original),
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
                                    model.reduce(CircleTimelineMvi.Intent.TogglePin(post))

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
                                        )
                                    }
                                }

                                OptionId.ViewDetails -> seeDetailsPost = post.original

                                OptionId.CopyToClipboard ->
                                    model.reduce(
                                        CircleTimelineMvi.Intent.CopyToClipboard(
                                            post.original,
                                        ),
                                    )

                                OptionId.Translate ->
                                    model.reduce(
                                        CircleTimelineMvi.Intent.ToggleTranslation(
                                            post.original,
                                        ),
                                    )

                                OptionId.AddShortcut ->
                                    model.reduce(
                                        CircleTimelineMvi.Intent.AddInstanceShortcut(
                                            post.nodeName,
                                        ),
                                    )

                                OptionId.OpenInBrowser ->
                                    model.reduce(
                                        CircleTimelineMvi.Intent.OpenInBrowser(post),
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
                        model.reduce(CircleTimelineMvi.Intent.LoadNextPage)
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

    if (confirmDeletePostId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val postId = confirmDeletePostId ?: ""
                confirmDeletePostId = null
                if (confirm && postId.isNotEmpty()) {
                    model.reduce(CircleTimelineMvi.Intent.Delete(postId))
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
                                CircleTimelineMvi.Intent.MuteUser(
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
                        CircleTimelineMvi.Intent.BlockUser(
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
                    model.reduce(CircleTimelineMvi.Intent.ToggleReblog(e))
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
