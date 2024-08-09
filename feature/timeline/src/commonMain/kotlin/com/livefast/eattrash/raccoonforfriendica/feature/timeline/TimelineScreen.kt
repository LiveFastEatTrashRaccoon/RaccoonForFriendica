package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.composable.TimelineTypeBottomSheet
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TimelineScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<TimelineMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val connection = navigationCoordinator.getBottomBarScrollConnection()
        val uriHandler = LocalUriHandler.current
        val openUrl = remember { getOpenUrlUseCase(uriHandler) }
        val detailOpener = remember { getDetailOpener() }
        var timelineTypeSelectorOpen by remember { mutableStateOf(false) }
        val lazyListState = rememberLazyListState()

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        TimelineMviModel.Effect.BackToTop ->
                            runCatching {
                                lazyListState.scrollToItem(0)
                                topAppBarState.heightOffset = 0f
                                topAppBarState.contentOffset = 0f
                            }
                    }
                }.launchIn(this)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Column(
                            modifier =
                                Modifier
                                    .clickable {
                                        if (uiState.availableTimelineTypes.isNotEmpty()) {
                                            timelineTypeSelectorOpen = true
                                        }
                                    }.fillMaxWidth(),
                        ) {
                            Text(
                                text = uiState.timelineType.toReadableName(),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text =
                                    buildString {
                                        append(LocalStrings.current.nodeVia)
                                        append(" ")
                                        append(uiState.nodeName)
                                    },
                                style = MaterialTheme.typography.labelMedium,
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
                        model.reduce(TimelineMviModel.Intent.Refresh)
                    },
                )
            Box(
                modifier =
                    Modifier
                        .padding(padding)
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
                            TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.s),
                            )
                        }
                    }

                    items(
                        items = uiState.entries,
                        key = { it.id },
                    ) { entry ->
                        TimelineItem(
                            entry = entry,
                            onClick = {
                                detailOpener.openEntryDetail(entry.id)
                            },
                            onOpenUrl = { url ->
                                openUrl(url)
                            },
                            onOpenUser = {
                                detailOpener.openUserDetail(it.id)
                            },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )
                    }

                    item {
                        if (!uiState.initial && !uiState.loading && uiState.canFetchMore) {
                            model.reduce(TimelineMviModel.Intent.LoadNextPage)
                        }
                        if (uiState.loading) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
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

        if (timelineTypeSelectorOpen) {
            ModalBottomSheet(
                onDismissRequest = {
                    timelineTypeSelectorOpen = false
                },
                content = {
                    TimelineTypeBottomSheet(
                        types = uiState.availableTimelineTypes,
                        onSelected = { type ->
                            timelineTypeSelectorOpen = false
                            model.reduce(TimelineMviModel.Intent.ChangeType(type))
                        },
                    )
                },
            )
        }
    }
}
