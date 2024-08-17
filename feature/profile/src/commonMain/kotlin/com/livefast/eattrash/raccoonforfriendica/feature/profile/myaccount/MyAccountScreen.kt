package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserFields
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeaderPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.safeKey
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toAccountSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toInt
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MyAccountScreen : Screen {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<MyAccountMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val openUrl = remember { getOpenUrlUseCase(uriHandler) }
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()

        suspend fun goBackToTop() {
            runCatching {
                if (lazyListState.firstVisibleItemIndex > 0) {
                    if (uiState.entries.isEmpty()) {
                        lazyListState.scrollToItem(1)
                    } else {
                        lazyListState.scrollToItem(2)
                    }
                } else {
                    lazyListState.scrollToItem(0)
                }
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        MyAccountMviModel.Effect.BackToTop -> goBackToTop()
                    }
                }.launchIn(this)
        }
        LaunchedEffect(navigationCoordinator) {
            navigationCoordinator.onDoubleTabSelection
                .onEach { section ->
                    if (section == BottomNavigationSection.Profile) {
                        goBackToTop()
                    }
                }.launchIn(this)
        }

        val pullRefreshState =
            rememberPullRefreshState(
                refreshing = uiState.refreshing,
                onRefresh = {
                    model.reduce(MyAccountMviModel.Intent.Refresh)
                },
            )
        Box(
            modifier = Modifier.pullRefresh(pullRefreshState),
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                if (uiState.user != null) {
                    item {
                        UserHeader(
                            user = uiState.user,
                            onOpenUrl = { url ->
                                openUrl(url)
                            },
                            onOpenFollowers = {
                                uiState.user?.id?.also { userId ->
                                    detailOpener.openFollowers(userId)
                                }
                            },
                            onOpenFollowing = {
                                uiState.user?.id?.also { userId ->
                                    detailOpener.openFollowing(userId)
                                }
                            },
                        )
                    }
                } else {
                    item {
                        UserHeaderPlaceholder(modifier = Modifier.fillMaxWidth())
                    }
                }

                item {
                    UserFields(
                        modifier =
                            Modifier.padding(
                                top = Spacing.m,
                                bottom = Spacing.s,
                            ),
                        fields =
                            buildList {
                                uiState.user?.created?.also { date ->
                                    add(
                                        FieldModel(
                                            key = LocalStrings.current.accountAge,
                                            value = date.prettifyDate(),
                                        ),
                                    )
                                }
                                addAll(uiState.user?.fields.orEmpty())
                            },
                        onOpenUrl = { url ->
                            openUrl(url)
                        },
                    )
                }

                stickyHeader {
                    SectionSelector(
                        modifier = Modifier.padding(bottom = Spacing.s),
                        titles =
                            listOf(
                                UserSection.Posts.toReadableName(),
                                UserSection.All.toReadableName(),
                                UserSection.Pinned.toReadableName(),
                                UserSection.Media.toReadableName(),
                            ),
                        scrollable = true,
                        currentSection = uiState.section.toInt(),
                        onSectionSelected = {
                            val section = it.toAccountSection()
                            model.reduce(
                                MyAccountMviModel.Intent.ChangeSection(section),
                            )
                        },
                    )
                }

                if (uiState.initial) {
                    items(5) {
                        TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )
                    }
                }

                itemsIndexed(
                    items = uiState.entries,
                    key = { _, e -> e.safeKey },
                ) { idx, entry ->
                    TimelineItem(
                        entry = entry,
                        blurNsfw = uiState.blurNsfw,
                        onClick = { e ->
                            detailOpener.openEntryDetail(e.id)
                        },
                        onOpenUrl = { url ->
                            openUrl(url)
                        },
                        onOpenUser = {
                            detailOpener.openUserDetail(it.id)
                        },
                        onReblog = { e ->
                            model.reduce(MyAccountMviModel.Intent.ToggleReblog(e))
                        },
                        onBookmark = { e ->
                            model.reduce(MyAccountMviModel.Intent.ToggleBookmark(e))
                        },
                        onFavorite = { e ->
                            model.reduce(MyAccountMviModel.Intent.ToggleFavorite(e))
                        },
                        onReply = { e ->
                            detailOpener.openComposer(
                                inReplyToId = e.id,
                                inReplyToHandle = e.creator?.handle,
                                inReplyToUsername = e.creator?.let { it.displayName ?: it.username },
                            )
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.s),
                    )

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    if (idx == uiState.entries.lastIndex - 5 && canFetchMore) {
                        model.reduce(MyAccountMviModel.Intent.LoadNextPage)
                    }
                }
                if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.entries.isEmpty()) {
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
                    if (uiState.loading && !uiState.refreshing && uiState.canFetchMore) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            ListLoadingIndicator()
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
