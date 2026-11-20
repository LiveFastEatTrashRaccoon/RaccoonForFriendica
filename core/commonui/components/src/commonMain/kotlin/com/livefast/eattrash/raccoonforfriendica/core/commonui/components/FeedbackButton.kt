package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.PreviewWrapper
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import kotlinx.coroutines.launch

@Composable
fun FeedbackButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    tintColor: Color = MaterialTheme.colorScheme.onBackground,
    contentDescription: String? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val currentOnClick by rememberUpdatedState(onClick)

    Image(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                rotationZ = rotation.value
            }
            .then(
                if (enabled) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                coroutineScope.launch {
                                    // run scale pop and rotational tilt concurrently
                                    launch {
                                        scale.animateTo(
                                            targetValue = 1.35f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow,
                                            ),
                                        )
                                        scale.animateTo(
                                            targetValue = 1f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessMedium,
                                            ),
                                        )
                                    }
                                    launch {
                                        rotation.animateTo(
                                            targetValue = -12f,
                                            animationSpec = tween(durationMillis = 80),
                                        )
                                        rotation.animateTo(
                                            targetValue = 8f,
                                            animationSpec = tween(durationMillis = 80),
                                        )
                                        rotation.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                        )
                                    }
                                }
                                currentOnClick?.invoke()
                            },
                        )
                    }
                } else {
                    Modifier
                },
            ),
        imageVector = imageVector,
        contentDescription = contentDescription,
        colorFilter = ColorFilter.tint(color = tintColor),
    )
}

@Composable
@Preview
private fun FeedbackButtonPreview() {
    PreviewWrapper {
        FeedbackButton(
            imageVector = LocalResources.current.thumbUp,
            onClick = {},
        )
    }
}
