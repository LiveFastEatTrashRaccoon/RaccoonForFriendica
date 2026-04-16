package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.HashtagHistoryItem
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel

@Composable
fun HashtagItem(hashtag: TagModel, modifier: Modifier = Modifier, onOpen: ((String) -> Unit)? = null) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Row(
        modifier =
        modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onOpen?.invoke(hashtag.name)
            }.padding(Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text =
                buildString {
                    append("#")
                    append(hashtag.name)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = fullColor,
            )
            if (hashtag.history.isNotEmpty()) {
                val count = hashtag.history.run { first().users.toInt() }
                Text(
                    text = LocalStrings.current.hashtagPeopleUsing(count),
                    style = MaterialTheme.typography.labelMedium,
                    color = ancillaryColor,
                )
            }
        }

        if (hashtag.history.isNotEmpty()) {
            HashtagChart(
                modifier =
                Modifier
                    .weight(0.25f)
                    .padding(end = Spacing.s)
                    .aspectRatio(3f),
                dataset = hashtag.history,
            )
        }
    }
}

@Composable
private fun HashtagChart(modifier: Modifier = Modifier, dataset: List<HashtagHistoryItem> = emptyList()) {
    val lineColor = MaterialTheme.colorScheme.secondary
    val lineWidth = with(LocalDensity.current) { 0.75.dp.toPx() }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val trimmedDataSet = dataset.take(10).reversed()
        if (trimmedDataSet.isEmpty()) return@Canvas

        val maxValue = trimmedDataSet.maxOf { it.uses }.coerceAtLeast(1L).toFloat()
        val heightUnit = height / maxValue
        val xStep = if (trimmedDataSet.size > 1) width / (trimmedDataSet.size - 1) else width

        val fillPath = Path()
        val strokePath = Path()

        fillPath.moveTo(0f, height)

        trimmedDataSet.forEachIndexed { i, item ->
            val x = if (trimmedDataSet.size > 1) i * xStep else width
            val y = height - item.uses * heightUnit

            if (i == 0) {
                if (trimmedDataSet.size == 1) {
                    fillPath.lineTo(x, y)
                    strokePath.moveTo(0f, height)
                    strokePath.lineTo(x, y)
                } else {
                    fillPath.lineTo(x, y)
                    strokePath.moveTo(x, y)
                }
            } else {
                val prevX = (i - 1) * xStep
                val prevY = height - trimmedDataSet[i - 1].uses * heightUnit
                val cp1x = prevX + (x - prevX) / 2f

                fillPath.cubicTo(cp1x, prevY, cp1x, y, x, y)
                strokePath.cubicTo(cp1x, prevY, cp1x, y, x, y)
            }
        }

        fillPath.lineTo(width, height)
        fillPath.close()

        val fillBrush = Brush.verticalGradient(
            colors = listOf(lineColor.copy(alpha = 0.9f), lineColor.copy(alpha = 0.5f)),
            startY = 0f,
            endY = height,
        )

        drawPath(path = fillPath, brush = fillBrush, style = Fill)
        drawPath(path = strokePath, color = lineColor, style = Stroke(lineWidth))
    }
}
