package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.tan

@Composable
internal fun SpoilerBar(
    modifier: Modifier = Modifier,
    inverted: Boolean = false,
) {
    val yellowHeight = with(LocalDensity.current) { 8.dp.toPx() }
    val blackHeight = with(LocalDensity.current) { 4.dp.toPx() }
    Canvas(modifier = modifier) {
        val totalHeight = size.height
        val totalWidth = size.width
        var currentOffset = 0f
        var index = 0
        val path = Path()
        val inclineAngleDeg = 45f * if (inverted) -1 else 1
        val inclineAngleRad = (inclineAngleDeg / 180f * PI).toFloat()
        val delta = totalWidth * tan(inclineAngleRad)
        val segmentCount = ceil(totalHeight / (yellowHeight + blackHeight) * 2).roundToInt()

        while (index < segmentCount) {
            val isYellow = index % 2 == 0
            val currentHeight = if (isYellow) yellowHeight else blackHeight
            val currentColor = if (isYellow) Color.Yellow else Color.Black
            path.reset()
            path.moveTo(0f, currentOffset)
            path.lineTo(x = totalWidth, y = currentOffset - delta)
            path.lineTo(x = totalWidth, y = currentOffset - delta + currentHeight)
            path.lineTo(x = 0f, y = currentOffset + currentHeight)
            path.close()
            drawPath(path, color = currentColor)

            index++
            currentOffset += currentHeight
        }
    }
}
