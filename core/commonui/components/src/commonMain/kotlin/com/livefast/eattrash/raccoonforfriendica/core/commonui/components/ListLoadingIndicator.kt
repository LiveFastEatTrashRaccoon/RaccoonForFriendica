package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize

@Composable
fun ListLoadingIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition("rotation")
    val rotationAngle by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1000, delayMillis = 0, easing = LinearEasing),
                ),
        )
    Box(modifier = modifier.height(IconSize.xl), contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.rotate(rotationAngle),
            text = "🦝",
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
