package com.livefast.eattrash.raccoonforfriendica.feature.thread

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.Dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.EntryDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldEntry
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.feature.thread.composable.TimelineReplyItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.parameter.parameterArrayOf
import kotlin.time.Duration

class ThreadScreen(
    private val entryId: String,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<ThreadMviModel>(parameters = { parameterArrayOf(entryId) })
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
        val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
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
                        ThreadMviModel.Effect.PollVoteFailure -> pollErrorDialogOpened = true
                    }
                }.launchIn(this)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.threadTitle,
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
                                    contentDescription = null,
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
                                val entry = uiState.entry
                                if (entry != null) {
                                    detailOpener.openComposer(
                                        inReplyToId = entry.id,
                                        inReplyToUser = entry.creator,
                                    )
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Reply,
                                contentDescription = null,
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
            val pullRefreshState =
                rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = {
                        model.reduce(ThreadMviModel.Intent.Refresh)
                    },
                )
            Box(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .nestedScroll(fabNestedScrollConnection)
                        .pullRefresh(pullRefreshState),
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    // original entry
                    uiState.entry?.also { original ->
                        item {
                            TimelineItem(
                                modifier =
                                    Modifier
                                        .border(
                                            width = Dp.Hairline,
                                            color =
                                                MaterialTheme.colorScheme.onBackground.copy(
                                                    ancillaryTextAlpha,
                                                ),
                                            shape = RoundedCornerShape(CornerSize.l),
                                        ).padding(
                                            vertical = Spacing.s,
                                            horizontal = Spacing.xxxs,
                                        ),
                                entry = original,
                                reshareAndReplyVisible = false,
                                extendedSocialInfoEnabled = true,
                                blurNsfw = uiState.blurNsfw,
                                onOpenUrl = { url ->
                                    uriHandler.openUri(url)
                                },
                                onOpenUser = {
                                    detailOpener.openUserDetail(it)
                                },
                                onOpenImage = { urls, idx ->
                                    detailOpener.openImageDetail(urls = urls, initialIndex = idx)
                                },
                                onReblog =
                                    uiState.currentUserId?.let {
                                        { e ->
                                            val timeSinceCreation =
                                                e.created?.run {
                                                    getDurationFromDateToNow(this)
                                                } ?: Duration.ZERO
                                            when {
                                                !e.reblogged && timeSinceCreation.isOldEntry ->
                                                    confirmReblogEntry = e

                                                else ->
                                                    model.reduce(
                                                        ThreadMviModel.Intent.ToggleReblog(e),
                                                    )
                                            }
                                        }
                                    },
                                onBookmark =
                                    uiState.currentUserId?.let {
                                        { e -> model.reduce(ThreadMviModel.Intent.ToggleBookmark(e)) }
                                    },
                                onFavorite =
                                    uiState.currentUserId?.let {
                                        { e -> model.reduce(ThreadMviModel.Intent.ToggleFavorite(e)) }
                                    },
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
                                    uiState.currentUserId?.let {
                                        { e ->
                                            detailOpener.openComposer(
                                                inReplyToId = e.id,
                                                inReplyToUser = e.creator,
                                            )
                                        }
                                    },
                                onPollVote =
                                    uiState.currentUserId?.let {
                                        { e, choices ->
                                            model.reduce(
                                                ThreadMviModel.Intent.SubmitPollVote(
                                                    entry = e,
                                                    choices = choices,
                                                ),
                                            )
                                        }
                                    },
                                onToggleSpoilerActive = { e ->
                                    model.reduce(ThreadMviModel.Intent.ToggleSpoilerActive(e))
                                },
                                options =
                                    buildList {
                                        val entry = uiState.entry ?: return@buildList
                                        if (actionRepository.canShare(entry.original)) {
                                            this += OptionId.Share.toOption()
                                            this += OptionId.CopyUrl.toOption()
                                        }
                                        this += OptionId.ViewDetails.toOption()
                                    },
                                onOptionSelected = { optionId ->
                                    when (optionId) {
                                        OptionId.Share -> {
                                            val urlString = uiState.entry?.url.orEmpty()
                                            shareHelper.share(urlString)
                                        }

                                        OptionId.CopyUrl -> {
                                            val urlString = uiState.entry?.url.orEmpty()
                                            clipboardManager.setText(AnnotatedString(urlString))
                                            scope.launch {
                                                snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                            }
                                        }
                                        OptionId.ViewDetails -> seeDetailsEntry = uiState.entry?.original
                                        else -> Unit
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.height(Spacing.s))
                        }
                    }

                    // here to show the initial entry from cache at startup
                    if (uiState.initial) {
                        val placeholderCount = 5
                        items(placeholderCount) { idx ->
                            TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                            if (idx < placeholderCount - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.s),
                                )
                            }
                        }
                    }

                    // replies
                    itemsIndexed(
                        items = uiState.replies,
                        key = { _, e -> e.safeKey },
                    ) { idx, entry ->
                        TimelineReplyItem(
                            entry = entry,
                            onOpenUrl = { url ->
                                uriHandler.openUri(url)
                            },
                            onOpenUser = {
                                detailOpener.openUserDetail(it)
                            },
                            onOpenImage = { urls, index ->
                                detailOpener.openImageDetail(urls = urls, initialIndex = index)
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
                                                ThreadMviModel.Intent.ToggleReblog(e),
                                            )
                                    }
                                }.takeIf { actionRepository.canReblog(entry.original) },
                            onBookmark =
                                { e: TimelineEntryModel ->
                                    model.reduce(ThreadMviModel.Intent.ToggleBookmark(e))
                                }.takeIf { actionRepository.canBookmark(entry.original) },
                            onFavorite =
                                { e: TimelineEntryModel ->
                                    model.reduce(ThreadMviModel.Intent.ToggleFavorite(e))
                                }.takeIf { actionRepository.canReact(entry.original) },
                            onReply =
                                { e: TimelineEntryModel ->
                                    detailOpener.openComposer(
                                        inReplyToId = e.id,
                                        inReplyToUser = e.creator,
                                    )
                                }.takeIf { actionRepository.canReply(entry.original) },
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
                                        val urlString = entry.url.orEmpty()
                                        shareHelper.share(urlString)
                                    }

                                    OptionId.CopyUrl -> {
                                        val urlString = entry.url.orEmpty()
                                        clipboardManager.setText(AnnotatedString(urlString))
                                        scope.launch {
                                            snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                        }
                                    }

                                    OptionId.Edit -> {
                                        detailOpener.openComposer(
                                            inReplyToUser = entry.creator,
                                            editedPostId = entry.reblog?.id,
                                            inGroup = true,
                                        )
                                    }

                                    OptionId.Delete -> confirmDeleteEntryId = entry.id
                                    OptionId.Mute -> confirmMuteEntry = entry
                                    OptionId.Block -> confirmBlockEntry = entry
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
                                    else -> Unit
                                }
                            },
                        )

                        // load more button
                        if (entry.loadMoreButtonVisible) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Button(
                                    onClick = {
                                        model.reduce(
                                            ThreadMviModel.Intent.LoadMoreReplies(entry),
                                        )
                                    },
                                ) {
                                    Text(
                                        text =
                                            buildString {
                                                append(LocalStrings.current.buttonLoadMoreReplies)
                                                entry.replyCount
                                                    .takeIf { it > 0 }
                                                    ?.also { count ->
                                                        append(" (")
                                                        append(count)
                                                        append(")")
                                                    }
                                            },
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                        }
                        if (idx < uiState.replies.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.s),
                            )
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

        if (confirmDeleteEntryId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val entryId = confirmDeleteEntryId
                    confirmDeleteEntryId = null
                    if (confirm && entryId != null) {
                        model.reduce(ThreadMviModel.Intent.DeleteEntry(entryId))
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
                                    ThreadMviModel.Intent.MuteUser(
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
                            ThreadMviModel.Intent.BlockUser(
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
                        model.reduce(ThreadMviModel.Intent.ToggleReblog(e))
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
