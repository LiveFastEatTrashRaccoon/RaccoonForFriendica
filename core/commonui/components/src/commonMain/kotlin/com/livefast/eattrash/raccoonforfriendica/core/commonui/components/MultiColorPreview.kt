package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.setupPreview

@Composable
fun MultiColorPreview(colors: List<Color>, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier,
    ) {
        val step = 360f / colors.size.coerceAtLeast(1)
        var start = -90f
        for (i in colors.indices) {
            drawArc(
                color = colors[i],
                startAngle = start,
                sweepAngle = step,
                useCenter = true,
            )
            start += step
        }
    }
}

@Composable
@Preview
private fun MultiColorPreviewPreview() {
    setupPreview()
    MultiColorPreview(
        modifier = Modifier.size(24.dp),
        colors = listOf(
            Color(0xFF9400D3),
            Color(0xFF0000FF),
            Color(0xFF00FF00),
            Color(0xFFFFFF00),
            Color(0xFFFF7F00),
            Color(0xFFFF0000),
        ),
    )
}
