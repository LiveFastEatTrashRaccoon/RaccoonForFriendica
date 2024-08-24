package com.livefast.eattrash.raccoonforfriendica.feature.userlist

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

class UserListScreen(
    private val type: UserListType,
) : Screen {
    override val key: ScreenKey
        get() =
            super.key +
                when (type) {
                    is UserListType.Follower ->
                        buildString {
                            append("follower-")
                            append(type.userId)
                        }

                    is UserListType.Following ->
                        buildString {
                            append("following-")
                            append(type.userId)
                        }

                    is UserListType.UsersFavorite ->
                        buildString {
                            append("favorites-")
                            append(type.entryId)
                        }

                    is UserListType.UsersReblog ->
                        buildString {
                            append("reblogs-")
                            append(type.entryId)
                        }
                }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<UserListMviModel>(parameters = { parametersOf(type) })
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        var confirmUnfollowDialogUserId by remember { mutableStateOf<String?>(null) }
        var confirmDeleteFollowRequestDialogUserId by remember { mutableStateOf<String?>(null) }

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
                            text =
                                buildString {
                                    when (type) {
                                        is UserListType.Follower -> {
                                            append(LocalStrings.current.followerTitle)
                                            append(" (")
                                            append(uiState.user?.handle.orEmpty())
                                            append(")")
                                        }

                                        is UserListType.Following -> {
                                            append(LocalStrings.current.followingTitle)
                                            append(" (")
                                            append(uiState.user?.handle.orEmpty())
                                            append(")")
                                        }

                                        is UserListType.UsersFavorite -> {
                                            append(type.count)
                                            append(" ")
                                            append(LocalStrings.current.extendedSocialInfoFavorites(type.count))
                                        }

                                        is UserListType.UsersReblog -> {
                                            append(type.count)
                                            append(" ")
                                            append(LocalStrings.current.extendedSocialInfoReblogs(type.count))
                                        }
                                    }
                                },
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
        ) { padding ->
            val pullRefreshState =
                rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = {
                        model.reduce(UserListMviModel.Intent.Refresh)
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
                    if (uiState.initial) {
                        items(20) {
                            UserItemPlaceholder(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(Spacing.interItem))
                        }
                    }

                    itemsIndexed(
                        items = uiState.users,
                        key = { _, e -> e.id },
                    ) { idx, user ->
                        UserItem(
                            user = user,
                            onClick = {
                                detailOpener.openUserDetail(user.id)
                            },
                            onRelationshipClicked = { nextAction ->
                                when (nextAction) {
                                    RelationshipStatusNextAction.AcceptRequest -> {
                                        detailOpener.openFollowRequests()
                                    }

                                    RelationshipStatusNextAction.ConfirmUnfollow -> {
                                        confirmUnfollowDialogUserId = user.id
                                    }

                                    RelationshipStatusNextAction.ConfirmDeleteFollowRequest -> {
                                        confirmDeleteFollowRequestDialogUserId = user.id
                                    }

                                    RelationshipStatusNextAction.Follow -> {
                                        model.reduce(UserListMviModel.Intent.Follow(user.id))
                                    }

                                    RelationshipStatusNextAction.Unfollow -> {
                                        model.reduce(UserListMviModel.Intent.Unfollow(user.id))
                                    }
                                }
                            },
                        )
                        Spacer(modifier = Modifier.height(Spacing.interItem))

                        if (idx == uiState.users.lastIndex - 5 && uiState.canFetchMore) {
                            model.reduce(UserListMviModel.Intent.LoadNextPage)
                        }
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
                                model.reduce(UserListMviModel.Intent.Unfollow(userId))
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
                                model.reduce(UserListMviModel.Intent.Unfollow(userId))
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
