package com.livefast.eattrash.raccoonforfriendica.feature.circles.manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.feature.circles.components.CircleItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.feature.circles.components.ManageCircleItem
import com.livefast.eattrash.raccoonforfriendica.feature.circles.di.ManageUserCirclesViewModelArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUserCirclesScreen(userId: String, modifier: Modifier = Modifier) {
    val model: ManageUserCirclesMviModel = getViewModel<ManageUserCirclesViewModel>(
        arg = ManageUserCirclesViewModelArgs(userId = userId),
    )
    val uiState by model.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var confirmAddItemId by remember { mutableStateOf<String?>(null) }
    var confirmRemoveItemId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val genericError = LocalStrings.current.messageGenericError

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
                    ManageUserCirclesMviModel.Effect.Error ->
                        snackbarHostState.showSnackbar(genericError)
                }
            }.launchIn(this)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar =
        {
            TopAppBar(
                modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                windowInsets = topAppBarState.toWindowInsets(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = LocalStrings.current.actionManageCircles,
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
        snackbarHost =
        {
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
            modifier = Modifier.padding(padding).fillMaxWidth(),
            isRefreshing = uiState.refreshing,
            onRefresh = {
                model.reduce(ManageUserCirclesMviModel.Intent.Refresh)
            },
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                if (uiState.initial) {
                    items(20) {
                        CircleItemPlaceholder(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(Spacing.interItem))
                    }
                }
                items(
                    items = uiState.items,
                    key = { e -> "manage-user-circles-${e.circle.id}" },
                ) { item ->
                    ManageCircleItem(
                        modifier = Modifier.padding(bottom = Spacing.interItem),
                        circle = item.circle,
                        belonging = item.belonging,
                        pending = item.pending,
                        onToggleBelonging = { newValue: Boolean ->
                            if (newValue) {
                                confirmAddItemId = item.circle.id
                            } else {
                                confirmRemoveItemId = item.circle.id
                            }
                        }.takeIf { item.circle.type == CircleType.UserDefined },
                    )
                }

                if (!uiState.initial &&
                    !uiState.refreshing &&
                    uiState.items.isEmpty()
                ) {
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
                    Spacer(modifier = Modifier.height(Spacing.xxxl))
                }
            }
        }

        if (confirmAddItemId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionAdd,
                onClose = { confirm ->
                    val itemId = confirmAddItemId
                    confirmAddItemId = null
                    if (confirm && itemId != null) {
                        model.reduce(ManageUserCirclesMviModel.Intent.Add(circleId = itemId))
                    }
                },
            )
        }

        if (confirmRemoveItemId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionRemove,
                onClose = { confirm ->
                    val itemId = confirmRemoveItemId
                    confirmRemoveItemId = null
                    if (confirm && itemId != null) {
                        model.reduce(ManageUserCirclesMviModel.Intent.Remove(circleId = itemId))
                    }
                },
            )
        }
    }
}
