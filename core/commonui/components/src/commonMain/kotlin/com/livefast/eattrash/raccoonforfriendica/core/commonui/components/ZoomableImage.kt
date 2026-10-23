package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.PreviewWrapper
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

private const val LOADING_ANIMATION_DURATION = 1000

@Composable
fun ZoomableImage(url: String, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Fit) {
    var scale by remember {
        mutableFloatStateOf(1f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    var visible by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(contentScale) {
        visible = false
        delay(50.milliseconds)
        visible = true
    }

    BoxWithConstraints(
        modifier =
        modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(visible = visible) {
            CustomImage(
                modifier =
                Modifier
                    .combinedClickable(
                        onClick = {},
                        onDoubleClick = {
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale *= 2.5f
                            }
                        },
                    ).pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            do {
                                val event = awaitPointerEvent()
                                val canceled = event.changes.any { it.isConsumed }
                                if (!canceled) {
                                    val zoomChange = event.calculateZoom()
                                    val panChange = event.calculatePan()

                                    if (scale > 1f || zoomChange != 1f) {
                                        val extraWidth = (scale - 1) * constraints.maxWidth
                                        val extraHeight = (scale - 1) * constraints.maxHeight
                                        val maxX = extraWidth / 2
                                        val maxY = extraHeight / 2

                                        scale = (scale * zoomChange).coerceIn(1f, 16f)

                                        offset =
                                            if (scale > 1) {
                                                Offset(
                                                    x =
                                                    (offset.x + panChange.x * scale).coerceIn(
                                                        minimumValue = -maxX,
                                                        maximumValue = maxX,
                                                    ),
                                                    y =
                                                    (offset.y + panChange.y * scale).coerceIn(
                                                        minimumValue = -maxY,
                                                        maximumValue = maxY,
                                                    ),
                                                )
                                            } else {
                                                Offset.Zero
                                            }

                                        // consume the gesture so pager doesn't intercept it while zoomed in
                                        if (scale > 1f) {
                                            event.changes.forEach {
                                                if (it.positionChanged()) {
                                                    it.consume()
                                                }
                                            }
                                        }
                                    }
                                }
                            } while (!canceled && event.changes.any { it.pressed })
                        }
                    }.graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                    ),
                url = url,
                contentScale = contentScale,
                quality = FilterQuality.High,
                onLoading = { progress ->
                    val prog =
                        if (progress != null) {
                            progress
                        } else {
                            val transition = rememberInfiniteTransition()
                            val res by transition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec =
                                InfiniteRepeatableSpec(
                                    animation = tween(LOADING_ANIMATION_DURATION),
                                ),
                            )
                            res
                        }
                    CircularProgressIndicator(
                        progress = { prog },
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            )
        }
    }
}

@Composable
@Preview
private fun ZoomableImagePreview() {
    PreviewWrapper {
        ZoomableImage(url = "fake-image")
    }
}
