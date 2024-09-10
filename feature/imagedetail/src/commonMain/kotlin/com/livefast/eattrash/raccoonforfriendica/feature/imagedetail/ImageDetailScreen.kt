package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ZoomableImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.parameter.parametersOf

class ImageDetailScreen(
    private val urls: List<String>,
    private val initialIndex: Int = 0,
) : Screen {
    override val key: ScreenKey
        get() = super.key + urls.joinToString("-")

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val model =
            getScreenModel<ImageDetailMviModel>(
                parameters = {
                    parametersOf(
                        urls,
                        initialIndex,
                    )
                },
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
        LaunchedEffect(key) {
            drawerCoordinator.setGesturesEnabled(false)
        }
        DisposableEffect(key) {
            onDispose {
                drawerCoordinator.setGesturesEnabled(true)
            }
        }

        Scaffold(
            topBar = {
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
                                    contentDescription = null,
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
                                contentDescription = null,
                            )
                        }
                        IconButton(
                            onClick = {
                                shareModeBottomSheetOpened = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                            )
                        }
                        IconButton(
                            onClick = {
                                scaleModeBottomSheetOpened = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.AspectRatio,
                                contentDescription = null,
                            )
                        }
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        snackbarData = data,
                    )
                }
            },
            content =
                { padding ->
                    HorizontalPager(
                        state = pagerState,
                        beyondBoundsPageCount = 1,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .padding(
                                        top = padding.calculateTopPadding(),
                                    ).fillMaxWidth()
                                    .background(Color.Black),
                            contentAlignment = Alignment.Center,
                        ) {
                            val url = urls.getOrNull(uiState.currentIndex)
                            if (!url.isNullOrEmpty()) {
                                ZoomableImage(
                                    url = url,
                                    contentScale = uiState.contentScale,
                                )
                            }
                        }
                    }
                },
        )

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
                onSelected = { index ->
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
                onSelected = { index ->
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
