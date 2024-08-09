package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AccountFields
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AccountHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AccountSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toAccountSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toInt
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getUrlManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel

class MyAccountScreen : Screen {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<MyAccountMviModel>()
        val uiState by model.uiState.collectAsState()
        val uriHandler = LocalUriHandler.current
        val urlManager = remember { getUrlManager(uriHandler) }
        val detailOpener = remember { getDetailOpener() }

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
            LazyColumn {
                if (uiState.account != null) {
                    item {
                        AccountHeader(
                            account = uiState.account,
                            onOpenUrl = { url ->
                                urlManager.open(url)
                            },
                        )
                    }
                }
                item {
                    AccountFields(
                        modifier =
                            Modifier.padding(
                                top = Spacing.m,
                                bottom = Spacing.s,
                            ),
                        fields =
                            buildList {
                                uiState.account?.created?.also { date ->
                                    add(
                                        FieldModel(
                                            key = LocalStrings.current.accountAge,
                                            value = date.prettifyDate(),
                                        ),
                                    )
                                }
                                addAll(uiState.account?.fields.orEmpty())
                            },
                        onOpenUrl = { url ->
                            urlManager.open(url)
                        },
                    )
                }
                if (uiState.account != null) {
                    stickyHeader {
                        SectionSelector(
                            modifier = Modifier.padding(bottom = Spacing.s),
                            titles =
                                listOf(
                                    AccountSection.Posts.toReadableName(),
                                    AccountSection.All.toReadableName(),
                                    AccountSection.Pinned.toReadableName(),
                                    AccountSection.Media.toReadableName(),
                                ),
                            currentSection = uiState.section.toInt(),
                            onSectionSelected = {
                                val section = it.toAccountSection()
                                model.reduce(
                                    MyAccountMviModel.Intent.ChangeSection(section),
                                )
                            },
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
                            urlManager.open(url)
                        },
                        onOpenUser = {
                            detailOpener.openAccountDetail(it.id)
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.s),
                    )
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
                    if (!uiState.initial && !uiState.loading && uiState.canFetchMore) {
                        model.reduce(MyAccountMviModel.Intent.LoadNextPage)
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
