package com.livefast.eattrash.raccoonforfriendica.feature.thread

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.feature.thread.composable.TimelineReplyItem
import kotlinx.coroutines.launch
import org.koin.core.parameter.parameterArrayOf

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
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }
        var confirmMuteEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var confirmBlockEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }

        fun goBackToTop() {
            runCatching {
                scope.launch {
                    lazyListState.scrollToItem(0)
                    topAppBarState.heightOffset = 0f
                    topAppBarState.contentOffset = 0f
                }
            }
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
                            Image(
                                modifier =
                                    Modifier.clickable {
                                        navigationCoordinator.pop()
                                    },
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            )
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
                                        inReplyToUsername = entry.creator?.username,
                                        inReplyToHandle = entry.creator?.handle,
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
                    if (uiState.initial) {
                        items(5) {
                            TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.s),
                            )
                        }
                    }

                    // original entry
                    uiState.entry?.also { original ->
                        item {
                            TimelineItem(
                                modifier =
                                    Modifier
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            shape = RoundedCornerShape(CornerSize.l),
                                        ).padding(
                                            vertical = Spacing.s,
                                            horizontal = Spacing.xxxs,
                                        ),
                                entry = original,
                                reshareAndReplyVisible = false,
                                blurNsfw = uiState.blurNsfw,
                                onOpenUrl = { url ->
                                    uriHandler.openUri(url)
                                },
                                onOpenUser = {
                                    detailOpener.openUserDetail(it.id)
                                },
                                onOpenImage = { imageUrl ->
                                    detailOpener.openImageDetail(imageUrl)
                                },
                                onReblog = { e ->
                                    model.reduce(ThreadMviModel.Intent.ToggleReblog(e))
                                },
                                onBookmark = { e ->
                                    model.reduce(ThreadMviModel.Intent.ToggleBookmark(e))
                                },
                                onFavorite = { e ->
                                    model.reduce(ThreadMviModel.Intent.ToggleFavorite(e))
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
                                onReply = { e ->
                                    detailOpener.openComposer(
                                        inReplyToId = e.id,
                                        inReplyToHandle = e.creator?.handle,
                                        inReplyToUsername =
                                            e.creator?.let {
                                                it.displayName ?: it.username
                                            },
                                    )
                                },
                                options =
                                    buildList {
                                        if (!uiState.entry?.url.isNullOrBlank()) {
                                            this += OptionId.Share.toOption()
                                            this += OptionId.CopyUrl.toOption()
                                        }
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

                                        else -> Unit
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.height(Spacing.s))
                        }
                    }

                    // replies
                    items(
                        items = uiState.replies,
                        key = { it.safeKey },
                    ) { entry ->
                        TimelineReplyItem(
                            entry = entry,
                            onOpenUrl = { url ->
                                uriHandler.openUri(url)
                            },
                            onOpenUser = {
                                detailOpener.openUserDetail(it.id)
                            },
                            onOpenImage = { url ->
                                detailOpener.openImageDetail(url)
                            },
                            onReblog = { e ->
                                model.reduce(ThreadMviModel.Intent.ToggleReblog(e))
                            },
                            onBookmark = { e ->
                                model.reduce(ThreadMviModel.Intent.ToggleBookmark(e))
                            },
                            onFavorite = { e ->
                                model.reduce(ThreadMviModel.Intent.ToggleFavorite(e))
                            },
                            onReply = { e ->
                                detailOpener.openComposer(
                                    inReplyToId = e.id,
                                    inReplyToHandle = e.creator?.handle,
                                    inReplyToUsername =
                                        e.creator?.let {
                                            it.displayName ?: it.username
                                        },
                                )
                            },
                            options =
                                buildList {
                                    if (!entry.url.isNullOrBlank()) {
                                        this += OptionId.Share.toOption()
                                        this += OptionId.CopyUrl.toOption()
                                    }
                                    if (entry.reblog?.creator?.id == uiState.currentUserId) {
                                        this += OptionId.Edit.toOption()
                                        this += OptionId.Delete.toOption()
                                    } else if (uiState.currentUserId != null) {
                                        this += OptionId.Mute.toOption()
                                        this += OptionId.Block.toOption()
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
                                            snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                        }
                                    }

                                    OptionId.Edit -> {
                                        detailOpener.openComposer(
                                            groupHandle = entry.creator?.handle,
                                            groupUsername = entry.creator?.username,
                                            editedPostId = entry.reblog?.id,
                                        )
                                    }

                                    OptionId.Delete -> confirmDeleteEntryId = entry.id
                                    OptionId.Mute -> confirmMuteEntry = entry
                                    OptionId.Block -> confirmBlockEntry = entry
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

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )
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
            AlertDialog(
                onDismissRequest = {
                    confirmDeleteEntryId = null
                },
                title = {
                    Text(
                        text = LocalStrings.current.actionDelete,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmDeleteEntryId = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val entryId = confirmDeleteEntryId ?: ""
                            confirmDeleteEntryId = null
                            if (entryId.isNotEmpty()) {
                                model.reduce(ThreadMviModel.Intent.DeleteEntry(entryId))
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmMuteEntry != null) {
            val creator = confirmMuteEntry?.reblog?.creator ?: confirmMuteEntry?.creator
            AlertDialog(
                onDismissRequest = {
                    confirmMuteEntry = null
                },
                title = {
                    Text(
                        text =
                            buildString {
                                append(LocalStrings.current.actionMute)
                                val handle = creator?.handle ?: ""
                                if (handle.isNotEmpty()) {
                                    append(" @$handle")
                                }
                            },
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmMuteEntry = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val entryId = confirmMuteEntry?.id
                            val creatorId = creator?.id
                            confirmMuteEntry = null
                            if (entryId != null && creatorId != null) {
                                model.reduce(
                                    ThreadMviModel.Intent.MuteUser(
                                        userId = creatorId,
                                        entryId = entryId,
                                    ),
                                )
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmBlockEntry != null) {
            val creator = confirmBlockEntry?.reblog?.creator ?: confirmBlockEntry?.creator
            AlertDialog(
                onDismissRequest = {
                    confirmBlockEntry = null
                },
                title = {
                    Text(
                        text =
                            buildString {
                                append(LocalStrings.current.actionBlock)
                                val handle = creator?.handle ?: ""
                                if (handle.isNotEmpty()) {
                                    append(" @$handle")
                                }
                            },
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmBlockEntry = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val entryId = confirmBlockEntry?.id
                            val creatorId = creator?.id
                            confirmBlockEntry = null
                            if (entryId != null && creatorId != null) {
                                model.reduce(
                                    ThreadMviModel.Intent.BlockUser(
                                        userId = creatorId,
                                        entryId = entryId,
                                    ),
                                )
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }
    }
}
