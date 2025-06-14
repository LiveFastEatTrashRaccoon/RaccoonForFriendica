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
    val belowLineColor = lineColor.copy(0.5f)
    val lineWidth = with(LocalDensity.current) { 1.5.dp.toPx() }

    Canvas(modifier = modifier) {
        val width = drawContext.size.width
        val height = drawContext.size.height

        val trimmedDataSet = dataset.takeLast(10)
        val maxValue = trimmedDataSet.maxOf { it.uses }
        val heightUnit = height / maxValue
        val widthUnit = width / trimmedDataSet.size

        val path = Path()
        path.moveTo(0f, height)
        for (i in trimmedDataSet.indices) {
            val x = (i + 1) * widthUnit
            val y = height - trimmedDataSet[i].uses * heightUnit
            path.lineTo(x, y)
        }
        path.lineTo(width, height)
        path.close()

        drawPath(path = path, color = belowLineColor, style = Fill)
        drawPath(path = path, color = lineColor, style = Stroke(lineWidth))
    }
}
