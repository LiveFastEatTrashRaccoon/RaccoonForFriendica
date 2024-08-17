package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay

private const val LOADING_ANIMATION_DURATION = 1000

@Composable
fun ZoomableImage(
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    url: String,
) {
    var scale by remember {
        mutableStateOf(1f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    var visible by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(contentScale) {
        visible = false
        delay(50)
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
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = {
                                if (scale > 1f) {
                                    scale = 1f
                                    offset = Offset.Zero
                                } else {
                                    scale *= 2.5f
                                }
                            })
                            detectTransformGestures(
                                onGesture = { _, pan, gestureZoom, _ ->
                                    val extraWidth = (scale - 1) * constraints.maxWidth
                                    val extraHeight = (scale - 1) * constraints.maxHeight
                                    val maxX = extraWidth / 2
                                    val maxY = extraHeight / 2

                                    scale = (scale * gestureZoom).coerceIn(1f, 16f)

                                    offset =
                                        if (scale > 1) {
                                            Offset(
                                                x = (offset.x + pan.x * scale).coerceIn(-maxX, maxX),
                                                y = (offset.y + pan.y * scale).coerceIn(-maxY, maxY),
                                            )
                                        } else {
                                            Offset.Zero
                                        }
                                },
                            )
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
