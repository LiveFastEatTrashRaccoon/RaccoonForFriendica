package com.livefast.eattrash.raccoonforfriendica.feature.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.EntryDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.HashtagItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.LinkItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldEntry
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.toReadableName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    model: ExploreMviModel,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val connection = navigationCoordinator.getBottomBarScrollConnection()
    val uriHandler = LocalUriHandler.current
    val mainRouter = remember { getMainRouter() }
    val scope = rememberCoroutineScope()
    val drawerCoordinator = remember { getDrawerCoordinator() }
    val snackbarHostState = remember { SnackbarHostState() }
    val shareHelper = remember { getShareHelper() }
    val actionRepository = remember { getEntryActionRepository() }
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboard = LocalClipboard.current
    val clipboardHelper = remember { getClipboardHelper(clipboard) }
    var confirmUnfollowDialogUserId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteFollowRequestDialogUserId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }
    var confirmMuteEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
    var confirmBlockEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
    var confirmReblogEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
    var pollErrorDialogOpened by remember { mutableStateOf(false) }
    var seeDetailsEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }

    suspend fun goBackToTop() {
        runCatching {
            lazyListState.scrollToItem(0)
            topAppBarState.heightOffset = 0f
            topAppBarState.contentOffset = 0f
        }
    }

    LaunchedEffect(model) {
        model.effects
            .onEach { event ->
                when (event) {
                    ExploreMviModel.Effect.BackToTop -> goBackToTop()
                    ExploreMviModel.Effect.PollVoteFailure -> pollErrorDialogOpened = true
                    is ExploreMviModel.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is ExploreMviModel.Effect.OpenUrl -> uriHandler.openExternally(event.url)
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = LocalStrings.current.sectionTitleExplore,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerCoordinator.toggleDrawer()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = LocalStrings.current.actionOpenSideMenu,
                        )
                    }
                },
                actions = {
                    if (uiState.currentUserId != null) {
                        // only logged users can call the v2/search API
                        IconButton(
                            onClick = {
                                mainRouter.openSearch()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = LocalStrings.current.actionSearch,
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
                model.reduce(ExploreMviModel.Intent.Refresh)
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
                                ExploreMviModel.Intent.ChangeSection(uiState.availableSections[it]),
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
                    key = { _, e ->
                        when (e) {
                            is ExploreItemModel.Entry -> "explore-${e.entry.safeKey}"
                            else -> "explore-${e.id}"
                        }
                    },
                ) { idx, item ->
                    when (item) {
                        is ExploreItemModel.Entry -> {
                            TimelineItem(
                                entry = item.entry,
                                layout = uiState.layout,
                                blurNsfw = uiState.blurNsfw,
                                autoloadImages = uiState.autoloadImages,
                                maxBodyLines = uiState.maxBodyLines,
                                onClick = { e ->
                                    mainRouter.openEntryDetail(e)
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
                                                ExploreMviModel.Intent.ToggleReblog(e),
                                            )
                                    }
                                }.takeIf { actionRepository.canReblog(item.entry.original) },
                                onBookmark =
                                { e: TimelineEntryModel ->
                                    model.reduce(ExploreMviModel.Intent.ToggleBookmark(e))
                                }.takeIf { actionRepository.canBookmark(item.entry.original) },
                                onFavorite =
                                { e: TimelineEntryModel ->
                                    model.reduce(ExploreMviModel.Intent.ToggleFavorite(e))
                                }.takeIf { actionRepository.canFavorite(item.entry.original) },
                                onDislike =
                                { e: TimelineEntryModel ->
                                    model.reduce(ExploreMviModel.Intent.ToggleDislike(e))
                                }.takeIf { actionRepository.canDislike(item.entry.original) },
                                onReply =
                                { e: TimelineEntryModel ->
                                    mainRouter.openComposer(
                                        inReplyTo = e,
                                        inReplyToUser = e.creator,
                                    )
                                }.takeIf { actionRepository.canReply(item.entry.original) },
                                onPollVote =
                                uiState.currentUserId?.let {
                                    { e, choices ->
                                        model.reduce(
                                            ExploreMviModel.Intent.SubmitPollVote(
                                                entry = e,
                                                choices = choices,
                                            ),
                                        )
                                    }
                                },
                                onShowOriginal = {
                                    model.reduce(
                                        ExploreMviModel.Intent.ToggleTranslation(
                                            item.entry.original,
                                        ),
                                    )
                                },
                                options =
                                buildList {
                                    val entry = item.entry
                                    if (actionRepository.canShare(entry.original)) {
                                        this += OptionId.Share.toOption()
                                        this += OptionId.CopyUrl.toOption()
                                    }
                                    if (actionRepository.canEdit(entry.original)) {
                                        this += OptionId.Edit.toOption()
                                    }
                                    if (actionRepository.canDelete(entry.original)) {
                                        this += OptionId.Delete.toOption()
                                    }
                                    if (actionRepository.canTogglePin(entry)) {
                                        if (entry.pinned) {
                                            this += OptionId.Unpin.toOption()
                                        } else {
                                            this += OptionId.Pin.toOption()
                                        }
                                    }
                                    if (actionRepository.canMute(entry)) {
                                        this += OptionId.Mute.toOption()
                                    }
                                    if (actionRepository.canBlock(entry)) {
                                        this += OptionId.Block.toOption()
                                    }
                                    if (actionRepository.canReport(entry.original)) {
                                        this += OptionId.ReportUser.toOption()
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
                                            val urlString = item.entry.url.orEmpty()
                                            shareHelper.share(urlString)
                                        }

                                        OptionId.CopyUrl -> {
                                            val urlString = item.entry.url.orEmpty()
                                            scope.launch {
                                                clipboardHelper.setText(urlString)
                                                snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                            }
                                        }

                                        OptionId.Edit -> {
                                            item.entry.original.also { entryToEdit ->
                                                mainRouter.openComposer(
                                                    inReplyTo = entryToEdit.inReplyTo,
                                                    inReplyToUser = entryToEdit.inReplyTo?.creator,
                                                    editedPostId = entryToEdit.id,
                                                )
                                            }
                                        }

                                        OptionId.Delete -> confirmDeleteEntryId = item.entry.id
                                        OptionId.Mute -> confirmMuteEntry = item.entry
                                        OptionId.Block -> confirmBlockEntry = item.entry
                                        OptionId.Pin, OptionId.Unpin ->
                                            model.reduce(
                                                ExploreMviModel.Intent.TogglePin(item.entry),
                                            )

                                        OptionId.ReportUser ->
                                            item.entry.original.creator?.also { userToReport ->
                                                mainRouter.openCreateReport(user = userToReport)
                                            }

                                        OptionId.ReportEntry ->
                                            item.entry.original.also { entryToReport ->
                                                entryToReport.creator?.also { userToReport ->
                                                    mainRouter.openCreateReport(
                                                        user = userToReport,
                                                        entry = entryToReport,
                                                    )
                                                }
                                            }

                                        OptionId.ViewDetails -> seeDetailsEntry = item.entry.original
                                        OptionId.Quote -> {
                                            item.entry.original.also { entryToShare ->
                                                mainRouter.openComposer(
                                                    urlToShare = entryToShare.url,
                                                )
                                            }
                                        }

                                        OptionId.CopyToClipboard ->
                                            model.reduce(
                                                ExploreMviModel.Intent.CopyToClipboard(
                                                    item.entry.original,
                                                ),
                                            )

                                        OptionId.Translate ->
                                            model.reduce(
                                                ExploreMviModel.Intent.ToggleTranslation(
                                                    item.entry.original,
                                                ),
                                            )

                                        OptionId.AddShortcut ->
                                            model.reduce(
                                                ExploreMviModel.Intent.AddInstanceShortcut(
                                                    item.entry.nodeName,
                                                ),
                                            )

                                        OptionId.OpenInBrowser ->
                                            model.reduce(
                                                ExploreMviModel.Intent.OpenInBrowser(item.entry),
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
                                user = item.user,
                                autoloadImages = uiState.autoloadImages,
                                onClick = {
                                    mainRouter.openUserDetail(item.user)
                                },
                                onRelationshipClick = { nextAction ->
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
                                            model.reduce(ExploreMviModel.Intent.Follow(item.user.id))
                                        }

                                        RelationshipStatusNextAction.Unfollow -> {
                                            model.reduce(ExploreMviModel.Intent.Unfollow(item.user.id))
                                        }
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.height(Spacing.interItem))
                        }
                    }

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    val isNearTheEnd = idx.isNearTheEnd(uiState.items)
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(ExploreMviModel.Intent.LoadNextPage)
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
                    model.reduce(ExploreMviModel.Intent.Unfollow(userId))
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
                    model.reduce(ExploreMviModel.Intent.Unfollow(userId))
                }
            },
        )
    }

    if (confirmDeleteEntryId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val entryId = confirmDeleteEntryId
                if (confirm && entryId != null) {
                    model.reduce(ExploreMviModel.Intent.DeleteEntry(entryId))
                }
            },
        )
    }

    if (confirmMuteEntry != null) {
        (confirmMuteEntry?.reblog?.creator ?: confirmMuteEntry?.creator)?.also { user ->
            ConfirmMuteUserBottomSheet(
                userHandle = user.handle.orEmpty(),
                onClose = { pair ->
                    val entryId = confirmMuteEntry?.id
                    confirmMuteEntry = null
                    if (pair != null) {
                        val (duration, disableNotifications) = pair
                        if (entryId != null) {
                            model.reduce(
                                ExploreMviModel.Intent.MuteUser(
                                    userId = user.id,
                                    entryId = entryId,
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

    if (confirmBlockEntry != null) {
        val creator = confirmBlockEntry?.reblog?.creator ?: confirmBlockEntry?.creator
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
                val entryId = confirmBlockEntry?.id
                val creatorId = creator?.id
                confirmBlockEntry = null
                if (confirm && entryId != null && creatorId != null) {
                    model.reduce(
                        ExploreMviModel.Intent.BlockUser(
                            userId = creatorId,
                            entryId = entryId,
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

    if (confirmReblogEntry != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.buttonConfirm,
            body = LocalStrings.current.messageAreYouSureReblog,
            onClose = { confirm ->
                val e = confirmReblogEntry
                confirmReblogEntry = null
                if (confirm && e != null) {
                    model.reduce(ExploreMviModel.Intent.ToggleReblog(e))
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
