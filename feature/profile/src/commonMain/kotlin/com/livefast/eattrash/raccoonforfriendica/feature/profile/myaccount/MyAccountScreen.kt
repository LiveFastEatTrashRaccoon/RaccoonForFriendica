package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PostDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserFields
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.LocalUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import com.livefast.eattrash.raccoonforfriendica.feature.profile.LocalProfileTopAppBarStateWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun MyAccountScreen(
    model: MyAccountMvi,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    val connection = navigationCoordinator.getBottomBarScrollConnection()
    val uriHandler = LocalUriHandler.current
    val mainRouter = LocalUiDeps.current.mainRouter
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val shareHelper = LocalUiDeps.current.shareHelper
    val genericError = LocalStrings.current.messageGenericError
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboardHelper = LocalUiDeps.current.getClipboardHelper(LocalClipboard.current)
    var confirmDeletePostId by remember { mutableStateOf<String?>(null) }
    var confirmReblogPost by remember { mutableStateOf<PostModel?>(null) }
    val topAppBarState = LocalProfileTopAppBarStateWrapper.current.topAppBarState
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val stickyHeaderTopOffset by animateDpAsState(
        if (lazyListState.firstVisibleItemIndex >= 2) {
            Dimensions.maxTopBarInset * topAppBarState.collapsedFraction
        } else {
            0.dp
        },
    )
    var seeDetailsPost by remember { mutableStateOf<PostModel?>(null) }

    suspend fun goBackToTop() {
        try {
            if (lazyListState.firstVisibleItemIndex > 0) {
                if (uiState.posts.isEmpty()) {
                    lazyListState.scrollToItem(1)
                } else {
                    lazyListState.scrollToItem(2)
                }
            } else {
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
                    MyAccountMvi.Effect.BackToTop -> goBackToTop()
                    MyAccountMvi.Effect.Failure ->
                        snackbarHostState.showSnackbar(message = genericError)

                    is MyAccountMvi.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is MyAccountMvi.Effect.OpenUrl -> uriHandler.openExternally(event.url)
                }
            }.launchIn(this)
    }
    LaunchedEffect(navigationCoordinator) {
        navigationCoordinator.onDoubleTabSelection
            .onEach { section ->
                if (section == BottomNavigationSection.Profile) {
                    goBackToTop()
                }
            }.launchIn(this)
    }

    PullToRefreshBox(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (connection != null && uiState.hideNavigationBarWhileScrolling) {
                        Modifier.nestedScroll(connection)
                    } else {
                        Modifier
                    },
                ).then(
                    if (connection != null && uiState.hideNavigationBarWhileScrolling) {
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    } else {
                        Modifier
                    },
                ),
        isRefreshing = uiState.refreshing,
        onRefresh = {
            model.reduce(MyAccountMvi.Intent.Refresh)
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
                        editButtonEnabled = true,
                        onOpenUrl = { url, allowOpenInternal ->
                            if (allowOpenInternal) {
                                uriHandler.openUri(url)
                            } else {
                                uriHandler.openExternally(url)
                            }
                        },
                        onOpenFollowers = {
                            uiState.user?.also { user ->
                                mainRouter.openFollowers(
                                    user = user,
                                    enableExport = true,
                                )
                            }
                        },
                        onOpenFollowing = {
                            uiState.user?.also { user ->
                                mainRouter.openFollowing(
                                    user = user,
                                    enableExport = true,
                                )
                            }
                        },
                        onEditClick = {
                            mainRouter.openEditProfile()
                        },
                    )
                }
            } else if (uiState.initial) {
                item {
                    GenericPlaceholder(
                        modifier = Modifier.fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .padding(bottom = Spacing.xs),
                    )
                }
            } else {
                item {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.s),
                        textAlign = TextAlign.Center,
                        text = LocalStrings.current.messageAuthIssue,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    val annotatedString =
                        buildAnnotatedString {
                            val linkStyle =
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                )
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                                append(LocalStrings.current.messageAuthIssueHintsTitle)
                                append("\n• ")
                                pushLink(
                                    LinkAnnotation.Clickable(
                                        tag = "action-refresh",
                                        linkInteractionListener = {
                                            model.reduce(MyAccountMvi.Intent.Refresh)
                                        },
                                    ),
                                )
                                withStyle(linkStyle) {
                                    append(LocalStrings.current.messageAuthIssueHint1)
                                }
                                pop()
                                append("\n• ")
                                pushLink(
                                    LinkAnnotation.Clickable(
                                        tag = "action-login",
                                        linkInteractionListener = {
                                            model.reduce(MyAccountMvi.Intent.Logout)
                                        },
                                    ),
                                )
                                withStyle(linkStyle) {
                                    append(LocalStrings.current.messageAuthIssueHint2)
                                }
                                pop()
                                append("\n• ")
                                append(LocalStrings.current.messageAuthIssueHint3)
                            }
                        }
                    Box(
                        modifier =
                            Modifier
                                .padding(
                                    vertical = Spacing.m,
                                    horizontal = Spacing.s,
                                ).border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = RoundedCornerShape(CornerSize.l),
                                ).padding(
                                    vertical = Spacing.s,
                                    horizontal = Spacing.m,
                                ),
                    ) {
                        Text(
                            modifier =
                                Modifier.fillMaxWidth(),
                            text = annotatedString,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
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
                            MyAccountMvi.Intent.ChangeSection(titles[it]),
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
                items = uiState.posts,
                key = { _, post -> "my-account-${post.safeKey}" },
            ) { idx, post ->
                TimelineItem(
                    post = post,
                    layout = uiState.layout,
                    blurNsfw = uiState.blurNsfw,
                    autoloadImages = uiState.autoloadImages,
                    pollEnabled = false,
                    maxBodyLines = uiState.maxBodyLines,
                    onClick = { post ->
                        mainRouter.openPostDetail(post)
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
                    onBookmark =
                        { post: PostModel ->
                            model.reduce(MyAccountMvi.Intent.ToggleBookmark(post))
                        }.takeIf { post.original.actionPermissions.canBookmark },
                    onFavorite =
                        { post: PostModel ->
                            model.reduce(MyAccountMvi.Intent.ToggleFavorite(post))
                        }.takeIf { post.original.actionPermissions.canFavorite },
                    onDislike =
                        { post: PostModel ->
                            model.reduce(MyAccountMvi.Intent.ToggleDislike(post))
                        }.takeIf { post.original.actionPermissions.canDislike },
                    onReply =
                        { post: PostModel ->
                            mainRouter.openComposer(
                                inReplyTo = post,
                                inReplyToUser = post.creator,
                            )
                        }.takeIf { post.original.actionPermissions.canReply },
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
                            if (post.original.actionPermissions.canQuote) {
                                this += OptionId.Quote.toOption()
                            }
                            this += OptionId.ViewDetails.toOption()
                            this += OptionId.CopyToClipboard.toOption()
                            this += OptionId.OpenInBrowser.toOption()
                        },
                    onSelectOption = { optionId ->
                        when (optionId) {
                            OptionId.Edit -> {
                                if (post.creator?.group == true) {
                                    // edit the original post reblogged by the group
                                    mainRouter.openComposer(
                                        inReplyTo = post.inReplyTo,
                                        inReplyToUser = post.creator,
                                        editedPostId = post.reblog?.id,
                                        inGroup = true,
                                    )
                                } else {
                                    mainRouter.openComposer(
                                        inReplyTo = post.inReplyTo,
                                        inReplyToUser = post.inReplyTo?.creator,
                                        editedPostId = post.id,
                                    )
                                }
                            }

                            OptionId.Delete -> confirmDeletePostId = post.id
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

                            OptionId.Pin, OptionId.Unpin ->
                                model.reduce(
                                    MyAccountMvi.Intent.TogglePin(post),
                                )

                            OptionId.ViewDetails -> seeDetailsPost = post.original
                            OptionId.Quote -> {
                                post.original.also { toShare ->
                                    mainRouter.openComposer(
                                        quoted = toShare,
                                        urlToShare = toShare.url,
                                    )
                                }
                            }

                            OptionId.CopyToClipboard ->
                                model.reduce(
                                    MyAccountMvi.Intent.CopyToClipboard(post.original),
                                )

                            OptionId.OpenInBrowser ->
                                model.reduce(
                                    MyAccountMvi.Intent.OpenInBrowser(post),
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
                    model.reduce(MyAccountMvi.Intent.LoadNextPage)
                }
            }
            if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.posts.isEmpty() &&
                uiState.user != null
            ) {
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

        SnackbarHost(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Dimensions.floatingActionButtonBottomInset),
            hostState = snackbarHostState,
        ) { data ->
            Snackbar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                snackbarData = data,
            )
        }
    }

    if (confirmDeletePostId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val postId = confirmDeletePostId
                confirmDeletePostId = null
                if (confirm && postId != null) {
                    model.reduce(MyAccountMvi.Intent.Delete(postId))
                }
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
                    model.reduce(MyAccountMvi.Intent.ToggleReblog(e))
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
