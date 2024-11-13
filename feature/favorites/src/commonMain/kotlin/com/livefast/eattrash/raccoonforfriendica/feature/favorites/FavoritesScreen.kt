package com.livefast.eattrash.raccoonforfriendica.feature.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
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
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldEntry
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toFavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getEntryActionRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration

class FavoritesScreen(
    private val type: Int,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model =
            getScreenModel<FavoritesMviModel>(parameters = { parametersOf(type.toFavoritesType()) })
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val uriHandler = LocalUriHandler.current
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
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
                        FavoritesMviModel.Effect.BackToTop -> goBackToTop()
                        FavoritesMviModel.Effect.PollVoteFailure -> pollErrorDialogOpened = true
                        is FavoritesMviModel.Effect.TriggerCopy -> {
                            clipboardManager.setText(AnnotatedString(event.text))
                            snackbarHostState.showSnackbar(copyToClipboardSuccess)
                        }
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
                            text = type.toFavoritesType().toReadableName(),
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
                    model.reduce(FavoritesMviModel.Intent.Refresh)
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
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.s),
                                )
                            }
                        }
                    }

                    if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.entries.isEmpty()) {
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
                        items = uiState.entries,
                        key = { _, e -> "favorites-${e.safeKey}" },
                    ) { idx, entry ->
                        TimelineItem(
                            entry = entry,
                            blurNsfw = uiState.blurNsfw,
                            autoloadImages = uiState.autoloadImages,
                            maxBodyLines = uiState.maxBodyLines,
                            onClick = { e ->
                                detailOpener.openEntryDetail(e)
                            },
                            onOpenUrl = { url ->
                                uriHandler.openUri(url)
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
                                                FavoritesMviModel.Intent.ToggleReblog(e),
                                            )
                                    }
                                }.takeIf { actionRepository.canReblog(entry.original) },
                            onBookmark =
                                { e: TimelineEntryModel ->
                                    model.reduce(FavoritesMviModel.Intent.ToggleBookmark(e))
                                }.takeIf { actionRepository.canBookmark(entry.original) },
                            onFavorite =
                                { e: TimelineEntryModel ->
                                    model.reduce(FavoritesMviModel.Intent.ToggleFavorite(e))
                                }.takeIf { actionRepository.canReact(entry.original) },
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
                                            FavoritesMviModel.Intent.SubmitPollVote(
                                                entry = e,
                                                choices = choices,
                                            ),
                                        )
                                    }
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
                                        model.reduce(FavoritesMviModel.Intent.TogglePin(entry))
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
                                            FavoritesMviModel.Intent.CopyToClipboard(entry.original),
                                        )
                                    else -> Unit
                                }
                            },
                        )
                        if (idx < uiState.entries.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.s),
                            )
                        }

                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        val isNearTheEnd = idx.isNearTheEnd(uiState.entries)
                        if (isNearTheEnd && canFetchMore) {
                            model.reduce(FavoritesMviModel.Intent.LoadNextPage)
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

        if (confirmDeleteEntryId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val entryId = confirmDeleteEntryId
                    confirmDeleteEntryId = null
                    if (confirm && entryId != null) {
                        model.reduce(FavoritesMviModel.Intent.DeleteEntry(entryId))
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
                                    FavoritesMviModel.Intent.MuteUser(
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
                            FavoritesMviModel.Intent.BlockUser(
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
                        model.reduce(FavoritesMviModel.Intent.ToggleReblog(e))
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
