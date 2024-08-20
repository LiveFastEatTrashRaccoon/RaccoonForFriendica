package com.livefast.eattrash.feature.userdetail.classic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserFields
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeaderPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toAccountSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toInt
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

class UserDetailScreen(
    private val id: String,
) : Screen {
    override val key: ScreenKey
        get() = super.key + id

    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalFoundationApi::class,
        ExperimentalMaterialApi::class,
    )
    @Composable
    override fun Content() {
        val model = getScreenModel<UserDetailMviModel>(parameters = { parametersOf(id) })
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val openUrl = remember { getOpenUrlUseCase(uriHandler) }
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val shareHelper = remember { getShareHelper() }
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var confirmUnfollowDialogOpen by remember { mutableStateOf(false) }
        var confirmDeleteFollowRequestDialogOpen by remember { mutableStateOf(false) }
        var confirmMuteNotificationsDialogOpen by remember { mutableStateOf(false) }

        suspend fun goBackToTop() {
            runCatching {
                if (lazyListState.firstVisibleItemIndex > 0) {
                    if (uiState.entries.isEmpty()) {
                        lazyListState.scrollToItem(1)
                    } else {
                        lazyListState.scrollToItem(2)
                    }
                } else {
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
                        UserDetailMviModel.Effect.BackToTop -> goBackToTop()
                    }
                }.launchIn(this)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = uiState.user?.handle.orEmpty(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
            content = { padding ->
                val pullRefreshState =
                    rememberPullRefreshState(
                        refreshing = uiState.refreshing,
                        onRefresh = {
                            model.reduce(UserDetailMviModel.Intent.Refresh)
                        },
                    )
                Box(
                    modifier =
                        Modifier
                            .padding(padding)
                            .fillMaxWidth()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .pullRefresh(pullRefreshState),
                ) {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        if (uiState.user != null) {
                            item {
                                UserHeader(
                                    user = uiState.user,
                                    onOpenUrl = { url ->
                                        openUrl(url)
                                    },
                                    onOpenImage = { url ->
                                        detailOpener.openImageDetail(url)
                                    },
                                    onRelationshipClicked = { nextAction ->
                                        when (nextAction) {
                                            RelationshipStatusNextAction.AcceptRequest -> {
                                                model.reduce(UserDetailMviModel.Intent.AcceptFollowRequest)
                                            }

                                            RelationshipStatusNextAction.ConfirmUnfollow -> {
                                                confirmUnfollowDialogOpen = true
                                            }

                                            RelationshipStatusNextAction.ConfirmDeleteFollowRequest -> {
                                                confirmDeleteFollowRequestDialogOpen = true
                                            }

                                            RelationshipStatusNextAction.Follow -> {
                                                model.reduce(UserDetailMviModel.Intent.Follow)
                                            }

                                            RelationshipStatusNextAction.Unfollow -> {
                                                model.reduce(UserDetailMviModel.Intent.Unfollow)
                                            }
                                        }
                                    },
                                    onNotificationsClicked = { nextAction ->
                                        when (nextAction) {
                                            NotificationStatusNextAction.Disable -> {
                                                confirmMuteNotificationsDialogOpen = true
                                            }

                                            NotificationStatusNextAction.Enable -> {
                                                model.reduce(UserDetailMviModel.Intent.EnableNotifications)
                                            }
                                        }
                                    },
                                    onOpenFollowers = {
                                        detailOpener.openFollowers(id)
                                    },
                                    onOpenFollowing = {
                                        detailOpener.openFollowing(id)
                                    },
                                    onOpenInForumMode = {
                                        detailOpener.openInForumMode(id)
                                    },
                                )
                            }
                        } else {
                            item {
                                UserHeaderPlaceholder(modifier = Modifier.fillMaxWidth())
                            }
                        }
                        item {
                            UserFields(
                                modifier =
                                    Modifier.padding(
                                        top = Spacing.m,
                                        bottom = Spacing.s,
                                    ),
                                fields =
                                    buildList {
                                        uiState.user?.created?.also { date ->
                                            add(
                                                FieldModel(
                                                    key = LocalStrings.current.accountAge,
                                                    value = date.prettifyDate(),
                                                ),
                                            )
                                        }
                                        addAll(uiState.user?.fields.orEmpty())
                                    },
                                onOpenUrl = { url ->
                                    openUrl(url)
                                },
                            )
                        }

                        stickyHeader {
                            SectionSelector(
                                modifier = Modifier.padding(bottom = Spacing.s),
                                titles =
                                    listOf(
                                        UserSection.Posts.toReadableName(),
                                        UserSection.All.toReadableName(),
                                        UserSection.Pinned.toReadableName(),
                                        UserSection.Media.toReadableName(),
                                    ),
                                scrollable = true,
                                currentSection = uiState.section.toInt(),
                                onSectionSelected = {
                                    val section = it.toAccountSection()
                                    model.reduce(
                                        UserDetailMviModel.Intent.ChangeSection(
                                            section,
                                        ),
                                    )
                                },
                            )
                        }

                        if (uiState.initial) {
                            items(5) {
                                TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.s),
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
                                    openUrl(url)
                                },
                                onOpenUser = {
                                    detailOpener.openUserDetail(it.id)
                                },
                                onOpenImage = { imageUrl ->
                                    detailOpener.openImageDetail(imageUrl)
                                },
                                onReblog = { e ->
                                    model.reduce(UserDetailMviModel.Intent.ToggleReblog(e))
                                },
                                onBookmark = { e ->
                                    model.reduce(UserDetailMviModel.Intent.ToggleBookmark(e))
                                },
                                onFavorite = { e ->
                                    model.reduce(UserDetailMviModel.Intent.ToggleFavorite(e))
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
                                model.reduce(UserDetailMviModel.Intent.LoadNextPage)
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
            },
        )

        if (confirmUnfollowDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    confirmUnfollowDialogOpen = false
                },
                title = {
                    Text(
                        text = LocalStrings.current.actionUnfollow,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmUnfollowDialogOpen = false
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmUnfollowDialogOpen = false
                            model.reduce(UserDetailMviModel.Intent.Unfollow)
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmDeleteFollowRequestDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    confirmDeleteFollowRequestDialogOpen = false
                },
                title = {
                    Text(
                        text = LocalStrings.current.actionDeleteFollowRequest,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmDeleteFollowRequestDialogOpen = false
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmDeleteFollowRequestDialogOpen = false
                            model.reduce(UserDetailMviModel.Intent.Unfollow)
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmMuteNotificationsDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    confirmMuteNotificationsDialogOpen = false
                },
                title = {
                    Text(
                        text = LocalStrings.current.actionMuteNotifications,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmMuteNotificationsDialogOpen = false
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmMuteNotificationsDialogOpen = false
                            model.reduce(UserDetailMviModel.Intent.DisableNotifications)
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }
    }
}
