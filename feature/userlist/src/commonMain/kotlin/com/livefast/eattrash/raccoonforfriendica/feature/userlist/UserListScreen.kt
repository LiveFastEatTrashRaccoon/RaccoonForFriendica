package com.livefast.eattrash.raccoonforfriendica.feature.userlist

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.getFileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toUserListType
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.di.UserListViewModelArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    type: Int,
    modifier: Modifier = Modifier,
    userId: String? = null,
    entryId: String? = null,
    infoCount: Int? = null,
    enableExport: Boolean = false,
) {
    val model: UserListMviModel =
        getViewModel<UserListViewModel>(
            arg =
            UserListViewModelArgs(
                type.toUserListType(),
                userId.orEmpty(),
                entryId.orEmpty(),
            ),
        )
    val uiState by model.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val snackbarHostState = remember { SnackbarHostState() }
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val mainRouter = remember { getMainRouter() }
    val fileSystemManager = remember { getFileSystemManager() }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val successMessage = LocalStrings.current.messageSuccess
    val errorMessage = LocalStrings.current.messageGenericError
    var confirmUnfollowDialogUserId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteFollowRequestDialogUserId by remember { mutableStateOf<String?>(null) }
    var listContent by remember { mutableStateOf<String?>(null) }

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
                    is UserListMviModel.Effect.SaveList -> {
                        if (event.content.isEmpty()) {
                            snackbarHostState.showSnackbar(errorMessage)
                        } else {
                            listContent = event.content
                        }
                    }
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
                actions = {
                    val options =
                        buildList {
                            if (enableExport) {
                                this +=
                                    CustomOptions.Export.toOption(
                                        label = LocalStrings.current.actionExport,
                                    )
                            }
                        }
                    if (options.isNotEmpty()) {
                        Box {
                            var optionsOffset by remember { mutableStateOf(Offset.Zero) }
                            var optionsMenuOpen by remember { mutableStateOf(false) }
                            IconButton(
                                modifier =
                                Modifier.onGloballyPositioned {
                                    optionsOffset = it.positionInParent()
                                },
                                onClick = {
                                    optionsMenuOpen = true
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = LocalStrings.current.actionOpenOptions,
                                )
                            }

                            CustomDropDown(
                                expanded = optionsMenuOpen,
                                onDismiss = {
                                    optionsMenuOpen = false
                                },
                                offset =
                                with(LocalDensity.current) {
                                    DpOffset(
                                        x = optionsOffset.x.toDp(),
                                        y = optionsOffset.y.toDp(),
                                    )
                                },
                            ) {
                                for (option in options) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(option.label)
                                        },
                                        onClick = {
                                            optionsMenuOpen = false
                                            when (option.id) {
                                                CustomOptions.Export -> {
                                                    model.reduce(UserListMviModel.Intent.Export)
                                                }

                                                else -> Unit
                                            }
                                        },
                                    )
                                }
                            }
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
                model.reduce(UserListMviModel.Intent.Refresh)
            },
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

                if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.users.isEmpty()) {
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
                    items = uiState.users,
                    key = { _, e -> "user-list-${e.id}" },
                ) { idx, user ->
                    UserItem(
                        user = user,
                        autoloadImages = uiState.autoloadImages,
                        onClick = {
                            mainRouter.openUserDetail(user)
                        },
                        onRelationshipClick = { nextAction ->
                            when (nextAction) {
                                RelationshipStatusNextAction.AcceptRequest -> {
                                    mainRouter.openFollowRequests()
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

                    val isNearTheEnd = idx.isNearTheEnd(uiState.users)
                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(UserListMviModel.Intent.LoadNextPage)
                    }
                }
            }
        }
    }

    if (uiState.operationInProgress) {
        ProgressHud()
    }

    if (confirmUnfollowDialogUserId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionUnfollow,
            onClose = { confirm ->
                val unfollowUserId = confirmUnfollowDialogUserId
                confirmUnfollowDialogUserId = null
                if (confirm && unfollowUserId != null) {
                    model.reduce(UserListMviModel.Intent.Unfollow(unfollowUserId))
                }
            },
        )
    }

    if (confirmDeleteFollowRequestDialogUserId != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDeleteFollowRequest,
            onClose = { confirm ->
                val deleteUserId = confirmDeleteFollowRequestDialogUserId
                confirmDeleteFollowRequestDialogUserId = null
                if (confirm && deleteUserId != null) {
                    model.reduce(UserListMviModel.Intent.Unfollow(deleteUserId))
                }
            },
        )
    }

    listContent?.also { content ->
        fileSystemManager.writeToFile(
            mimeType = EXPORT_LIST_MIME_TYPE,
            name = EXPORT_LIST_FILE_NAME,
            data = content,
        ) { success ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (success) {
                        successMessage
                    } else {
                        errorMessage
                    },
                )
            }
            listContent = null
        }
    }
}

private sealed interface CustomOptions : OptionId.Custom {
    data object Export : CustomOptions
}

private const val EXPORT_LIST_MIME_TYPE = "text/plain"
private const val EXPORT_LIST_FILE_NAME = "user_list.txt"
