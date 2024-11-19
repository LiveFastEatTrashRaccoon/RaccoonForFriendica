package com.livefast.eattrash.raccoonforfriendica.feature.inbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable.ConfigureNotificationTypeDialog
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable.NotificationItem
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable.NotificationItemPlaceholder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class InboxScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<InboxMviModel>()
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
        var confirmUnfollowDialogUserId by remember { mutableStateOf<String?>(null) }
        var confirmDeleteFollowRequestDialogUserId by remember { mutableStateOf<String?>(null) }
        var confirmDismissAllDialogOpen by remember { mutableStateOf(false) }
        var configureSelectedTypesDialogOpen by remember { mutableStateOf(false) }

        suspend fun goBackToTop() {
            runCatching {
                lazyListState.scrollToItem(0)
                topAppBarState.heightOffset = 0f
                topAppBarState.contentOffset = 0f
            }
        }

        LaunchedEffect(navigationCoordinator) {
            navigationCoordinator.onDoubleTabSelection
                .onEach { section ->
                    if (section is BottomNavigationSection.Inbox) {
                        goBackToTop()
                    }
                }.launchIn(this)
        }
        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        InboxMviModel.Effect.BackToTop -> goBackToTop()
                    }
                }.launchIn(this)
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.sectionTitleInbox,
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
                            IconButton(
                                onClick = {
                                    configureSelectedTypesDialogOpen = true
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                )
                            }
                            IconButton(
                                enabled = uiState.notifications.isNotEmpty(),
                                onClick = {
                                    confirmDismissAllDialogOpen = true
                                },
                            ) {
                                if (uiState.markAllAsReadLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(IconSize.s),
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
                    },
                )
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
                            if (connection != null && uiState.hideNavigationBarWhileScrolling) {
                                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            } else {
                                Modifier
                            },
                        ),
                isRefreshing = uiState.refreshing,
                onRefresh = {
                    model.reduce(InboxMviModel.Intent.Refresh)
                },
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    if (uiState.initial) {
                        val placeholderCount = 5
                        items(placeholderCount) { idx ->
                            NotificationItemPlaceholder(modifier = Modifier.fillMaxWidth())
                            if (idx < placeholderCount - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.interItem),
                                )
                            }
                        }
                    }

                    itemsIndexed(
                        items = uiState.notifications,
                        key = { _, e -> "inbox-${e.type}-${e.id}" },
                    ) { idx, notification ->
                        NotificationItem(
                            notification = notification,
                            blurNsfw = uiState.blurNsfw,
                            autoloadImages = uiState.autoloadImages,
                            maxBodyLines = uiState.maxBodyLines,
                            onOpenEntry = { entry ->
                                detailOpener.openEntryDetail(entry)
                                model.reduce(InboxMviModel.Intent.MarkAsRead(notification))
                            },
                            onOpenUrl = { url ->
                                uriHandler.openUri(url)
                            },
                            onOpenUser = {
                                detailOpener.openUserDetail(it)
                                model.reduce(InboxMviModel.Intent.MarkAsRead(notification))
                            },
                            onUserRelationshipClicked = { userId, nextAction ->
                                model.reduce(InboxMviModel.Intent.MarkAsRead(notification))
                                when (nextAction) {
                                    RelationshipStatusNextAction.AcceptRequest -> {
                                        detailOpener.openFollowRequests()
                                    }

                                    RelationshipStatusNextAction.ConfirmUnfollow -> {
                                        confirmUnfollowDialogUserId = userId
                                    }

                                    RelationshipStatusNextAction.ConfirmDeleteFollowRequest -> {
                                        confirmDeleteFollowRequestDialogUserId = userId
                                    }

                                    RelationshipStatusNextAction.Follow -> {
                                        model.reduce(InboxMviModel.Intent.Follow(userId))
                                    }

                                    RelationshipStatusNextAction.Unfollow -> {
                                        model.reduce(InboxMviModel.Intent.Unfollow(userId))
                                    }
                                }
                            },
                        )
                        if (idx < uiState.notifications.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.interItem),
                            )
                        }

                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        val isNearTheEnd = idx.isNearTheEnd(uiState.notifications)
                        if (isNearTheEnd && canFetchMore) {
                            model.reduce(InboxMviModel.Intent.LoadNextPage)
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
                    if (!uiState.initial && !uiState.loading && !uiState.refreshing && uiState.notifications.isEmpty()) {
                        item {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                                text =
                                    if (uiState.currentUserId != null) {
                                        LocalStrings.current.messageEmptyInbox
                                    } else {
                                        LocalStrings.current.messageUserUnlogged
                                    },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                            )
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
                    val userId = confirmUnfollowDialogUserId ?: ""
                    confirmUnfollowDialogUserId = null
                    if (confirm && userId.isNotEmpty()) {
                        model.reduce(InboxMviModel.Intent.Unfollow(userId))
                    }
                },
            )
        }

        if (confirmDeleteFollowRequestDialogUserId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDeleteFollowRequest,
                onClose = { confirm ->
                    val userId = confirmUnfollowDialogUserId ?: ""
                    confirmUnfollowDialogUserId = null
                    if (confirm && userId.isNotEmpty()) {
                        model.reduce(InboxMviModel.Intent.Unfollow(userId))
                    }
                },
            )
        }

        if (configureSelectedTypesDialogOpen) {
            ConfigureNotificationTypeDialog(
                initialSelection = uiState.selectedNotificationTypes,
                availableTypes = NotificationType.ALL,
                onClose = { values ->
                    configureSelectedTypesDialogOpen = false
                    if (values != null) {
                        model.reduce(InboxMviModel.Intent.ChangeSelectedNotificationTypes(values))
                    }
                },
            )
        }

        if (confirmDismissAllDialogOpen) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDismissAllNotifications,
                body = LocalStrings.current.messageAreYouSure,
                onClose = { confirm ->
                    confirmDismissAllDialogOpen = false
                    if (confirm) {
                        model.reduce(InboxMviModel.Intent.DismissAll)
                    }
                },
            )
        }
    }
}
