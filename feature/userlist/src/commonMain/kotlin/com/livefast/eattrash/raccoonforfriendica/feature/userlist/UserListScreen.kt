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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toUserListType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

class UserListScreen(
    private val type: Int,
    private val userId: String? = null,
    private val entryId: String? = null,
    private val infoCount: Int? = null,
) : Screen {
    override val key: ScreenKey
        get() =
            super.key +
                when (type.toUserListType()) {
                    is UserListType.Follower ->
                        buildString {
                            append("follower-")
                            append(userId)
                        }

                    is UserListType.Following ->
                        buildString {
                            append("following-")
                            append(userId)
                        }

                    is UserListType.UsersFavorite ->
                        buildString {
                            append("favorites-")
                            append(entryId)
                        }

                    is UserListType.UsersReblog ->
                        buildString {
                            append("reblogs-")
                            append(entryId)
                        }
                }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model =
            getScreenModel<UserListMviModel>(parameters = {
                parametersOf(
                    type.toUserListType(),
                    userId,
                    entryId,
                )
            })
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

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        UserListMviModel.Effect.BackToTop -> goBackToTop()
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
                            modifier = Modifier.padding(horizontal = Spacing.s),
                            text =
                                buildString {
                                    when (type.toUserListType()) {
                                        is UserListType.Follower -> {
                                            append(LocalStrings.current.followerTitle)
                                            val handle = uiState.user?.handle.orEmpty()
                                            if (handle.isNotEmpty()) {
                                                append(" (")
                                                append(uiState.user?.handle.orEmpty())
                                                append(")")
                                            }
                                        }

                                        is UserListType.Following -> {
                                            append(LocalStrings.current.followingTitle)
                                            val handle = uiState.user?.handle.orEmpty()
                                            if (handle.isNotEmpty()) {
                                                append(" (")
                                                append(handle)
                                                append(")")
                                            }
                                        }

                                        is UserListType.UsersFavorite -> {
                                            val count = infoCount ?: 0
                                            if (count > 0) {
                                                append(count)
                                                append(" ")
                                                append(
                                                    LocalStrings.current.extendedSocialInfoFavorites(
                                                        count,
                                                    ),
                                                )
                                            }
                                        }

                                        is UserListType.UsersReblog -> {
                                            val count = infoCount ?: 0
                                            if (count > 0) {
                                                append(count)
                                                append(" ")
                                                append(
                                                    LocalStrings.current.extendedSocialInfoReblogs(
                                                        count,
                                                    ),
                                                )
                                            }
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
                                detailOpener.openUserDetail(user)
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

                        val isNearTheEnd =
                            idx == uiState.users.lastIndex - 5 || uiState.users.size < 5
                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        if (isNearTheEnd && canFetchMore) {
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
            CustomConfirmDialog(
                title = LocalStrings.current.actionUnfollow,
                onClose = { confirm ->
                    val userId = confirmUnfollowDialogUserId
                    confirmUnfollowDialogUserId = null
                    if (confirm && userId != null) {
                        model.reduce(UserListMviModel.Intent.Unfollow(userId))
                    }
                },
            )
        }

        if (confirmDeleteFollowRequestDialogUserId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDeleteFollowRequest,
                onClose = { confirm ->
                    val userId = confirmDeleteFollowRequestDialogUserId
                    confirmDeleteFollowRequestDialogUserId = null
                    if (confirm && userId != null) {
                        model.reduce(UserListMviModel.Intent.Unfollow(userId))
                    }
                },
            )
        }
    }
}
