package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.shimmerEffect

@Composable
fun MessageItemPlaceholder(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp,
    isMyMessage: Boolean = false,
    isFirstOfSequence: Boolean = true,
    isLastOfSequence: Boolean = true,
) {
    val cornersSize = CornerSize.l
    val fillerSizeDp = Spacing.xxxl
    val contentHorizontalPadding = 10.dp

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    top =
                        if (isFirstOfSequence) {
                            Spacing.xs
                        } else {
                            0.dp
                        },
                    start = contentHorizontalPadding,
                    end = contentHorizontalPadding,
                ),
    ) {
        Box(
            modifier =
                Modifier.then(
                    if (isMyMessage) {
                        Modifier.padding(start = fillerSizeDp)
                    } else {
                        Modifier.padding(end = fillerSizeDp)
                    },
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isMyMessage) {
                    Spacer(modifier = Modifier.weight(1f))
                }
                Box(
                    modifier =
                        Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = if (isFirstOfSequence || isMyMessage) cornersSize else 0.dp,
                                    topEnd = if (isFirstOfSequence || !isMyMessage) cornersSize else 0.dp,
                                    bottomStart = if (isLastOfSequence || isMyMessage) cornersSize else 0.dp,
                                    bottomEnd = if (isLastOfSequence || !isMyMessage) cornersSize else 0.dp,
                                ),
                            ).padding(
                                vertical = Spacing.s,
                                horizontal = 12.dp,
                            ),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .height(height)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(cornersSize))
                                .shimmerEffect(),
                    )
                }
            }
        }
    }
}
