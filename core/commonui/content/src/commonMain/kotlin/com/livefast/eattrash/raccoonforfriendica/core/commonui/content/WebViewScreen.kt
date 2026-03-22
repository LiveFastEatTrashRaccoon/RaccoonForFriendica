package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.rememberCoreResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.mohamedrejeb.calf.ui.web.WebView
import com.mohamedrejeb.calf.ui.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(url: String, modifier: Modifier = Modifier) {
    val navigationCoordinator = rememberNavigationCoordinator()
    val drawerCoordinator = rememberDrawerCoordinator()
    val coreResources = rememberCoreResources()
    val state = rememberWebViewState(url = url)

    LaunchedEffect(Unit) {
        state.settings.javaScriptEnabled = true
        state.settings.androidSettings.supportZoom = true
    }

    DisposableEffect(drawerCoordinator) {
        drawerCoordinator.setGesturesEnabled(false)
        onDispose {
            drawerCoordinator.setGesturesEnabled(true)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text(
                        text = url,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    if (navigationCoordinator.canPop.value && isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
                        IconButton(
                            onClick = {
                                navigationCoordinator.pop()
                            },
                        ) {
                            Icon(
                                imageVector = coreResources.arrowBack,
                                contentDescription = LocalStrings.current.actionGoBack,
                            )
                        }
                    }
                },
            )
            Box(modifier = Modifier.weight(1f)) {
                WebView(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                )
            }
        }
    }
}
