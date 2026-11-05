package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.PreviewWrapper

@Composable
fun ListLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    dotSize: Dp = 8.dp,
    bounceHeight: Dp = 10.dp,
) {
    val density = LocalDensity.current
    val bounceHeightPx = with(density) { bounceHeight.toPx() }
    val infiniteTransition = rememberInfiniteTransition(label = "bouncing_dots")

    // staggered vertical displacement with keyframes
    val dot0 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0f at 0 using FastOutSlowInEasing
                (-bounceHeightPx) at 250 using FastOutSlowInEasing
                0f at 500 using FastOutSlowInEasing
                0f at 1200
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot0",
    )
    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0f at 150 using FastOutSlowInEasing
                (-bounceHeightPx) at 400 using FastOutSlowInEasing
                0f at 650 using FastOutSlowInEasing
                0f at 1200
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot1",
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0f at 300 using FastOutSlowInEasing
                (-bounceHeightPx) at 550 using FastOutSlowInEasing
                0f at 800 using FastOutSlowInEasing
                0f at 1200
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot2",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IconSize.xl)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(dot0, dot1, dot2).forEach { offset ->
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .graphicsLayer {
                            translationY = offset
                            // scale & alpha wave synchronized with the bounce
                            val progress = (-offset / bounceHeightPx).coerceIn(0f, 1f)
                            scaleX = 0.85f + 0.25f * progress
                            scaleY = 0.85f + 0.25f * progress
                            alpha = 0.5f + 0.5f * progress
                        }
                        .background(color, CircleShape),
                )
            }
        }
    }
}

@Composable
@Preview
private fun ListLoadingIndicatorPreview() {
    PreviewWrapper {
        Surface {
            ListLoadingIndicator()
        }
    }
}
