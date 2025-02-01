package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.kodein.rememberScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.EntryDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldEntry
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di.EntryDetailMviModelParams
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

class EntryDetailScreen(
    private val id: String,
    private val swipeNavigationEnabled: Boolean,
) : Screen {
    override val key: ScreenKey
        get() = super.key + id

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model: EntryDetailMviModel =
            rememberScreenModel(
                arg =
                    EntryDetailMviModelParams(
                        id = id,
                        swipeNavigationEnabled = swipeNavigationEnabled,
                    ),
            )
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val detailOpener = remember { getDetailOpener() }
        val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
        val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val shareHelper = remember { getShareHelper() }
        val actionRepository = remember { getEntryActionRepository() }
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }
        var confirmMuteEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var confirmBlockEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var confirmReblogEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var pollErrorDialogOpened by remember { mutableStateOf(false) }
        var seeDetailsEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var lazyListState = rememberLazyListState()

        fun goBackToTop() {
            runCatching {
                scope.launch {
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
                        is EntryDetailMviModel.Effect.ScrollToItem ->
                            runCatching { lazyListState.scrollToItem(event.index) }

                        EntryDetailMviModel.Effect.PollVoteFailure -> pollErrorDialogOpened = true
                        is EntryDetailMviModel.Effect.TriggerCopy -> {
                            clipboardManager.setText(AnnotatedString(event.text))
                            snackbarHostState.showSnackbar(copyToClipboardSuccess)
                        }
                    }
                }.launchIn(this)
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text =
                                buildString {
                                    append(LocalStrings.current.postTitle)
                                    uiState.mainEntry?.creator?.username?.also { username ->
                                        append(" ")
                                        append(LocalStrings.current.postBy)
                                        append(" ")
                                        append(username)
                                    }
                                },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        if (navigationCoordinator.canPop.value) {
                            IconButton(
                                onClick = {
                                    navigationCoordinator.pop()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = LocalStrings.current.actionGoBack,
                                )
                            }
                        }
                    },
                )
            },
            floatingActionButton = {
                if (uiState.currentUserId != null) {
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
                                val entry = uiState.mainEntry
                                if (entry != null) {
                                    detailOpener.openComposer(
                                        inReplyTo = entry,
                                        inReplyToUser = entry.creator,
                                    )
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Reply,
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
            val pageCount = uiState.entries.size
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
                        model.reduce(EntryDetailMviModel.Intent.ChangeNavigationIndex(it))
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
                val currentEntryList = uiState.entries[index]
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
                        model.reduce(EntryDetailMviModel.Intent.Refresh)
                    },
                ) {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        itemsIndexed(
                            items = currentEntryList,
                            key = { _, e -> "entry-detail-${e.safeKey}" },
                        ) { idx, entry ->
                            val isMainEntry = entry.id == uiState.mainEntry?.id
                            TimelineItem(
                                modifier =
                                    Modifier.then(
                                        if (isMainEntry && uiState.layout == TimelineLayout.Card) {
                                            // since the main entry is forced to "full", recreates card appearance
                                            Modifier
                                                .padding(horizontal = Spacing.xs)
                                                .shadow(
                                                    elevation = 5.dp,
                                                    shape = RoundedCornerShape(CornerSize.l),
                                                ).background(
                                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                                                    shape = RoundedCornerShape(CornerSize.l),
                                                ).padding(
                                                    vertical = Spacing.s,
                                                    horizontal = Spacing.xxxs,
                                                )
                                        } else {
                                            Modifier
                                        },
                                    ),
                                entry = entry,
                                extendedSocialInfoEnabled = isMainEntry,
                                layout =
                                    if (isMainEntry) {
                                        TimelineLayout.Full
                                    } else {
                                        uiState.layout
                                    },
                                blurNsfw = uiState.blurNsfw,
                                autoloadImages = uiState.autoloadImages,
                                onOpenUrl = { url, allowOpenInternal ->
                                    if (allowOpenInternal) {
                                        uriHandler.openUri(url)
                                    } else {
                                        uriHandler.openExternally(url)
                                    }
                                },
                                onClick = { e ->
                                    if (e.id != uiState.mainEntry?.id) {
                                        detailOpener.openEntryDetail(
                                            entry = entry,
                                            replaceTop = true,
                                        )
                                    }
                                },
                                onOpenUser = {
                                    detailOpener.openUserDetail(it)
                                },
                                onOpenImage = { urls, imageIdx, videoIndices ->
                                    detailOpener.openImageDetail(
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
                                                    EntryDetailMviModel.Intent.ToggleReblog(e),
                                                )
                                        }
                                    }.takeIf { actionRepository.canReblog(entry.original) },
                                onBookmark =
                                    { e: TimelineEntryModel ->
                                        model.reduce(EntryDetailMviModel.Intent.ToggleBookmark(e))
                                    }.takeIf { actionRepository.canBookmark(entry.original) },
                                onFavorite =
                                    { e: TimelineEntryModel ->
                                        model.reduce(EntryDetailMviModel.Intent.ToggleFavorite(e))
                                    }.takeIf { actionRepository.canFavorite(entry.original) },
                                onDislike =
                                    { e: TimelineEntryModel ->
                                        model.reduce(EntryDetailMviModel.Intent.ToggleDislike(e))
                                    }.takeIf { actionRepository.canDislike(entry.original) },
                                onOpenUsersFavorite = { e ->
                                    detailOpener.openEntryUsersFavorite(
                                        entryId = e.id,
                                        count = e.favoriteCount,
                                    )
                                },
                                onOpenUsersReblog = { e ->
                                    detailOpener.openEntryUsersReblog(
                                        entryId = e.id,
                                        count = e.reblogCount,
                                    )
                                },
                                onReply =
                                    { e: TimelineEntryModel ->
                                        detailOpener.openComposer(
                                            inReplyTo = e,
                                            inReplyToUser = e.creator,
                                        )
                                    }.takeIf { actionRepository.canReply(entry.original) },
                                onPollVote =
                                    uiState.currentUserId?.let {
                                        { e, choices ->
                                            model.reduce(
                                                EntryDetailMviModel.Intent.SubmitPollVote(
                                                    entry = e,
                                                    choices = choices,
                                                ),
                                            )
                                        }
                                    },
                                onShowOriginal = {
                                    model.reduce(
                                        EntryDetailMviModel.Intent.ToggleTranslation(entry.original),
                                    )
                                },
                                options =
                                    buildList {
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
                                        if (currentLang.isNotEmpty() && entry.lang != currentLang && !entry.isShowingTranslation) {
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

                                        OptionId.Edit -> {
                                            entry.original.also { entryToEdit ->
                                                detailOpener.openComposer(
                                                    inReplyTo = entryToEdit.inReplyTo,
                                                    inReplyToUser = entryToEdit.inReplyTo?.creator,
                                                    editedPostId = entryToEdit.id,
                                                )
                                            }
                                        }

                                        OptionId.Delete -> confirmDeleteEntryId = entry.id
                                        OptionId.Mute -> confirmMuteEntry = entry
                                        OptionId.Block -> confirmBlockEntry = entry
                                        OptionId.Pin, OptionId.Unpin ->
                                            model.reduce(EntryDetailMviModel.Intent.TogglePin(entry))

                                        OptionId.ReportUser ->
                                            entry.original.creator?.also { userToReport ->
                                                detailOpener.openCreateReport(user = userToReport)
                                            }

                                        OptionId.ReportEntry ->
                                            entry.original.also { entryToReport ->
                                                entryToReport.creator?.also { userToReport ->
                                                    detailOpener.openCreateReport(
                                                        user = userToReport,
                                                        entry = entryToReport,
                                                    )
                                                }
                                            }

                                        OptionId.ViewDetails -> seeDetailsEntry = entry.original
                                        OptionId.Quote -> {
                                            entry.original.also { entryToShare ->
                                                detailOpener.openComposer(
                                                    urlToShare = entryToShare.url,
                                                )
                                            }
                                        }

                                        OptionId.CopyToClipboard ->
                                            model.reduce(
                                                EntryDetailMviModel.Intent.CopyToClipboard(
                                                    entry.original,
                                                ),
                                            )

                                        OptionId.Translate ->
                                            model.reduce(
                                                EntryDetailMviModel.Intent.ToggleTranslation(
                                                    entry.original,
                                                ),
                                            )

                                        OptionId.AddShortcut ->
                                            model.reduce(
                                                EntryDetailMviModel.Intent.AddInstanceShortcut(
                                                    entry.nodeName,
                                                ),
                                            )
                                        else -> Unit
                                    }
                                },
                            )
                            if (idx < currentEntryList.lastIndex) {
                                TimelineDivider(layout = uiState.layout)
                            }
                        }

                        // here to show the initial entry from cache at startup
                        if (uiState.initial) {
                            val placeholderCount = 5
                            items(placeholderCount) { idx ->
                                TimelineItemPlaceholder(
                                    modifier =
                                        Modifier.fillMaxWidth().then(
                                            if (idx == 0) {
                                                Modifier.padding(top = Spacing.s)
                                            } else {
                                                Modifier
                                            },
                                        ),
                                )
                                if (idx < placeholderCount - 1) {
                                    TimelineDivider(layout = uiState.layout)
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(Spacing.xxxl))
                        }
                    }
                }
            }
        }

        if (confirmDeleteEntryId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val entryId = confirmDeleteEntryId ?: ""
                    confirmDeleteEntryId = null
                    if (confirm && entryId.isNotEmpty()) {
                        model.reduce(EntryDetailMviModel.Intent.DeleteEntry(entryId))
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
                                    EntryDetailMviModel.Intent.MuteUser(
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
                            EntryDetailMviModel.Intent.BlockUser(
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
                        model.reduce(EntryDetailMviModel.Intent.ToggleReblog(e))
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
