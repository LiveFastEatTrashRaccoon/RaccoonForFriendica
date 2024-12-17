package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.canBeEdited
import com.livefast.eattrash.raccoonforfriendica.feature.circles.components.CircleEditorDialog
import com.livefast.eattrash.raccoonforfriendica.feature.circles.components.CircleHeader
import com.livefast.eattrash.raccoonforfriendica.feature.circles.components.CircleItem
import com.livefast.eattrash.raccoonforfriendica.feature.circles.components.CircleItemPlaceholder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CirclesScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<CirclesMviModel>()
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val lazyListState = rememberLazyListState()
        val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
        val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
        val scope = rememberCoroutineScope()
        val detailOpener = remember { getDetailOpener() }
        var confirmDeleteItemId by remember { mutableStateOf<String?>(null) }
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
                        CirclesMviModel.Effect.Failure ->
                            snackbarHostState.showSnackbar(genericError)
                        is CirclesMviModel.Effect.OpenUser ->
                            detailOpener.openUserDetail(event.user)

                        is CirclesMviModel.Effect.OpenCircle ->
                            detailOpener.openCircleTimeline(event.circle)
                    }
                }.launchIn(this)
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.manageCirclesTitle,
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
                            model.reduce(CirclesMviModel.Intent.OpenEditor())
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = LocalStrings.current.actionAddNew,
                        )
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
                        ).nestedScroll(fabNestedScrollConnection),
                isRefreshing = uiState.refreshing,
                onRefresh = {
                    model.reduce(CirclesMviModel.Intent.Refresh)
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
                        key = { e ->
                            when (e) {
                                is CircleListItem.Circle -> "circles-${e.circle.id}"
                                is CircleListItem.Header -> "circles-${e.type}"
                            }
                        },
                    ) { item ->
                        when (item) {
                            is CircleListItem.Header ->
                                CircleHeader(
                                    modifier = Modifier.padding(bottom = Spacing.xs),
                                    type = item.type,
                                )

                            is CircleListItem.Circle -> {
                                CircleItem(
                                    modifier = Modifier.padding(bottom = Spacing.interItem),
                                    circle = item.circle,
                                    onClick = {
                                        model.reduce(CirclesMviModel.Intent.OpenDetail(item.circle))
                                    },
                                    options =
                                        buildList {
                                            if (item.circle.canBeEdited) {
                                                this += OptionId.Edit.toOption()
                                                this += OptionId.Delete.toOption()
                                                this +=
                                                    CustomOptions.EditMembers.toOption(
                                                        label = LocalStrings.current.actionEditMembers,
                                                    )
                                            }
                                        },
                                    onOptionSelected = { optionId ->
                                        when (optionId) {
                                            OptionId.Edit -> {
                                                model.reduce(CirclesMviModel.Intent.OpenEditor(item.circle))
                                            }
                                            CustomOptions.EditMembers -> {
                                                detailOpener.openCircleEditMembers(item.circle.id)
                                            }

                                            OptionId.Delete -> confirmDeleteItemId = item.circle.id
                                            else -> Unit
                                        }
                                    },
                                )
                            }
                        }
                    }

                    item {
                        if (uiState.loading && !uiState.refreshing) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                ListLoadingIndicator()
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

                    item {
                        Spacer(modifier = Modifier.height(Spacing.xxxl))
                    }
                }
            }
        }

        if (uiState.operationInProgress) {
            ProgressHud()
        }

        if (confirmDeleteItemId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val itemId = confirmDeleteItemId
                    confirmDeleteItemId = null
                    if (confirm && itemId != null) {
                        model.reduce(CirclesMviModel.Intent.Delete(itemId))
                    }
                },
            )
        }

        val editorData = uiState.editorData
        if (editorData != null) {
            CircleEditorDialog(
                data = editorData,
                onDataChanged = { newData ->
                    model.reduce(CirclesMviModel.Intent.UpdateEditorData(newData))
                },
                onClose = { success ->
                    if (success) {
                        model.reduce(CirclesMviModel.Intent.SubmitEditorData)
                    } else {
                        model.reduce(CirclesMviModel.Intent.DismissEditor)
                    }
                },
            )
        }
    }
}

sealed interface CustomOptions : OptionId.Custom {
    data object EditMembers : CustomOptions
}
