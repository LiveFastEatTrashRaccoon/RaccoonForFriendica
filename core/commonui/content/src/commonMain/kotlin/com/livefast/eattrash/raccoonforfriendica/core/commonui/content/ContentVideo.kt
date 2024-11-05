package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.VideoPlayer
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ContentVideo(
    url: String,
    modifier: Modifier = Modifier,
    sensitive: Boolean = false,
    autoload: Boolean = true,
    originalWidth: Int = 0,
    originalHeight: Int = 0,
    onClick: (() -> Unit)? = null,
) {
    var revealing by remember { mutableStateOf(!sensitive && autoload) }
    var initialLoadDelay by remember { mutableStateOf(true) }
    val ratio =
        if (originalWidth != 0 && originalHeight != 0) {
            originalWidth / originalHeight.toFloat()
        } else {
            16 / 9f
        }
    val density = LocalDensity.current
    var availableWidth by remember { mutableStateOf(0.dp) }
    var videoSize by remember(availableWidth) {
        mutableStateOf(
            DpSize(
                width = availableWidth,
                height = (availableWidth.value / ratio).roundToInt().dp,
            ),
        )
    }

    LaunchedEffect(Unit) {
        // this delay is needed in order to allow the compent to settle after the zoom,
        // so that the onResize callback in VideoPlayer gets called with the correct values
        delay(INITIAL_LOAD_DELAY)
        initialLoadDelay = false
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    val w = it.size.toSize().width
                    availableWidth = with(density) { w.toDp() }
                },
        contentAlignment = Alignment.Center,
    ) {
        if (!revealing) {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(ratio),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    if (sensitive) {
                        Text(
                            text = LocalStrings.current.messageVideoNsfw,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            revealing = true
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonLoad)
                    }
                }
            }
        } else {
            if (initialLoadDelay) {
                Box(
                    modifier = Modifier.fillMaxWidth().aspectRatio(ratio),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (!initialLoadDelay) {
                VideoPlayer(
                    modifier = Modifier.size(videoSize),
                    url = url,
                    onResize = { newSize ->
                        videoSize =
                            with(density) {
                                DpSize(
                                    width = newSize.width.toDp(),
                                    height = newSize.height.toDp(),
                                )
                            }
                    },
                )
            }
        }

        IconButton(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        top = Spacing.xxs,
                        start = Spacing.xs,
                    ),
            onClick = {
                onClick?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = null,
            )
        }
    }
}

private const val INITIAL_LOAD_DELAY = 350L
