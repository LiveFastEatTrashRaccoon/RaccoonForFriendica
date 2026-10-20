package com.livefast.eattrash.raccoonforfriendica.feature.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ChangeInstanceDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.HashtagItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.LinkItem
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
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
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
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.toReadableName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    model: ExploreMvi,
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
    val snackbarHostState = remember { SnackbarHostState() }
    val shareHelper = LocalUiDeps.current.shareHelper
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboardHelper = LocalUiDeps.current.getClipboardHelper(LocalClipboard.current)
    var confirmUnfollowDialogUserId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteFollowRequestDialogUserId by remember { mutableStateOf<String?>(null) }
    var confirmDeletePostId by remember { mutableStateOf<String?>(null) }
    var confirmMutePost by remember { mutableStateOf<PostModel?>(null) }
    var confirmBlockPost by remember { mutableStateOf<PostModel?>(null) }
    var confirmReblogPost by remember { mutableStateOf<PostModel?>(null) }
    var pollErrorDialogOpened by remember { mutableStateOf(false) }
    var seeDetailsPost by remember { mutableStateOf<PostModel?>(null) }
    val isHomeInstance = uiState.otherInstance.isNullOrEmpty()
    var selectForeignInstanceOpened by remember { mutableStateOf(false) }

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
                    ExploreMvi.Effect.BackToTop -> goBackToTop()

                    ExploreMvi.Effect.PollVoteFailure -> pollErrorDialogOpened = true

                    is ExploreMvi.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is ExploreMvi.Effect.OpenUrl -> uriHandler.openExternally(event.url)

                    ExploreMvi.Effect.SelectForeignInstanceSuccess -> {
                        selectForeignInstanceOpened = false
                    }
                }
            }.launchIn(this)
    }
    LaunchedEffect(navigationCoordinator) {
        navigationCoordinator.onDoubleTabSelection
            .onEach { section ->
                if (section == BottomNavigationSection.Explore) {
                    goBackToTop()
                }
            }.launchIn(this)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = buildString {
                            append(LocalStrings.current.sectionTitleExplore)
                            if (!uiState.otherInstance.isNullOrEmpty()) {
                                append(" (")
                                append(uiState.otherInstance)
                                append(")")
                            }
                        },
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
                    if (uiState.currentUserId != null && isHomeInstance) {
                        // only logged users can call the v2/search API
                        IconButton(
                            onClick = {
                                mainRouter.openSearch()
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.search,
                                contentDescription = LocalStrings.current.actionSearch,
                            )
                        }
                    }
                    val options =
                        buildList {
                            if (!uiState.otherInstance.isNullOrEmpty()) {
                                this +=
                                    CustomOptions.BackToHomeInstance.toOption(
                                        label = LocalStrings.current.actionBackToHomeInstance,
                                    )
                            } else {
                                this +=
                                    CustomOptions.SelectForeignInstance.toOption(
                                        label = LocalStrings.current.actionSelectForeignInstance,
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
                                                CustomOptions.BackToHomeInstance -> {
                                                    model.reduce(ExploreMvi.Intent.ResetOtherInstance)
                                                }

                                                CustomOptions.SelectForeignInstance -> {
                                                    selectForeignInstanceOpened = true
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
                model.reduce(ExploreMvi.Intent.Refresh)
            },
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                stickyHeader {
                    SectionSelector(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(
                                top = Dimensions.maxTopBarInset * topAppBarState.collapsedFraction,
                                bottom = Spacing.s,
                            ),
                        titles =
                        uiState.availableSections.map {
                            it.toReadableName()
                        },
                        currentSection = uiState.availableSections.indexOf(uiState.section),
                        onSelectSection = {
                            model.reduce(
                                ExploreMvi.Intent.ChangeSection(uiState.availableSections[it]),
                            )
                        },
                    )
                }

                if (uiState.initial) {
                    val placeholderCount = 20
                    items(placeholderCount) { idx ->
                        val placeholderModifier = Modifier.fillMaxWidth()
                        when (uiState.section) {
                            ExploreSection.Hashtags -> {
                                GenericPlaceholder(modifier = placeholderModifier)
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            ExploreSection.Links -> {
                                GenericPlaceholder(modifier = placeholderModifier)
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            ExploreSection.Posts -> {
                                TimelineItemPlaceholder(modifier = placeholderModifier)
                                if (idx < placeholderCount - 1) {
                                    TimelineDivider(layout = uiState.layout)
                                }
                            }

                            ExploreSection.Suggestions -> {
                                UserItemPlaceholder(modifier = placeholderModifier)
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }
                        }
                    }
                }

                if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.items.isEmpty()) {
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
                    items = uiState.items,
                    key = { _, item ->
                        when (item) {
                            is ExploreItemModel.Post -> "explore-${item.post.safeKey}"
                            else -> "explore-${item.id}"
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
                                pollEnabled = isHomeInstance,
                                onClick = { post ->
                                    mainRouter.openPostDetail(post = post, otherInstance = uiState.otherInstance)
                                },
                                onOpenUrl = { url, allowOpenInternal ->
                                    if (allowOpenInternal) {
                                        uriHandler.openUri(url)
                                    } else {
                                        uriHandler.openExternally(url)
                                    }
                                },
                                onOpenUser = { user ->
                                    mainRouter.openUserDetail(user = user, otherInstance = uiState.otherInstance)
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
                                                ExploreMvi.Intent.ToggleReblog(post),
                                            )
                                    }
                                }.takeIf { item.post.original.actionPermissions.canReblog && isHomeInstance },
                                onBookmark =
                                { post: PostModel ->
                                    model.reduce(ExploreMvi.Intent.ToggleBookmark(post))
                                }.takeIf { item.post.original.actionPermissions.canBookmark && isHomeInstance },
                                onFavorite =
                                { post: PostModel ->
                                    model.reduce(ExploreMvi.Intent.ToggleFavorite(post))
                                }.takeIf { item.post.original.actionPermissions.canFavorite && isHomeInstance },
                                onDislike =
                                { post: PostModel ->
                                    model.reduce(ExploreMvi.Intent.ToggleDislike(post))
                                }.takeIf { item.post.original.actionPermissions.canDislike && isHomeInstance },
                                onReply =
                                { post: PostModel ->
                                    mainRouter.openComposer(
                                        inReplyTo = post,
                                        inReplyToUser = post.creator,
                                    )
                                }.takeIf { item.post.original.actionPermissions.canReply && isHomeInstance },
                                onPollVote =
                                uiState.currentUserId?.let {
                                    { post, choices ->
                                        model.reduce(
                                            ExploreMvi.Intent.SubmitPollVote(
                                                post = post,
                                                choices = choices,
                                            ),
                                        )
                                    }
                                },
                                onShowOriginal = {
                                    model.reduce(
                                        ExploreMvi.Intent.ToggleTranslation(
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
                                    if (item.post.original.actionPermissions.canShare) {
                                        this += OptionId.Share.toOption()
                                        this += OptionId.CopyUrl.toOption()
                                    }
                                    if (item.post.original.actionPermissions.canEdit && isHomeInstance) {
                                        this += OptionId.Edit.toOption()
                                    }
                                    if (item.post.original.actionPermissions.canDelete && isHomeInstance) {
                                        this += OptionId.Delete.toOption()
                                    }
                                    if (item.post.original.actionPermissions.canTogglePin && isHomeInstance) {
                                        if (post.pinned) {
                                            this += OptionId.Unpin.toOption()
                                        } else {
                                            this += OptionId.Pin.toOption()
                                        }
                                    }
                                    if (item.post.original.actionPermissions.canMute && isHomeInstance) {
                                        this += OptionId.Mute.toOption()
                                    }
                                    if (item.post.original.actionPermissions.canBlock && isHomeInstance) {
                                        this += OptionId.Block.toOption()
                                    }
                                    if (item.post.original.actionPermissions.canReport && isHomeInstance) {
                                        this += OptionId.ReportUser.toOption()
                                        this += OptionId.ReportPost.toOption()
                                    }
                                    if (item.post.original.actionPermissions.canQuote) {
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
                                            item.post.original.also { toEdit ->
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
                                                ExploreMvi.Intent.TogglePin(item.post),
                                            )

                                        OptionId.ReportUser ->
                                            item.post.original.creator?.also { toReport ->
                                                mainRouter.openCreateReport(user = toReport)
                                            }

                                        OptionId.ReportPost ->
                                            item.post.original.also { toReport ->
                                                toReport.creator?.also { userToReport ->
                                                    mainRouter.openCreateReport(
                                                        user = userToReport,
                                                        post = toReport,
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
                                                ExploreMvi.Intent.CopyToClipboard(
                                                    item.post.original,
                                                ),
                                            )

                                        OptionId.Translate ->
                                            model.reduce(
                                                ExploreMvi.Intent.ToggleTranslation(
                                                    item.post.original,
                                                ),
                                            )

                                        OptionId.AddShortcut ->
                                            model.reduce(
                                                ExploreMvi.Intent.AddInstanceShortcut(
                                                    item.post.nodeName,
                                                ),
                                            )

                                        OptionId.OpenInBrowser ->
                                            model.reduce(
                                                ExploreMvi.Intent.OpenInBrowser(item.post),
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
                                onOpen = { tag ->
                                    mainRouter.openHashtag(tag = tag, otherInstance = uiState.otherInstance)
                                },
                            )
                            Spacer(modifier = Modifier.height(Spacing.interItem))
                        }

                        is ExploreItemModel.Link -> {
                            LinkItem(
                                link = item.link,
                                autoloadImages = uiState.autoloadImages,
                                onOpen = { url ->
                                    uriHandler.openExternally(url)
                                },
                            )
                            Spacer(modifier = Modifier.height(Spacing.interItem))
                        }

                        is ExploreItemModel.User -> {
                            UserItem(
                                user = item.user.let { u ->
                                    u.copy(relationshipStatus = u.relationshipStatus.takeIf { isHomeInstance })
                                },
                                autoloadImages = uiState.autoloadImages,
                                onClick = {
                                    mainRouter.openUserDetail(user = item.user, otherInstance = uiState.otherInstance)
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
                                            model.reduce(ExploreMvi.Intent.Follow(item.user.id))
                                        }

                                        RelationshipStatusNextAction.Unfollow -> {
                                            model.reduce(ExploreMvi.Intent.Unfollow(item.user.id))
                                        }
                                    }
                                }.takeIf { isHomeInstance },
                            )
                            Spacer(modifier = Modifier.height(Spacing.interItem))
                        }
                    }

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    val isNearTheEnd = idx.isNearTheEnd(uiState.items)
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(ExploreMvi.Intent.LoadNextPage)
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
                    model.reduce(ExploreMvi.Intent.Unfollow(userId))
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
                    model.reduce(ExploreMvi.Intent.Unfollow(userId))
                }
            },
        )
    }

    if (confirmDeletePostId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val postId = confirmDeletePostId
                if (confirm && postId != null) {
                    model.reduce(ExploreMvi.Intent.Delete(postId))
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
                                ExploreMvi.Intent.MuteUser(
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
                        ExploreMvi.Intent.BlockUser(
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
                    model.reduce(ExploreMvi.Intent.ToggleReblog(e))
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

    if (selectForeignInstanceOpened) {
        ChangeInstanceDialog(
            nodeName = uiState.selectForeignInstanceName,
            validationInProgress = uiState.selectForeignInstanceValidationInProgress,
            validationError = uiState.selectForeignInstanceNameError,
            exclusions = uiState.currentNode?.let { listOf(it) }.orEmpty(),
            onClose = {
                selectForeignInstanceOpened = false
            },
            onNodeChange = { value ->
                model.reduce(ExploreMvi.Intent.SetSelectForeignInstanceName(value))
            },
            onSubmit = {
                model.reduce(ExploreMvi.Intent.SubmitSelectForeignInstanceName)
            },
        )
    }
}

private sealed interface CustomOptions : OptionId.Custom {
    data object SelectForeignInstance : CustomOptions
    data object BackToHomeInstance : CustomOptions
}
