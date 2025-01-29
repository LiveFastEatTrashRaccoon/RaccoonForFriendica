package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.EditTextualInfoDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericListItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.ManageBlocksItem
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.ManageBlocksSection
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data.toReadableName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ManageBlocksScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val model: ManageBlocksMviModel = rememberScreenModel()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        var confirmUnmuteUserId by remember { mutableStateOf<String?>(null) }
        var confirmUnblockUserId by remember { mutableStateOf<String?>(null) }
        var changeRateLimitUser by remember { mutableStateOf<UserModel?>(null) }
        var addStopWordDialogOpen by remember { mutableStateOf(false) }
        var confirmDeleteStopWord by remember { mutableStateOf<String?>(null) }

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
                        ManageBlocksMviModel.Effect.BackToTop -> goBackToTop()
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
                            text = LocalStrings.current.settingsItemBlockedAndMuted,
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
                    actions = {
                        if (uiState.section == ManageBlocksSection.StopWords) {
                            val options =
                                buildList {
                                    this += OptionId.Add.toOption()
                                }
                            Box {
                                var optionsOffset by remember { mutableStateOf(Offset.Zero) }
                                var optionsMenuOpen by remember { mutableStateOf(false) }
                                IconButton(
                                    modifier =
                                        Modifier.onGloballyPositioned {
                                            optionsOffset = it.positionInParent()
                                        },
                                    onClick = {
                                        optionsMenuOpen = true
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = LocalStrings.current.actionOpenOptions,
                                    )
                                }

                                CustomDropDown(
                                    expanded = optionsMenuOpen,
                                    onDismiss = {
                                        optionsMenuOpen = false
                                    },
                                    offset =
                                        with(LocalDensity.current) {
                                            DpOffset(
                                                x = optionsOffset.x.toDp(),
                                                y = optionsOffset.y.toDp(),
                                            )
                                        },
                                ) {
                                    for (option in options) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(option.label)
                                            },
                                            onClick = {
                                                optionsMenuOpen = false
                                                when (option.id) {
                                                    OptionId.Add -> addStopWordDialogOpen = true
                                                    else -> Unit
                                                }
                                            },
                                        )
                                    }
                                }
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
                            if (uiState.hideNavigationBarWhileScrolling) {
                                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            } else {
                                Modifier
                            },
                        ),
                isRefreshing = uiState.refreshing,
                onRefresh = {
                    model.reduce(ManageBlocksMviModel.Intent.Refresh)
                },
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    stickyHeader {
                        val sections =
                            listOf(
                                ManageBlocksSection.Muted,
                                ManageBlocksSection.Blocked,
                                ManageBlocksSection.Limited,
                                ManageBlocksSection.StopWords,
                            )
                        SectionSelector(
                            modifier =
                                Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(
                                        top = Dimensions.maxTopBarInset * topAppBarState.collapsedFraction,
                                        bottom = Spacing.s,
                                    ),
                            titles = sections.map { it.toReadableName() },
                            currentSection = sections.indexOf(uiState.section),
                            onSectionSelected = {
                                model.reduce(
                                    ManageBlocksMviModel.Intent.ChangeSection(sections[it]),
                                )
                            },
                        )
                    }

                    if (uiState.initial) {
                        items(20) {
                            UserItemPlaceholder(
                                modifier = Modifier.fillMaxWidth(),
                                withRelationshipButton = false,
                            )
                            Spacer(modifier = Modifier.height(Spacing.interItem))
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

                    itemsIndexed(
                        items = uiState.items,
                        key = { _, e -> "manage-blocks-${e.safeKey}" },
                    ) { idx, item ->
                        when (item) {
                            is ManageBlocksItem.StopWord ->
                                GenericListItem(
                                    title = item.word,
                                    options =
                                        buildList {
                                            this += OptionId.Delete.toOption()
                                        },
                                    onOptionSelected = { optionId ->
                                        when (optionId) {
                                            OptionId.Delete -> confirmDeleteStopWord = item.word
                                            else -> Unit
                                        }
                                    },
                                )

                            is ManageBlocksItem.User ->
                                UserItem(
                                    user = item.user,
                                    autoloadImages = uiState.autoloadImages,
                                    withSubtitle = uiState.section != ManageBlocksSection.Limited,
                                    onClick = {
                                        detailOpener.openUserDetail(item.user)
                                    },
                                    options =
                                        buildList {
                                            when (uiState.section) {
                                                ManageBlocksSection.Muted -> {
                                                    this += OptionId.Unmute.toOption()
                                                }

                                                ManageBlocksSection.Blocked -> {
                                                    this += OptionId.Unblock.toOption()
                                                }

                                                ManageBlocksSection.Limited -> {
                                                    this += OptionId.Edit.toOption()
                                                }

                                                else -> Unit
                                            }
                                        },
                                    onOptionSelected = { optionId ->
                                        when (optionId) {
                                            OptionId.Unmute -> confirmUnmuteUserId = item.user.id
                                            OptionId.Unblock -> confirmUnblockUserId = item.user.id
                                            OptionId.Edit -> changeRateLimitUser = item.user
                                            else -> Unit
                                        }
                                    },
                                )
                        }

                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        val isNearTheEnd = idx.isNearTheEnd(uiState.items)
                        if (isNearTheEnd && canFetchMore) {
                            model.reduce(ManageBlocksMviModel.Intent.LoadNextPage)
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
            }
        }

        if (confirmUnmuteUserId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionUnmute,
                onClose = { confirm ->
                    val userId = confirmUnmuteUserId
                    confirmUnmuteUserId = null
                    if (confirm && userId != null) {
                        model.reduce(ManageBlocksMviModel.Intent.ToggleMute(userId))
                    }
                },
            )
        }

        if (confirmUnblockUserId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionUnblock,
                onClose = { confirm ->
                    val userId = confirmUnblockUserId
                    confirmUnblockUserId = null
                    if (confirm && userId != null) {
                        model.reduce(ManageBlocksMviModel.Intent.ToggleBlock(userId))
                    }
                },
            )
        }

        if (changeRateLimitUser != null) {
            val availableRates =
                listOf(
                    0.1,
                    0.25,
                    0.5,
                    1.0,
                )
            CustomModalBottomSheet(
                title = LocalStrings.current.actionChangeRateLimit,
                items =
                    availableRates.map { rate ->
                        CustomModalBottomSheetItem(
                            label =
                                if (rate < 1.0) {
                                    "${rate * 100} %"
                                } else {
                                    LocalStrings.current.settingsOptionUnlimited
                                },
                            trailingContent = {
                                val selected = rate == changeRateLimitUser?.username?.toDoubleOrNull()
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.RadioButtonChecked,
                                        contentDescription = LocalStrings.current.itemSelected,
                                    )
                                }
                            },
                        )
                    },
                onSelected = { index ->
                    val user = changeRateLimitUser
                    changeRateLimitUser = null
                    if (user != null && index != null) {
                        val newRate = availableRates[index]
                        model.reduce(
                            ManageBlocksMviModel.Intent.SetRateLimit(
                                handle = user.handle.orEmpty(),
                                rate = newRate,
                            ),
                        )
                    }
                },
            )
        }

        if (addStopWordDialogOpen) {
            EditTextualInfoDialog(
                title = LocalStrings.current.actionAddNew,
                label = LocalStrings.current.manageBlocksSectionStopWords,
                value = "",
                singleLine = true,
                onClose = { newValue ->
                    if (newValue != null) {
                        model.reduce(
                            ManageBlocksMviModel.Intent.AddStopWord(newValue),
                        )
                    }
                    addStopWordDialogOpen = false
                },
            )
        }

        if (confirmDeleteStopWord != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val word = confirmDeleteStopWord
                    confirmDeleteStopWord = null
                    if (confirm && word != null) {
                        model.reduce(ManageBlocksMviModel.Intent.RemoveStopWord(word))
                    }
                },
            )
        }
    }
}
