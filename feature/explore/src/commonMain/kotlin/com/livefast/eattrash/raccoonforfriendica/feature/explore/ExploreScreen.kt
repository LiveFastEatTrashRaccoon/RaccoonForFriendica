package com.livefast.eattrash.raccoonforfriendica.feature.explore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
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
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldEntry
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.toExploreSection
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.toInt
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.toReadableName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

class ExploreScreen : Screen {
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterialApi::class,
        ExperimentalFoundationApi::class,
    )
    @Composable
    override fun Content() {
        val model = getScreenModel<ExploreMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val connection = navigationCoordinator.getBottomBarScrollConnection()
        val uriHandler = LocalUriHandler.current
        val detailOpener = remember { getDetailOpener() }
        val scope = rememberCoroutineScope()
        val drawerCoordinator = remember { getDrawerCoordinator() }
        val lazyListState = rememberLazyListState()
        val snackbarHostState = remember { SnackbarHostState() }
        val shareHelper = remember { getShareHelper() }
        val actionRepository = remember { getEntryActionRepository() }
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
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
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        if (uiState.currentUserId != null) {
                            // only logged users can call the v2/search API
                            IconButton(
                                onClick = {
                                    detailOpener.openSearch()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
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
            val pullRefreshState =
                rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = {
                        model.reduce(ExploreMviModel.Intent.Refresh)
                    },
                )
            Box(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .then(
                            if (connection != null) {
                                Modifier.nestedScroll(connection)
                            } else {
                                Modifier
                            },
                        ).nestedScroll(scrollBehavior.nestedScrollConnection)
                        .pullRefresh(pullRefreshState),
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    stickyHeader {
                        SectionSelector(
                            Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(
                                    top = Dimensions.maxTopBarInset * topAppBarState.collapsedFraction,
                                    bottom = Spacing.s,
                                ),
                            titles =
                                uiState.availableSections.map {
                                    it.toReadableName()
                                },
                            scrollable = true,
                            currentSection = uiState.section.toInt(),
                            onSectionSelected = {
                                val section = it.toExploreSection()
                                model.reduce(
                                    ExploreMviModel.Intent.ChangeSection(section),
                                )
                            },
                        )
                    }

                    if (uiState.initial) {
                        val placeholderCount = 20
                        items(placeholderCount) { idx ->
                            val modifier = Modifier.fillMaxWidth()
                            when (uiState.section) {
                                ExploreSection.Hashtags -> {
                                    GenericPlaceholder(modifier = modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }

                                ExploreSection.Links -> {
                                    GenericPlaceholder(modifier = modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }

                                ExploreSection.Posts -> {
                                    TimelineItemPlaceholder(modifier = modifier)
                                    if (idx < placeholderCount - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = Spacing.s),
                                        )
                                    }
                                }

                                ExploreSection.Suggestions -> {
                                    UserItemPlaceholder(modifier = modifier)
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
                                is ExploreItemModel.Entry -> e.entry.safeKey
                                else -> e.id
                            }
                        },
                    ) { idx, item ->
                        when (item) {
                            is ExploreItemModel.Entry -> {
                                TimelineItem(
                                    entry = item.entry,
                                    blurNsfw = uiState.blurNsfw,
                                    onClick = { e ->
                                        detailOpener.openEntryDetail(e)
                                    },
                                    onOpenUrl = { url ->
                                        uriHandler.openUri(url)
                                    },
                                    onOpenUser = {
                                        detailOpener.openUserDetail(it)
                                    },
                                    onOpenImage = { urls, imageIdx ->
                                        detailOpener.openImageDetail(
                                            urls = urls,
                                            initialIndex = imageIdx,
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
                                        }.takeIf { actionRepository.canReact(item.entry.original) },
                                    onReply =
                                        { e: TimelineEntryModel ->
                                            detailOpener.openComposer(
                                                inReplyToId = e.id,
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
                                    onToggleSpoilerActive = { e ->
                                        model.reduce(ExploreMviModel.Intent.ToggleSpoilerActive(e))
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
                                            if (actionRepository.canReport(entry)) {
                                                this += OptionId.ReportUser.toOption()
                                                this += OptionId.ReportEntry.toOption()
                                            }
                                            this += OptionId.ViewDetails.toOption()
                                        },
                                    onOptionSelected = { optionId ->
                                        when (optionId) {
                                            OptionId.Share -> {
                                                val urlString = item.entry.url.orEmpty()
                                                shareHelper.share(urlString)
                                            }

                                            OptionId.CopyUrl -> {
                                                val urlString = item.entry.url.orEmpty()
                                                clipboardManager.setText(AnnotatedString(urlString))
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        copyToClipboardSuccess,
                                                    )
                                                }
                                            }

                                            OptionId.Edit -> {
                                                item.entry.original.also { entryToEdit ->
                                                    detailOpener.openComposer(
                                                        inReplyToId = entryToEdit.inReplyTo?.id,
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
                                                    detailOpener.openCreateReport(user = userToReport)
                                                }
                                            OptionId.ReportEntry ->
                                                item.entry.original.also { entryToReport ->
                                                    entryToReport.creator?.also { userToReport ->
                                                        detailOpener.openCreateReport(
                                                            user = userToReport,
                                                            entry = entryToReport,
                                                        )
                                                    }
                                                }
                                            OptionId.ViewDetails -> seeDetailsEntry = item.entry.original
                                            else -> Unit
                                        }
                                    },
                                )
                                if (idx < uiState.items.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = Spacing.s),
                                    )
                                }
                            }

                            is ExploreItemModel.HashTag -> {
                                HashtagItem(
                                    hashtag = item.hashtag,
                                    onOpen = {
                                        detailOpener.openHashtag(it)
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            is ExploreItemModel.Link -> {
                                LinkItem(
                                    link = item.link,
                                    onOpen = { url ->
                                        uriHandler.openUri(url)
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            is ExploreItemModel.User -> {
                                UserItem(
                                    user = item.user,
                                    onClick = {
                                        detailOpener.openUserDetail(item.user)
                                    },
                                    onRelationshipClicked = { nextAction ->
                                        when (nextAction) {
                                            RelationshipStatusNextAction.AcceptRequest -> {
                                                detailOpener.openFollowRequests()
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
                        val isNearTheEnd =
                            idx == uiState.items.lastIndex - 5 || uiState.items.size < 5
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

                PullRefreshIndicator(
                    refreshing = uiState.refreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                )
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
}
