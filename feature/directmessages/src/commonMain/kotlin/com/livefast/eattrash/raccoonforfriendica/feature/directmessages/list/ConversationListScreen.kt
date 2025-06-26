package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SelectUserDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.components.ConversationItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(modifier: Modifier = Modifier) {
    val model: ConversationListMviModel = getViewModel<ConversationListViewModel>()
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val mainRouter = remember { getMainRouter() }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val lazyListState = rememberLazyListState()
    val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
    val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
    val scope = rememberCoroutineScope()
    var selectUserToCreateConversationDialogOpen by remember { mutableStateOf(false) }

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
                    ConversationListMviModel.Effect.BackToTop -> goBackToTop()
                }
            }.launchIn(this)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                windowInsets = topAppBarState.toWindowInsets(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = LocalStrings.current.directMessagesTitle,
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
                        selectUserToCreateConversationDialogOpen = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = LocalStrings.current.actionAddNew,
                    )
                }
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
                model.reduce(ConversationListMviModel.Intent.Refresh)
            },
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                if (uiState.initial) {
                    val placeholderCount = 10
                    items(placeholderCount) { idx ->
                        GenericPlaceholder(height = 120.dp)
                        if (idx < placeholderCount - 1) {
                            TimelineDivider()
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
                    key = { _, e -> "direct-message-list-${e.lastMessage.id}" },
                ) { idx, conversation ->
                    ConversationItem(
                        conversation = conversation,
                        autoloadImages = uiState.autoloadImages,
                        onClick = {
                            val parentUri = conversation.lastMessage.parentUri
                            if (parentUri != null) {
                                model.reduce(
                                    ConversationListMviModel.Intent.MarkConversationAsRead(idx),
                                )
                                mainRouter.openConversation(
                                    otherUser = conversation.otherUser,
                                    parentUri = parentUri,
                                )
                            }
                        },
                    )
                    if (idx < uiState.items.lastIndex) {
                        TimelineDivider()
                    }

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    val isNearTheEnd = idx.isNearTheEnd(uiState.items)
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(ConversationListMviModel.Intent.LoadNextPage)
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

    if (selectUserToCreateConversationDialogOpen) {
        SelectUserDialog(
            query = uiState.userSearchQuery,
            users = uiState.userSearchUsers,
            autoloadImages = uiState.autoloadImages,
            loading = uiState.userSearchLoading,
            canFetchMore = uiState.userSearchCanFetchMore,
            onSearch = {
                model.reduce(ConversationListMviModel.Intent.UserSearchSetQuery(it))
            },
            onLoadMoreUsers = {
                model.reduce(ConversationListMviModel.Intent.UserSearchLoadNextPage)
            },
            onClose = { user ->
                model.reduce(ConversationListMviModel.Intent.UserSearchSetQuery(""))
                model.reduce(ConversationListMviModel.Intent.UserSearchClear)
                selectUserToCreateConversationDialogOpen = false
                val userId = user?.id
                if (userId != null) {
                    val existingConversation =
                        uiState.items.firstOrNull { it.otherUser.id == userId }
                    mainRouter.openConversation(
                        otherUser = user,
                        parentUri = existingConversation?.lastMessage?.parentUri.orEmpty(),
                    )
                }
            },
        )
    }
}
