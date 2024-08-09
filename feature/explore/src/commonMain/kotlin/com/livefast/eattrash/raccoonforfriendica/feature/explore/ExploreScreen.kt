package com.livefast.eattrash.raccoonforfriendica.feature.explore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.HashtagItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.LinkItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.ExploreSection
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.toExploreSection
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.toInt
import com.livefast.eattrash.raccoonforfriendica.feature.explore.data.toReadableName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ExploreScreen : Screen {
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterialApi::class,
        ExperimentalFoundationApi::class,
    )
    @Composable
    override fun Content() {
        val model = getScreenModel<ExploreMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val connection = navigationCoordinator.getBottomBarScrollConnection()
        val uriHandler = LocalUriHandler.current
        val openUrl = remember { getOpenUrlUseCase(uriHandler) }
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        ExploreMviModel.Effect.BackToTop ->
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
                        Text(
                            text = LocalStrings.current.sectionTitleExplore,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                )
            },
        ) { padding ->
            val pullRefreshState =
                rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = {
                        model.reduce(ExploreMviModel.Intent.Refresh)
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
                    stickyHeader {
                        SectionSelector(
                            modifier = Modifier.padding(bottom = Spacing.s),
                            titles =
                                listOf(
                                    ExploreSection.Posts.toReadableName(),
                                    ExploreSection.Hashtags.toReadableName(),
                                    ExploreSection.Links.toReadableName(),
                                    ExploreSection.Suggestions.toReadableName(),
                                ),
                            currentSection = uiState.section.toInt(),
                            onSectionSelected = {
                                val section = it.toExploreSection()
                                model.reduce(
                                    ExploreMviModel.Intent.ChangeSection(section),
                                )
                            },
                        )
                    }

                    if (uiState.initial) {
                        items(20) {
                            val modifier = Modifier.fillMaxWidth()
                            when (uiState.section) {
                                ExploreSection.Hashtags -> {
                                    GenericPlaceholder(modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }

                                ExploreSection.Links -> {
                                    GenericPlaceholder(modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }

                                ExploreSection.Posts -> {
                                    TimelineItemPlaceholder(modifier)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = Spacing.s),
                                    )
                                }

                                ExploreSection.Suggestions -> {
                                    UserItemPlaceholder(modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }
                            }
                        }
                    }

                    items(
                        items = uiState.items,
                        key = { it.id },
                    ) { item ->
                        when (item) {
                            is ExploreItemModel.Entry -> {
                                TimelineItem(
                                    entry = item.entry,
                                    onClick = {
                                        detailOpener.openEntryDetail(item.entry.id)
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

                            is ExploreItemModel.HashTag -> {
                                HashtagItem(
                                    hashtag = item.hashtag,
                                    onOpen = {
                                        detailOpener.openHashtag(it)
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            is ExploreItemModel.Link -> {
                                LinkItem(
                                    link = item.link,
                                    onOpen = { url ->
                                        openUrl(url)
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            is ExploreItemModel.Suggestion -> {
                                UserItem(
                                    account = item.account,
                                    onClick = {
                                        detailOpener.openUserDetail(item.account.id)
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }
                        }
                    }

                    item {
                        if (!uiState.initial && !uiState.loading && uiState.canFetchMore) {
                            model.reduce(ExploreMviModel.Intent.LoadNextPage)
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
    }
}
