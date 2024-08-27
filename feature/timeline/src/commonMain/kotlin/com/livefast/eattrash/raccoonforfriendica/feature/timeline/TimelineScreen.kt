package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TimelineScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<TimelineMviModel>()
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
        val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
        val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
        val bottomNavigationInset =
            with(LocalDensity.current) {
                WindowInsets.navigationBars.getBottom(this).toDp()
            }
        val snackbarHostState = remember { SnackbarHostState() }
        val shareHelper = remember { getShareHelper() }
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var timelineTypeSelectorOpen by remember { mutableStateOf(false) }
        var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }
        var confirmMuteEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var confirmBlockEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var pollErrorDialogOpened by remember { mutableStateOf(false) }

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
                        TimelineMviModel.Effect.BackToTop -> goBackToTop()
                        TimelineMviModel.Effect.PollVoteFailure -> pollErrorDialogOpened = true
                    }
                }.launchIn(this)
        }
        LaunchedEffect(navigationCoordinator) {
            navigationCoordinator.onDoubleTabSelection
                .onEach { section ->
                    if (section == BottomNavigationSection.Home) {
                        goBackToTop()
                    }
                }.launchIn(this)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    windowInsets = topAppBarState.toWindowInsets(),
                    modifier =
                        Modifier.clickable {
                            if (uiState.availableTimelineTypes.isNotEmpty()) {
                                timelineTypeSelectorOpen = true
                            }
                        },
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = uiState.timelineType?.toReadableName().orEmpty(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        Image(
                            modifier =
                                Modifier.clickable {
                                    scope.launch {
                                        drawerCoordinator.toggleDrawer()
                                    }
                                },
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        )
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
                            modifier =
                                Modifier.padding(
                                    bottom = Dimensions.floatingActionButtonBottomInset + bottomNavigationInset,
                                ),
                            onClick = {
                                detailOpener.openComposer()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
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
                        model.reduce(TimelineMviModel.Intent.Refresh)
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
                        key = { _, e -> e.safeKey },
                    ) { idx, entry ->
                        TimelineItem(
                            entry = entry,
                            blurNsfw = uiState.blurNsfw,
                            onClick = { e ->
                                detailOpener.openEntryDetail(e.id)
                            },
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
                                model.reduce(TimelineMviModel.Intent.ToggleReblog(e))
                            },
                            onBookmark = { e ->
                                model.reduce(TimelineMviModel.Intent.ToggleBookmark(e))
                            },
                            onFavorite = { e ->
                                model.reduce(TimelineMviModel.Intent.ToggleFavorite(e))
                            },
                            onPollVote = { e, choices ->
                                model.reduce(
                                    TimelineMviModel.Intent.SubmitPollVote(
                                        entry = e,
                                        choices = choices,
                                    ),
                                )
                            },
                            onReply = { e ->
                                detailOpener.openComposer(
                                    inReplyToId = e.id,
                                    inReplyToUsername =
                                        e.creator?.let {
                                            it.displayName ?: it.username
                                        },
                                    inReplyToHandle = e.creator?.handle,
                                )
                            },
                            onToggleSpoilerActive = { e ->
                                model.reduce(TimelineMviModel.Intent.ToggleSpoilerActive(e))
                            },
                            options =
                                buildList {
                                    if (!entry.url.isNullOrBlank()) {
                                        this += OptionId.Share.toOption()
                                        this += OptionId.CopyUrl.toOption()
                                    }
                                    val currentUserId = uiState.currentUserId
                                    val creatorId = entry.reblog?.creator?.id ?: entry.creator?.id
                                    if (creatorId == currentUserId) {
                                        this += OptionId.Edit.toOption()
                                        this += OptionId.Delete.toOption()
                                        if (entry.reblog == null) {
                                            if (entry.pinned) {
                                                this += OptionId.Unpin.toOption()
                                            } else {
                                                this += OptionId.Pin.toOption()
                                            }
                                        }
                                    } else if (currentUserId != null) {
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
                                        (entry.reblog ?: entry).also { entryToEdit ->
                                            detailOpener.openComposer(
                                                inReplyToId = entryToEdit.inReplyTo?.id,
                                                inReplyToHandle = entryToEdit.inReplyTo?.creator?.handle,
                                                inReplyToUsername = entryToEdit.inReplyTo?.creator?.username,
                                                editedPostId = entryToEdit.id,
                                            )
                                        }
                                    }

                                    OptionId.Delete -> confirmDeleteEntryId = entry.id
                                    OptionId.Mute -> confirmMuteEntry = entry
                                    OptionId.Block -> confirmBlockEntry = entry
                                    OptionId.Pin, OptionId.Unpin ->
                                        model.reduce(
                                            TimelineMviModel.Intent.TogglePin(entry),
                                        )
                                    else -> Unit
                                }
                            },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )

                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        if (idx == uiState.entries.lastIndex - 5 && canFetchMore) {
                            model.reduce(TimelineMviModel.Intent.LoadNextPage)
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

        if (timelineTypeSelectorOpen) {
            CustomModalBottomSheet(
                title = LocalStrings.current.feedTypeTitle,
                items =
                    uiState.availableTimelineTypes.map {
                        CustomModalBottomSheetItem(
                            label = it.toReadableName(),
                            trailingContent = {
                                Icon(
                                    modifier = Modifier.size(IconSize.m),
                                    imageVector = it.toIcon(),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            },
                        )
                    },
                onSelected = { index ->
                    timelineTypeSelectorOpen = false
                    if (index != null) {
                        val type = uiState.availableTimelineTypes[index]
                        model.reduce(TimelineMviModel.Intent.ChangeType(type))
                    }
                },
            )
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
                                model.reduce(TimelineMviModel.Intent.DeleteEntry(entryId))
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
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
                                    TimelineMviModel.Intent.MuteUser(
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
                                    TimelineMviModel.Intent.BlockUser(
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

        if (pollErrorDialogOpened) {
            PollVoteErrorDialog(
                onDismissRequest = {
                    pollErrorDialogOpened = false
                },
            )
        }
    }
}
