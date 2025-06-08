package com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.EditTextualInfoDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.getFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AlbumImageItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AlbumDetailScreen(private val name: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model: AlbumDetailMviModel = rememberScreenModel(arg = name)
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val snackbarHostState = remember { SnackbarHostState() }
        val lazyGridState = rememberLazyStaggeredGridState()
        val fabNestedScrollConnection = remember { getFabNestedScrollConnection() }
        val detailOpener = remember { getDetailOpener() }
        val galleryHelper = remember { getGalleryHelper() }
        val isFabVisible by fabNestedScrollConnection.isFabVisible.collectAsState()
        val scope = rememberCoroutineScope()
        val genericError = LocalStrings.current.messageGenericError
        var openImagePicker by remember { mutableStateOf(false) }
        if (openImagePicker) {
            galleryHelper.getImageFromGallery { bytes ->
                openImagePicker = false
                if (bytes.isNotEmpty()) {
                    model.reduce(AlbumDetailMviModel.Intent.Create(bytes))
                }
            }
        }
        var attachmentIdToDelete by remember { mutableStateOf<String?>(null) }
        var attachmentWithDescriptionBeingEdited by remember { mutableStateOf<AttachmentModel?>(null) }
        var attachmentToMove by remember { mutableStateOf<AttachmentModel?>(null) }

        fun goBackToTop() {
            runCatching {
                scope.launch {
                    lazyGridState.scrollToItem(0)
                    topAppBarState.heightOffset = 0f
                    topAppBarState.contentOffset = 0f
                }
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        AlbumDetailMviModel.Effect.BackToTop -> goBackToTop()
                        AlbumDetailMviModel.Effect.Failure ->
                            snackbarHostState.showSnackbar(genericError)
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
                            text = name,
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
            floatingActionButton = {
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
                            openImagePicker = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = LocalStrings.current.actionAddNew,
                        )
                    }
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
                    model.reduce(AlbumDetailMviModel.Intent.Refresh)
                },
            ) {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.padding(horizontal = Spacing.s),
                    state = lazyGridState,
                    columns = StaggeredGridCells.Fixed(count = 2),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalItemSpacing = Spacing.s,
                ) {
                    if (uiState.initial) {
                        items(10) {
                            GenericPlaceholder(
                                height = 180.dp,
                                modifier = Modifier.clip(RoundedCornerShape(CornerSize.xxl)),
                            )
                        }
                    }

                    if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.items.isEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                                text = LocalStrings.current.messageEmptyAlbum,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }

                    itemsIndexed(
                        items = uiState.items,
                        key = { _, e -> "album-detail-${e.id}" },
                    ) { idx, attachment ->
                        AlbumImageItem(
                            attachment = attachment,
                            autoload = uiState.autoloadImages,
                            onClick = {
                                val url = attachment.thumbnail ?: attachment.url
                                if (url.isNotEmpty()) {
                                    detailOpener.openImageDetail(url)
                                }
                            },
                            options =
                            buildList {
                                this += OptionId.Delete.toOption()
                                this += OptionId.Edit.toOption()
                                if (uiState.albums.isNotEmpty()) {
                                    this +=
                                        CustomOptions.Move.toOption(
                                            label = LocalStrings.current.actionMove,
                                        )
                                }
                            },
                            onSelectOption = { optionId ->
                                when (optionId) {
                                    OptionId.Delete -> attachmentIdToDelete = attachment.id
                                    OptionId.Edit ->
                                        attachmentWithDescriptionBeingEdited = attachment

                                    CustomOptions.Move -> attachmentToMove = attachment

                                    else -> Unit
                                }
                            },
                        )

                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        val isNearTheEnd = idx.isNearTheEnd(uiState.items)
                        if (isNearTheEnd && canFetchMore) {
                            model.reduce(AlbumDetailMviModel.Intent.LoadNextPage)
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        if (uiState.loading && !uiState.refreshing && uiState.canFetchMore) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                ListLoadingIndicator()
                            }
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(modifier = Modifier.height(Spacing.xxxl))
                    }
                }
            }
        }

        if (attachmentWithDescriptionBeingEdited != null) {
            EditTextualInfoDialog(
                label = LocalStrings.current.pictureDescriptionPlaceholder,
                value = attachmentWithDescriptionBeingEdited?.description.orEmpty(),
                minLines = 3,
                onClose = { newValue ->
                    val attachment = attachmentWithDescriptionBeingEdited
                    if (attachment != null && newValue != null) {
                        model.reduce(
                            AlbumDetailMviModel.Intent.EditDescription(
                                attachment = attachment,
                                description = newValue,
                            ),
                        )
                    }
                    attachmentWithDescriptionBeingEdited = null
                },
            )
        }

        if (attachmentToMove != null) {
            val items = uiState.albums.map { CustomModalBottomSheetItem(label = it.name) }
            CustomModalBottomSheet(
                title = LocalStrings.current.actionMove,
                items = items,
                onSelect = { idx ->
                    val attachment = attachmentToMove
                    attachmentToMove = null
                    if (idx != null && attachment != null) {
                        val albumName = items[idx].label
                        model.reduce(
                            AlbumDetailMviModel.Intent.Move(
                                attachment = attachment,
                                album = albumName,
                            ),
                        )
                    }
                },
            )
        }

        if (attachmentIdToDelete != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val idToDelete = attachmentIdToDelete
                    attachmentIdToDelete = null
                    if (confirm && idToDelete != null) {
                        model.reduce(AlbumDetailMviModel.Intent.Delete(idToDelete))
                    }
                },
            )
        }
    }
}

private sealed interface CustomOptions : OptionId.Custom {
    data object Move : CustomOptions
}
