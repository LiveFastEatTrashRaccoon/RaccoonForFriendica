package com.livefast.eattrash.raccoonforfriendica.feature.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase
import org.koin.core.parameter.parametersOf

class FavoritesScreen(
    private val type: FavoritesType,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<FavoritesMviModel>(parameters = { parametersOf(type) })
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val connection = navigationCoordinator.getBottomBarScrollConnection()
        val uriHandler = LocalUriHandler.current
        val openUrl = remember { getOpenUrlUseCase(uriHandler) }
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()

        Scaffold(
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = type.toReadableName(),
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
                        model.reduce(FavoritesMviModel.Intent.Refresh)
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
                            onReblog = {
                                model.reduce(FavoritesMviModel.Intent.ToggleReblog(entry.id))
                            },
                            onBookmark = {
                                model.reduce(FavoritesMviModel.Intent.ToggleBookmark(entry.id))
                            },
                            onFavorite = {
                                model.reduce(FavoritesMviModel.Intent.ToggleFavorite(entry.id))
                            },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )
                    }

                    item {
                        if (!uiState.initial && !uiState.loading && uiState.canFetchMore) {
                            model.reduce(FavoritesMviModel.Intent.LoadNextPage)
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
