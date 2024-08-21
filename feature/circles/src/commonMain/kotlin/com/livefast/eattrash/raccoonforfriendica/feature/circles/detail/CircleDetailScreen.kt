package com.livefast.eattrash.raccoonforfriendica.feature.circles.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

class CircleDetailScreen(
    val id: String,
) : Screen {
    override val key: ScreenKey
        get() = super.key + id

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<CircleDetailMviModel>(parameters = { parametersOf(id) })
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val lazyListState = rememberLazyListState()
        val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
        val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
        val scope = rememberCoroutineScope()
        var confirmDeleteUserId by remember { mutableStateOf<String?>(null) }

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
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = uiState.circle?.name.orEmpty(),
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
                            // open dialog to add user (coming soon)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    }
                }
            },
        ) { padding ->
            val pullRefreshState =
                rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = {
                        model.reduce(CircleDetailMviModel.Intent.Refresh)
                    },
                )
            Box(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .nestedScroll(fabNestedScrollConnection)
                        .pullRefresh(pullRefreshState),
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    if (uiState.initial) {
                        items(20) {
                            UserItemPlaceholder(
                                modifier = Modifier.fillMaxWidth(),
                                withRelationshipButton = false,
                            )
                            Spacer(modifier = Modifier.height(Spacing.interItem))
                        }
                    }

                    itemsIndexed(
                        items = uiState.users,
                        key = { _, e -> e.id },
                    ) { idx, user ->
                        UserItem(
                            user = user,
                            options =
                                buildList {
                                    this += OptionId.Delete.toOption()
                                },
                            onOptionSelected = { optionId ->
                                when (optionId) {
                                    OptionId.Delete -> confirmDeleteUserId = user.id
                                    else -> Unit
                                }
                            },
                        )
                        Spacer(modifier = Modifier.height(Spacing.interItem))

                        if (idx == uiState.users.lastIndex - 5 && uiState.canFetchMore) {
                            model.reduce(CircleDetailMviModel.Intent.LoadNextPage)
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

        if (confirmDeleteUserId != null) {
            AlertDialog(
                onDismissRequest = {
                    confirmDeleteUserId = null
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
                            confirmDeleteUserId = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val userId = confirmDeleteUserId ?: ""
                            confirmDeleteUserId = null
                            if (userId.isNotEmpty()) {
                                // remove user (coming soon)
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
