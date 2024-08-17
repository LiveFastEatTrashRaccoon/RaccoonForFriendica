package com.livefast.eattrash.raccoonforfriendica.feature.thread

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.thread.composable.TimelineReplyItem
import kotlinx.coroutines.launch
import org.koin.core.parameter.parameterArrayOf

class ThreadScreen(
    private val entryId: String,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<ThreadMviModel>(parameters = { parameterArrayOf(entryId) })
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val openUrl = remember { getOpenUrlUseCase(uriHandler) }
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
        val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()

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
                            text = LocalStrings.current.threadTitle,
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
                if (uiState.isLogged) {
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
                                val entry = uiState.entry
                                if (entry != null) {
                                    detailOpener.openComposer(
                                        inReplyToId = entry.id,
                                        inReplyToUsername = entry.creator?.username,
                                        inReplyToHandle = entry.creator?.handle,
                                    )
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Reply,
                                contentDescription = null,
                            )
                        }
                    }
                }
            },
            content = { padding ->
                val pullRefreshState =
                    rememberPullRefreshState(
                        refreshing = uiState.refreshing,
                        onRefresh = {
                            model.reduce(ThreadMviModel.Intent.Refresh)
                        },
                    )
                Box(
                    modifier =
                        Modifier
                            .padding(padding)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .nestedScroll(fabNestedScrollConnection)
                            .pullRefresh(pullRefreshState),
                ) {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        if (uiState.initial) {
                            items(5) {
                                TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.s),
                                )
                            }
                        }

                        // original entry
                        uiState.entry?.also { original ->
                            item {
                                TimelineItem(
                                    modifier =
                                        Modifier
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                shape = RoundedCornerShape(CornerSize.l),
                                            ).padding(
                                                vertical = Spacing.s,
                                                horizontal = Spacing.xxxs,
                                            ),
                                    entry = original,
                                    reshareAndReplyVisible = false,
                                    blurNsfw = uiState.blurNsfw,
                                    onOpenUrl = { url ->
                                        openUrl(url)
                                    },
                                    onOpenUser = {
                                        detailOpener.openUserDetail(it.id)
                                    },
                                    onReblog = { e ->
                                        model.reduce(ThreadMviModel.Intent.ToggleReblog(e))
                                    },
                                    onBookmark = { e ->
                                        model.reduce(ThreadMviModel.Intent.ToggleBookmark(e))
                                    },
                                    onFavorite = { e ->
                                        model.reduce(ThreadMviModel.Intent.ToggleFavorite(e))
                                    },
                                    onOpenUsersFavorite = { e ->
                                        detailOpener.openEntryUsersFavorite(
                                            entryId = e.id,
                                            count = e.favoriteCount,
                                        )
                                    },
                                    onOpenUsersReblog = { e ->
                                        detailOpener.openEntryUsersReblog(
                                            entryId = e.id,
                                            count = e.reblogCount,
                                        )
                                    },
                                    onReply = { e ->
                                        detailOpener.openComposer(
                                            inReplyToId = e.id,
                                            inReplyToHandle = e.creator?.handle,
                                            inReplyToUsername =
                                                e.creator?.let {
                                                    it.displayName ?: it.username
                                                },
                                        )
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.s))
                            }
                        }

                        // replies
                        items(
                            items = uiState.replies,
                            key = { it.safeKey },
                        ) { entry ->
                            TimelineReplyItem(
                                entry = entry,
                                onOpenUrl = { url ->
                                    openUrl(url)
                                },
                                onOpenUser = {
                                    detailOpener.openUserDetail(it.id)
                                },
                                onReblog = { e ->
                                    model.reduce(ThreadMviModel.Intent.ToggleReblog(e))
                                },
                                onBookmark = { e ->
                                    model.reduce(ThreadMviModel.Intent.ToggleBookmark(e))
                                },
                                onFavorite = { e ->
                                    model.reduce(ThreadMviModel.Intent.ToggleFavorite(e))
                                },
                                onReply = { e ->
                                    detailOpener.openComposer(
                                        inReplyToId = e.id,
                                        inReplyToHandle = e.creator?.handle,
                                        inReplyToUsername =
                                            e.creator?.let {
                                                it.displayName ?: it.username
                                            },
                                    )
                                },
                            )

                            // load more button
                            if (entry.loadMoreButtonVisible) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Button(
                                        onClick = {
                                            model.reduce(
                                                ThreadMviModel.Intent.LoadMoreReplies(entry),
                                            )
                                        },
                                    ) {
                                        Text(
                                            text =
                                                buildString {
                                                    append(LocalStrings.current.buttonLoadMoreReplies)
                                                    entry.replyCount
                                                        .takeIf { it > 0 }
                                                        ?.also { count ->
                                                            append(" (")
                                                            append(count)
                                                            append(")")
                                                        }
                                                },
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.s),
                            )
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
            },
        )
    }
}
