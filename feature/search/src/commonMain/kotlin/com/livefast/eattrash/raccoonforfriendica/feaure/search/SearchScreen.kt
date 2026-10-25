package com.livefast.eattrash.raccoonforfriendica.feaure.search

import androidx.compose.foundation.background
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.rememberMaxTopBarInset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SearchField
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.HashtagItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PostDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.LocalUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.getAnimatedDots
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldPost
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.SearchSection
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.toReadableName
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(modifier: Modifier = Modifier, model: SearchMvi = metroViewModel<SearchViewModel>()) {
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    val canPopState by navigationCoordinator.canPop.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val uriHandler = LocalUriHandler.current
    val mainRouter = LocalUiDeps.current.mainRouter
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val shareHelper = LocalUiDeps.current.shareHelper
    val searchFieldFocusRequester = remember { FocusRequester() }
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboardHelper = LocalUiDeps.current.getClipboardHelper(LocalClipboard.current)
    val isScrollInProgress = lazyListState.isScrollInProgress
    var firstOpening by rememberSaveable { mutableStateOf(true) }
    var confirmUnfollowDialogUserId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteFollowRequestDialogUserId by remember { mutableStateOf<String?>(null) }
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
                    SearchMvi.Effect.BackToTop -> goBackToTop()

                    SearchMvi.Effect.PollVoteFailure -> pollErrorDialogOpened = true

                    is SearchMvi.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is SearchMvi.Effect.OpenUrl -> uriHandler.openExternally(event.url)
                }
            }.launchIn(this)
    }
    LaunchedEffect(firstOpening) {
        if (firstOpening) {
            searchFieldFocusRequester.requestFocus()
            firstOpening = false
        }
    }
    LaunchedEffect(isScrollInProgress) {
        if (isScrollInProgress) {
            focusManager.clearFocus()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
                scrollBehavior = scrollBehavior,
                title = {
                    SearchField(
                        modifier = Modifier.focusRequester(searchFieldFocusRequester),
                        value = uiState.query,
                        hint = LocalStrings.current.searchPlaceholder,
                        onClear = {
                            model.reduce(SearchMvi.Intent.SetSearch(""))
                        },
                        onValueChange = {
                            model.reduce(SearchMvi.Intent.SetSearch(it))
                        },
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
                    if (uiState.hideNavigationBarWhileScrolling) {
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    } else {
                        Modifier
                    },
                ),
            isRefreshing = uiState.refreshing,
            onRefresh = {
                model.reduce(SearchMvi.Intent.Refresh)
            },
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                stickyHeader {
                    val titles =
                        listOf(
                            SearchSection.Posts,
                            SearchSection.Users,
                            SearchSection.Hashtags,
                            SearchSection.Groups,
                        )
                    val maxInset = rememberMaxTopBarInset()
                    SectionSelector(
                        modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(
                                top = maxInset * topAppBarState.collapsedFraction,
                                bottom = Spacing.s,
                            ),
                        titles = titles.map { it.toReadableName() },
                        currentSection = titles.indexOf(uiState.section),
                        onSelectSection = {
                            model.reduce(
                                SearchMvi.Intent.ChangeSection(titles[it]),
                            )
                        },
                    )
                }

                if (uiState.initial) {
                    val placeholderCount = 20
                    items(placeholderCount) { idx ->
                        val placeholderModifier = Modifier.fillMaxWidth()
                        when (uiState.section) {
                            SearchSection.Hashtags -> {
                                GenericPlaceholder(modifier = placeholderModifier)
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            SearchSection.Posts -> {
                                TimelineItemPlaceholder(modifier = placeholderModifier)
                                if (idx < placeholderCount - 1) {
                                    TimelineDivider(layout = uiState.layout)
                                }
                            }

                            SearchSection.Users, SearchSection.Groups -> {
                                UserItemPlaceholder(modifier = placeholderModifier)
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }
                        }
                    }
                }

                if (!uiState.initial &&
                    !uiState.refreshing &&
                    !uiState.loading &&
                    !uiState.earlyLoading &&
                    uiState.items.isEmpty()
                ) {
                    item {
                        if (uiState.query.isEmpty()) {
                            val animatingPart = getAnimatedDots()
                            Text(
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = Spacing.m,
                                        start = Spacing.s,
                                    ),
                                text =
                                buildString {
                                    append("🔦")
                                    append(" ")
                                    append(LocalStrings.current.messageSearchInitialEmpty)
                                    append(animatingPart)
                                },
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        } else {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                                text = LocalStrings.current.messageEmptyList,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }

                itemsIndexed(
                    items = uiState.items,
                    key = { _, item ->
                        when (item) {
                            is ExploreItemModel.Post -> "search-${item.post.safeKey}"
                            else -> "search-${item.id}"
                        }
                    },
                ) { idx, item ->
                    when (item) {
                        is ExploreItemModel.Post -> {
                            TimelineItem(
                                post = item.post,
                                layout = uiState.layout,
                                blurNsfw = uiState.blurNsfw,
                                autoloadImages = uiState.autoloadImages,
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
                                                SearchMvi.Intent.ToggleReblog(post),
                                            )
                                    }
                                }.takeIf { item.post.original.actionPermissions.canReblog },
                                onBookmark =
                                { post: PostModel ->
                                    model.reduce(SearchMvi.Intent.ToggleBookmark(post))
                                }.takeIf { item.post.original.actionPermissions.canBookmark },
                                onFavorite =
                                { post: PostModel ->
                                    model.reduce(SearchMvi.Intent.ToggleFavorite(post))
                                }.takeIf { item.post.original.actionPermissions.canFavorite },
                                onDislike =
                                { post: PostModel ->
                                    model.reduce(SearchMvi.Intent.ToggleDislike(post))
                                }.takeIf { item.post.original.actionPermissions.canDislike },
                                onReply =
                                { post: PostModel ->
                                    mainRouter.openComposer(
                                        inReplyTo = post,
                                        inReplyToUser = post.creator,
                                    )
                                }.takeIf { item.post.original.actionPermissions.canReply },
                                onPollVote =
                                uiState.currentUserId?.let {
                                    { post, choices ->
                                        model.reduce(
                                            SearchMvi.Intent.SubmitPollVote(
                                                post = post,
                                                choices = choices,
                                            ),
                                        )
                                    }
                                },
                                onShowOriginal = {
                                    model.reduce(
                                        SearchMvi.Intent.ToggleTranslation(
                                            post = item.post.original,
                                        ),
                                    )
                                },
                                onOpenQuote = { post ->
                                    mainRouter.openPostDetail(post)
                                },
                                options =
                                buildList {
                                    val post = item.post
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
                                            val urlString = item.post.url.orEmpty()
                                            shareHelper.share(urlString)
                                        }

                                        OptionId.CopyUrl -> {
                                            val urlString = item.post.url.orEmpty()
                                            scope.launch {
                                                clipboardHelper.setText(urlString)
                                                snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                            }
                                        }

                                        OptionId.Edit -> {
                                            (
                                                item.post.reblog
                                                    ?: item.post
                                                ).also { toEdit ->
                                                mainRouter.openComposer(
                                                    inReplyTo = toEdit.inReplyTo,
                                                    inReplyToUser = toEdit.inReplyTo?.creator,
                                                    editedPostId = toEdit.id,
                                                )
                                            }
                                        }

                                        OptionId.Delete -> confirmDeletePostId = item.post.id

                                        OptionId.Mute -> confirmMutePost = item.post

                                        OptionId.Block -> confirmBlockPost = item.post

                                        OptionId.Pin, OptionId.Unpin ->
                                            model.reduce(
                                                SearchMvi.Intent.TogglePin(item.post),
                                            )

                                        OptionId.ReportUser ->
                                            item.post.original.creator?.also { userToReport ->
                                                mainRouter.openCreateReport(user = userToReport)
                                            }

                                        OptionId.ReportPost ->
                                            item.post.original.also { postToReport ->
                                                postToReport.creator?.also { userToReport ->
                                                    mainRouter.openCreateReport(
                                                        user = userToReport,
                                                        post = postToReport,
                                                    )
                                                }
                                            }

                                        OptionId.ViewDetails -> seeDetailsPost = item.post.original

                                        OptionId.Quote -> {
                                            item.post.original.also { toShare ->
                                                mainRouter.openComposer(
                                                    quoted = toShare,
                                                    urlToShare = toShare.url,
                                                )
                                            }
                                        }

                                        OptionId.CopyToClipboard ->
                                            model.reduce(
                                                SearchMvi.Intent.CopyToClipboard(
                                                    item.post.original,
                                                ),
                                            )

                                        OptionId.Translate ->
                                            model.reduce(
                                                SearchMvi.Intent.ToggleTranslation(
                                                    item.post.original,
                                                ),
                                            )

                                        OptionId.AddShortcut ->
                                            model.reduce(
                                                SearchMvi.Intent.AddInstanceShortcut(
                                                    item.post.nodeName,
                                                ),
                                            )

                                        OptionId.OpenInBrowser ->
                                            model.reduce(
                                                SearchMvi.Intent.OpenInBrowser(item.post),
                                            )

                                        else -> Unit
                                    }
                                },
                            )
                            if (idx < uiState.items.lastIndex) {
                                TimelineDivider(layout = uiState.layout)
                            }
                        }

                        is ExploreItemModel.HashTag -> {
                            HashtagItem(
                                hashtag = item.hashtag,
                                onOpen = {
                                    mainRouter.openHashtag(it)
                                },
                            )
                            Spacer(modifier = Modifier.height(Spacing.interItem))
                        }

                        is ExploreItemModel.User -> {
                            UserItem(
                                user = item.user,
                                autoloadImages = uiState.autoloadImages,
                                onClick = {
                                    mainRouter.openUserDetail(item.user)
                                },
                                onRelationshipClick = { nextAction: RelationshipStatusNextAction ->
                                    when (nextAction) {
                                        RelationshipStatusNextAction.AcceptRequest -> {
                                            mainRouter.openFollowRequests()
                                        }

                                        RelationshipStatusNextAction.ConfirmUnfollow -> {
                                            confirmUnfollowDialogUserId = item.user.id
                                        }

                                        RelationshipStatusNextAction.ConfirmDeleteFollowRequest -> {
                                            confirmDeleteFollowRequestDialogUserId =
                                                item.user.id
                                        }

                                        RelationshipStatusNextAction.Follow -> {
                                            model.reduce(SearchMvi.Intent.Follow(item.user.id))
                                        }

                                        RelationshipStatusNextAction.Unfollow -> {
                                            model.reduce(SearchMvi.Intent.Unfollow(item.user.id))
                                        }
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.height(Spacing.interItem))
                        }

                        else -> Unit
                    }

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    val isNearTheEnd = idx.isNearTheEnd(uiState.items)
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(SearchMvi.Intent.LoadNextPage)
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

    if (confirmUnfollowDialogUserId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionUnfollow,
            onClose = { confirm ->
                val userId = confirmUnfollowDialogUserId
                confirmUnfollowDialogUserId = null
                if (confirm && userId != null) {
                    model.reduce(SearchMvi.Intent.Unfollow(userId))
                }
            },
        )
    }

    if (confirmDeleteFollowRequestDialogUserId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDeleteFollowRequest,
            onClose = { confirm ->
                val userId = confirmUnfollowDialogUserId
                confirmUnfollowDialogUserId = null
                if (confirm && userId != null) {
                    model.reduce(SearchMvi.Intent.Unfollow(userId))
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
                    model.reduce(SearchMvi.Intent.Delete(postId))
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
                                SearchMvi.Intent.MuteUser(
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
                        SearchMvi.Intent.BlockUser(
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
                    model.reduce(SearchMvi.Intent.ToggleReblog(e))
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
