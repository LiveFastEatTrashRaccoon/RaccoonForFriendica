package com.livefast.eattrash.raccoonforfriendica.feature.inbox

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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable.ConfigureNotificationTypeDialog
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable.NotificationItem
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable.NotificationItemPlaceholder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class InboxScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
                    actions = {
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
                    },
                )
            },
        ) { padding ->
            val pullRefreshState =
                rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = {
                        model.reduce(InboxMviModel.Intent.Refresh)
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
                    if (uiState.initial) {
                        items(5) {
                            NotificationItemPlaceholder(modifier = Modifier.fillMaxWidth())
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.interItem),
                            )
                        }
                    }

                    itemsIndexed(
                        items = uiState.notifications,
                        key = { _, e -> e.id },
                    ) { idx, notification ->
                        NotificationItem(
                            notification = notification,
                            blurNsfw = uiState.blurNsfw,
                            onOpenEntry = { entry ->
                                detailOpener.openEntryDetail(entry.id)
                                model.reduce(InboxMviModel.Intent.MarkAsRead(notification))
                            },
                            onOpenUrl = { url ->
                                uriHandler.openUri(url)
                            },
                            onOpenUser = {
                                detailOpener.openUserDetail(it.id)
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
                            onToggleSpoilerActive = { e ->
                                model.reduce(InboxMviModel.Intent.ToggleSpoilerActive(e))
                            },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.interItem),
                        )

                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        if (idx == uiState.notifications.lastIndex - 5 && canFetchMore) {
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
                                    if (uiState.isLogged) {
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
            AlertDialog(
                onDismissRequest = {
                    confirmUnfollowDialogUserId = null
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
                            confirmUnfollowDialogUserId = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val userId = confirmUnfollowDialogUserId ?: ""
                            confirmUnfollowDialogUserId = null
                            if (userId.isNotEmpty()) {
                                model.reduce(InboxMviModel.Intent.Unfollow(userId))
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmDeleteFollowRequestDialogUserId != null) {
            AlertDialog(
                onDismissRequest = {
                    confirmUnfollowDialogUserId = null
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
                            confirmUnfollowDialogUserId = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val userId = confirmUnfollowDialogUserId ?: ""
                            confirmUnfollowDialogUserId = null
                            if (userId.isNotEmpty()) {
                                model.reduce(InboxMviModel.Intent.Unfollow(userId))
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
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
    }
}
