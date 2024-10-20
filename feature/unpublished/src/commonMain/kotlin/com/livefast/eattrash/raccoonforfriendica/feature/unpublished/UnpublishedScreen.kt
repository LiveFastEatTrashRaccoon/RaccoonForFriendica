package com.livefast.eattrash.raccoonforfriendica.feature.unpublished

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toUnpublishedType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UnpublishedScreen : Screen {
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterialApi::class,
        ExperimentalFoundationApi::class,
    )
    @Composable
    override fun Content() {
        val model = getScreenModel<UnpublishedMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }

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
                        UnpublishedMviModel.Effect.BackToTop -> goBackToTop()
                    }
                }.launchIn(this)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.unpublishedTitle,
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
                                    contentDescription = null,
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
            val pullRefreshState =
                rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = {
                        model.reduce(UnpublishedMviModel.Intent.Refresh)
                    },
                )
            Box(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .pullRefresh(pullRefreshState),
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    stickyHeader {
                        SectionSelector(
                            modifier =
                                Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(
                                        top = Dimensions.maxTopBarInset * topAppBarState.collapsedFraction,
                                        bottom = Spacing.s,
                                    ),
                            titles =
                                listOf(
                                    UnpublishedType.Scheduled.toReadableName(),
                                    UnpublishedType.Drafts.toReadableName(),
                                ),
                            currentSection = uiState.section.toInt(),
                            onSectionSelected = {
                                val section = it.toUnpublishedType()
                                model.reduce(
                                    UnpublishedMviModel.Intent.ChangeSection(section),
                                )
                            },
                        )
                    }

                    if (uiState.initial) {
                        val placeholderCount = 20
                        items(placeholderCount) { idx ->
                            val modifier = Modifier.fillMaxWidth()
                            TimelineItemPlaceholder(modifier = modifier)
                            if (idx < placeholderCount - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.s),
                                )
                            }
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

                    itemsIndexed(
                        items = uiState.entries,
                        key = { _, e -> e.safeKey },
                    ) { idx, entry ->
                        TimelineItem(
                            entry = entry,
                            blurNsfw = uiState.blurNsfw,
                            maxBodyLines = uiState.maxBodyLines,
                            actionsEnabled = false,
                            onToggleSpoilerActive = { e ->
                                model.reduce(UnpublishedMviModel.Intent.ToggleSpoilerActive(e))
                            },
                            options =
                                buildList {
                                    this += OptionId.Edit.toOption()
                                    this += OptionId.Delete.toOption()
                                },
                            onOptionSelected = { optionId ->
                                when (optionId) {
                                    OptionId.Edit ->
                                        detailOpener.openEditUnpublished(
                                            entry = entry,
                                            type = uiState.section,
                                        )

                                    OptionId.Delete -> confirmDeleteEntryId = entry.id
                                    else -> Unit
                                }
                            },
                        )
                        if (idx < uiState.entries.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.s),
                            )
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = uiState.refreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
        }

        if (confirmDeleteEntryId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val entryId = confirmDeleteEntryId
                    confirmDeleteEntryId = null
                    if (confirm && entryId != null) {
                        model.reduce(UnpublishedMviModel.Intent.DeleteEntry(entryId))
                    }
                },
            )
        }
    }
}
