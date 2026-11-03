package com.livefast.eattrash.raccoonforfriendica.feature.settings.timelinetypes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.LocalUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassEqualOrAbove
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ConfigureTimelineTypesScreen(
    modifier: Modifier = Modifier,
    model: ConfigureTimelineTypesMvi = metroViewModel<ConfigureTimelineTypesViewModel>(),
) {
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    val canPopState by navigationCoordinator.canPop.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val lazyListState = rememberLazyListState()
    val fabNestedScrollConnection = LocalUiDeps.current.fabNestedScrollConnection
    val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        model.reduce(ConfigureTimelineTypesMvi.Intent.Reorder(fromIndex = from.index, toIndex = to.index))
    }
    var timelineTypeToDelete by remember { mutableStateOf<TimelineType?>(null) }
    var addFeedOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        LocalStrings.current.configureTimelineTypesTitle,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    if (canPopState) {
                        IconButton(
                            onClick = {
                                navigationCoordinator.pop()
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.arrowBack,
                                contentDescription = LocalStrings.current.actionGoBack,
                            )
                        }
                    }
                },
                actions = {
                    if (isWidthSizeClassEqualOrAbove(WindowWidthSizeClass.Expanded)) {
                        IconButton(
                            shape = MaterialTheme.shapes.small,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(),
                            onClick = {
                                addFeedOpen = true
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.add,
                                contentDescription = LocalStrings.current.actionAddNew,
                            )
                        }
                    }
                    if (!uiState.reorderMode) {
                        IconButton(
                            onClick = {
                                model.reduce(ConfigureTimelineTypesMvi.Intent.EnterReorderMode)
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.sort,
                                contentDescription = LocalStrings.current.actionEnterReorderMode,
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                model.reduce(ConfigureTimelineTypesMvi.Intent.ExitReorderMode)
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.check,
                                contentDescription = LocalStrings.current.actionSave,
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
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
                            addFeedOpen = true
                        },
                    ) {
                        Icon(
                            imageVector = LocalResources.current.add,
                            contentDescription = LocalStrings.current.actionAddNew,
                        )
                    }
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
            ).fillMaxSize().padding(horizontal = Spacing.s),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            state = lazyListState,
        ) {
            items(
                items = uiState.types,
                key = { type -> type.listKey },
            ) { type ->
                ReorderableItem(reorderableLazyListState, key = type.listKey) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                    Surface(shadowElevation = elevation) {
                        TimelineTypeItem(
                            type = type,
                            reorderEnabled = uiState.reorderMode,
                            reorderableScope = this@ReorderableItem,
                            options = buildList {
                                if (type.canBeDeleted && !uiState.reorderMode) {
                                    this += OptionId.Delete.toOption()
                                }
                            },
                            onSelectOption = { optionId ->
                                when (optionId) {
                                    OptionId.Delete -> {
                                        timelineTypeToDelete = type
                                    }

                                    else -> Unit
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    if (timelineTypeToDelete != null) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionDelete,
            onClose = { confirm ->
                val type = timelineTypeToDelete
                timelineTypeToDelete = null
                if (confirm && type != null) {
                    model.reduce(ConfigureTimelineTypesMvi.Intent.Delete(type))
                }
            },
        )
    }

    if (addFeedOpen) {
        val viewModelStoreOwner = rememberViewModelStoreOwner()
        val idsToExclude = uiState.types.filterIsInstance<TimelineType.Circle>().mapNotNull { it.circle?.id }
        val dialogModel: AddTimelineTypeMvi = assistedMetroViewModel<AddTimelineTypeViewModel>(
            viewModelStoreOwner = viewModelStoreOwner,
            extras = AddTimelineTypeViewModel.getExtras(idsToExclude),
        )
        val dialogUiState by dialogModel.uiState.collectAsState()
        AddTimelineTypeDialog(
            query = dialogUiState.query,
            loading = dialogUiState.loading,
            circles = dialogUiState.circles,
            onSearch = { query ->
                dialogModel.reduce(AddTimelineTypeMvi.Intent.SetQuery(query))
            },
            onClose = { result ->
                result?.forEach {
                    model.reduce(ConfigureTimelineTypesMvi.Intent.Add(it))
                }
                addFeedOpen = false
            },
        )
    }
}

private val TimelineType.canBeDeleted: Boolean
    get() = when (this) {
        is TimelineType.Circle -> true
        else -> false
    }

private val TimelineType.listKey: String
    get() = when (this) {
        TimelineType.All -> "type-all"

        TimelineType.Local -> "type-local"

        TimelineType.Subscriptions -> "type-subscriptions"

        is TimelineType.Circle -> {
            val type = circle?.type ?: CircleType.Other
            when (type) {
                CircleType.UserDefined -> "type-circle-${circle?.id ?: "unknown"}"
                CircleType.Group -> "type-group-${circle?.name.orEmpty()}"
                CircleType.Predefined -> "type-predefined-${circle?.name.orEmpty()}"
                else -> "type-unknown"
            }
        }

        is TimelineType.Foreign -> "type-foreign-$otherInstance"
    }
