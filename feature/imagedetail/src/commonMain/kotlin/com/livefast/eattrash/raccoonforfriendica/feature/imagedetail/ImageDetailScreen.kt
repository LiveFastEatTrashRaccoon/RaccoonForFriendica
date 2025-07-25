package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.VideoPlayer
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ZoomableImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di.ImageDetailViewModelArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    urls: List<String>,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    videoIndices: List<Int> = emptyList(),
) {
    val model: ImageDetailMviModel =
        getViewModel<ImageDetailViewModel>(
            arg =
            ImageDetailViewModelArgs(
                urls = urls,
                initialIndex = initialIndex,
            ),
        )
    val uiState by model.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = LocalStrings.current.messageSuccess
    val errorMessage = LocalStrings.current.messageGenericError
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val drawerCoordinator = remember { getDrawerCoordinator() }
    val pagerState =
        rememberPagerState(
            initialPage = initialIndex,
            pageCount = { urls.size },
        )
    var shareModeBottomSheetOpened by remember { mutableStateOf(false) }
    var scaleModeBottomSheetOpened by remember { mutableStateOf(false) }
    val isVideo = videoIndices.contains(uiState.currentIndex)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .onEach {
                model.reduce(ImageDetailMviModel.Intent.ChangeIndex(it))
            }.launchIn(this)
    }

    LaunchedEffect(model) {
        model.effects
            .onEach {
                when (it) {
                    ImageDetailMviModel.Effect.ShareSuccess ->
                        snackbarHostState.showSnackbar(
                            successMessage,
                        )

                    ImageDetailMviModel.Effect.ShareFailure ->
                        snackbarHostState.showSnackbar(
                            errorMessage,
                        )
                }
            }.launchIn(this)
    }
    DisposableEffect(drawerCoordinator) {
        drawerCoordinator.setGesturesEnabled(false)
        onDispose {
            drawerCoordinator.setGesturesEnabled(true)
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar =
        {
            TopAppBar(
                title = {
                    Text(
                        text =
                        buildString {
                            if (urls.size > 1) {
                                append(uiState.currentIndex + 1)
                                append("/")
                                append(urls.size)
                            }
                        },
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
                    IconButton(
                        onClick = {
                            model.reduce(ImageDetailMviModel.Intent.SaveToGallery)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = LocalStrings.current.actionDownload,
                        )
                    }
                    IconButton(
                        onClick = {
                            shareModeBottomSheetOpened = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = LocalStrings.current.actionShare,
                        )
                    }
                    if (!isVideo) {
                        IconButton(
                            onClick = {
                                scaleModeBottomSheetOpened = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.AspectRatio,
                                contentDescription = LocalStrings.current.contentScaleTitle,
                            )
                        }
                    }
                },
            )
        },
        snackbarHost =
        {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    snackbarData = data,
                )
            }
        },
    ) { padding ->
        Box(
            modifier =
            Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
                .background(Color.Black),
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                beyondViewportPageCount = 1,
            ) { index ->
                val url = urls.getOrNull(index)
                Box {
                    if (!url.isNullOrEmpty()) {
                        if (isVideo) {
                            VideoPlayer(
                                url = url,
                                muted = false,
                            )
                        } else {
                            ZoomableImage(
                                url = url,
                                contentScale = uiState.contentScale,
                            )
                        }
                    }
                }
            }
        }

        if (uiState.loading) {
            ProgressHud()
        }

        if (scaleModeBottomSheetOpened) {
            CustomModalBottomSheet(
                title = LocalStrings.current.contentScaleTitle,
                items =
                listOf(
                    CustomModalBottomSheetItem(
                        label = LocalStrings.current.contentScaleFit,
                    ),
                    CustomModalBottomSheetItem(
                        label = LocalStrings.current.contentScaleFillWidth,
                    ),
                    CustomModalBottomSheetItem(
                        label = LocalStrings.current.contentScaleFillHeight,
                    ),
                ),
                onSelect = { index ->
                    scaleModeBottomSheetOpened = false
                    if (index != null) {
                        model.reduce(
                            ImageDetailMviModel.Intent.ChangeContentScale(
                                when (index) {
                                    1 -> ContentScale.FillWidth
                                    2 -> ContentScale.FillHeight
                                    else -> ContentScale.Fit
                                },
                            ),
                        )
                    }
                },
            )
        }

        if (shareModeBottomSheetOpened) {
            CustomModalBottomSheet(
                title = LocalStrings.current.actionShare,
                items =
                listOf(
                    CustomModalBottomSheetItem(
                        label = LocalStrings.current.shareAsUrl,
                    ),
                    CustomModalBottomSheetItem(
                        label = LocalStrings.current.shareAsFile,
                    ),
                ),
                onSelect = { index ->
                    shareModeBottomSheetOpened = false
                    if (index != null) {
                        model.reduce(
                            when (index) {
                                1 -> ImageDetailMviModel.Intent.ShareAsFile
                                else -> ImageDetailMviModel.Intent.ShareAsUrl
                            },
                        )
                    }
                },
            )
        }
    }
}
