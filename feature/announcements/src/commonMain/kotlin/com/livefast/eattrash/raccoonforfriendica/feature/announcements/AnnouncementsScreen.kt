package com.livefast.eattrash.raccoonforfriendica.feature.announcements

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.InsertEmojiBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.components.AnnouncementCard
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AnnouncementsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model: AnnouncementsMviModel = rememberScreenModel()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val connection = navigationCoordinator.getBottomBarScrollConnection()
        val uriHandler = LocalUriHandler.current
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val lazyListState = rememberLazyListState()
        var chooseReactionAnnouncementIdBottomSheetOpened by remember { mutableStateOf<String?>(null) }

        suspend fun goBackToTop() {
            runCatching {
                lazyListState.scrollToItem(0)
                topAppBarState.heightOffset = 0f
                topAppBarState.contentOffset = 0f
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        AnnouncementsMviModel.Effect.BackToTop -> goBackToTop()
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
                            text = LocalStrings.current.announcementsTitle,
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
                    model.reduce(AnnouncementsMviModel.Intent.Refresh)
                },
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    if (uiState.initial) {
                        val placeholderCount = 5
                        items(placeholderCount) { idx ->
                            GenericPlaceholder(
                                modifier = Modifier.fillMaxWidth(),
                                height = 200.dp,
                            )
                            if (idx < placeholderCount - 1) {
                                TimelineDivider()
                            }
                        }
                    }

                    if (!uiState.initial && !uiState.refreshing && uiState.items.isEmpty()) {
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
                        key = { _, e -> "announcements-${e.id}" },
                    ) { idx, announcement ->
                        AnnouncementCard(
                            announcement = announcement,
                            autoloadImages = uiState.autoloadImages,
                            onOpenUrl = { url, allowOpenInternal ->
                                if (allowOpenInternal) {
                                    uriHandler.openUri(url)
                                } else {
                                    uriHandler.openExternally(url)
                                }
                            },
                            onAddNewReaction = {
                                chooseReactionAnnouncementIdBottomSheetOpened = announcement.id
                            },
                            onAddReaction = { name ->
                                model.reduce(
                                    AnnouncementsMviModel.Intent.AddReaction(
                                        id = announcement.id,
                                        name = name,
                                    ),
                                )
                            },
                            onRemoveReaction = { name ->
                                model.reduce(
                                    AnnouncementsMviModel.Intent.RemoveReaction(
                                        id = announcement.id,
                                        name = name,
                                    ),
                                )
                            },
                        )
                        if (idx < uiState.items.lastIndex) {
                            TimelineDivider()
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(Spacing.xxxl))
                    }
                }
            }
        }

        chooseReactionAnnouncementIdBottomSheetOpened?.also { id ->
            InsertEmojiBottomSheet(
                emojis = uiState.availableEmojis,
                withInsertCustom = true,
                onClose = {
                    chooseReactionAnnouncementIdBottomSheetOpened = null
                },
                onInsert = { emoji ->
                    chooseReactionAnnouncementIdBottomSheetOpened = null
                    model.reduce(
                        AnnouncementsMviModel.Intent.AddReaction(
                            id = id,
                            name = emoji.code,
                        ),
                    )
                },
                onInsertCustom = { name ->
                    chooseReactionAnnouncementIdBottomSheetOpened = null
                    model.reduce(
                        AnnouncementsMviModel.Intent.AddReaction(
                            id = id,
                            name = name,
                        ),
                    )
                },
            )
        }
    }
}
