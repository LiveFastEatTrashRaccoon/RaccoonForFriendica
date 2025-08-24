package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
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
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldEntry
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.di.ShortcutTimelineViewModelArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutTimelineScreen(node: String, modifier: Modifier = Modifier) {
    val model: ShortcutTimelineMviModel =
        getViewModel<ShortcutTimelineViewModel>(arg = ShortcutTimelineViewModelArgs(node))
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val connection = navigationCoordinator.getBottomBarScrollConnection()
    val uriHandler = LocalUriHandler.current
    val mainRouter = remember { getMainRouter() }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val shareHelper = remember { getShareHelper() }
    val actionRepository = remember { getEntryActionRepository() }
    val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
    val clipboard = LocalClipboard.current
    val clipboardHelper = remember { getClipboardHelper(clipboard) }
    var confirmReblogEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
    var pollErrorDialogOpened by remember { mutableStateOf(false) }
    var seeDetailsEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
    val genericError = LocalStrings.current.messageGenericError

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
                    ShortcutTimelineMviModel.Effect.BackToTop -> goBackToTop()
                    ShortcutTimelineMviModel.Effect.PollVoteFailure ->
                        pollErrorDialogOpened = true

                    ShortcutTimelineMviModel.Effect.Failure ->
                        snackbarHostState.showSnackbar(message = genericError)

                    is ShortcutTimelineMviModel.Effect.TriggerCopy -> {
                        clipboardHelper.setText(event.text)
                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                    }

                    is ShortcutTimelineMviModel.Effect.OpenDetail -> {
                        mainRouter.openEntryDetail(
                            entry = event.entry,
                            swipeNavigationEnabled = true,
                        )
                    }

                    is ShortcutTimelineMviModel.Effect.OpenUrl ->
                        uriHandler.openExternally(event.url)
                }
            }.launchIn(this)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets(),
                modifier =
                Modifier.clickable {
                    scope.launch {
                        goBackToTop()
                    }
                },
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = uiState.nodeName.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                model.reduce(ShortcutTimelineMviModel.Intent.Refresh)
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

                if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.entries.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                            text =
                            buildString {
                                append(LocalStrings.current.messageEmptyList)
                                append("\n")
                                append(LocalStrings.current.messageNoPublicTimeline)
                            },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                itemsIndexed(
                    items = uiState.entries,
                    key = { _, e -> "timeline-${e.safeKey}" },
                ) { idx, entry ->
                    TimelineItem(
                        entry = entry,
                        layout = uiState.layout,
                        blurNsfw = uiState.blurNsfw,
                        autoloadImages = uiState.autoloadImages,
                        maxBodyLines = uiState.maxBodyLines,
                        onClick = { e ->
                            model.reduce(ShortcutTimelineMviModel.Intent.WillOpenDetail(e))
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
                                        ShortcutTimelineMviModel.Intent.ToggleReblog(e),
                                    )
                            }
                        }.takeIf { actionRepository.canReblog(entry.original) },
                        onBookmark =
                        { e: TimelineEntryModel ->
                            model.reduce(ShortcutTimelineMviModel.Intent.ToggleBookmark(e))
                        }.takeIf { actionRepository.canBookmark(entry.original) },
                        onFavorite =
                        { e: TimelineEntryModel ->
                            model.reduce(ShortcutTimelineMviModel.Intent.ToggleFavorite(e))
                        }.takeIf { actionRepository.canFavorite(entry.original) },
                        onDislike =
                        { e: TimelineEntryModel ->
                            model.reduce(ShortcutTimelineMviModel.Intent.ToggleDislike(e))
                        }.takeIf { actionRepository.canDislike(entry.original) },
                        onReply =
                        { e: TimelineEntryModel ->
                            mainRouter.openComposer(
                                inReplyTo = e,
                                inReplyToUser = e.creator,
                            )
                        }.takeIf { actionRepository.canReply(entry.original) },
                        onPollVote =
                        uiState.currentUserId?.let {
                            { e, choices ->
                                model.reduce(
                                    ShortcutTimelineMviModel.Intent.SubmitPollVote(
                                        entry = e,
                                        choices = choices,
                                    ),
                                )
                            }
                        },
                        onShowOriginal = {
                            model.reduce(
                                ShortcutTimelineMviModel.Intent.ToggleTranslation(entry.original),
                            )
                        },
                        options =
                        buildList {
                            if (actionRepository.canShare(entry.original)) {
                                this += OptionId.Share.toOption()
                                this += OptionId.CopyUrl.toOption()
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
                            this += OptionId.OpenInBrowser.toOption()
                        },
                        onSelectOption = { optionId ->
                            when (optionId) {
                                OptionId.Share -> {
                                    val urlString = entry.url.orEmpty()
                                    shareHelper.share(urlString)
                                }

                                OptionId.CopyUrl -> {
                                    val urlString = entry.url.orEmpty()
                                    scope.launch {
                                        clipboardHelper.setText(urlString)
                                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                    }
                                }

                                OptionId.Quote -> {
                                    entry.original.also { entryToShare ->
                                        mainRouter.openComposer(
                                            urlToShare = entryToShare.url,
                                        )
                                    }
                                }

                                OptionId.ViewDetails -> seeDetailsEntry = entry.original
                                OptionId.CopyToClipboard ->
                                    model.reduce(
                                        ShortcutTimelineMviModel.Intent.CopyToClipboard(
                                            entry.original,
                                        ),
                                    )

                                OptionId.Translate ->
                                    model.reduce(
                                        ShortcutTimelineMviModel.Intent.ToggleTranslation(
                                            entry.original,
                                        ),
                                    )

                                OptionId.OpenInBrowser ->
                                    model.reduce(
                                        ShortcutTimelineMviModel.Intent.OpenInBrowser(entry),
                                    )

                                else -> Unit
                            }
                        },
                    )
                    if (idx < uiState.entries.lastIndex) {
                        TimelineDivider(layout = uiState.layout)
                    }

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    val isNearTheEnd = idx.isNearTheEnd(uiState.entries)
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(ShortcutTimelineMviModel.Intent.LoadNextPage)
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
                    model.reduce(ShortcutTimelineMviModel.Intent.ToggleReblog(e))
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
