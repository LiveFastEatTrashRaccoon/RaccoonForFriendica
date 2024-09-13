package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentBody
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getFormattedDate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel

@Composable
fun MessageItem(
    message: DirectMessageModel,
    modifier: Modifier = Modifier,
    isMyMessage: Boolean = false,
    isFirstOfSequence: Boolean = false,
    isLastOfSequence: Boolean = false,
    withDate: Boolean = false,
) {
    val cornersSize = CornerSize.l
    val fillerSizeDp = Spacing.xxxl
    var bubbleWidthPx by remember { mutableStateOf(0f) }
    val textColor =
        if (isMyMessage) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        }

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
                    start = 10.dp,
                    end = 10.dp,
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .then(
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
                            .background(
                                color =
                                    if (isMyMessage) {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.primaryContainer
                                    },
                                shape =
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
                    Column(
                        modifier =
                            Modifier.onGloballyPositioned {
                                bubbleWidthPx = it.size.toSize().width
                            },
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        ContentBody(
                            modifier =
                                Modifier.then(
                                    if (isMyMessage) {
                                        Modifier.align(Alignment.End)
                                    } else {
                                        Modifier
                                    },
                                ),
                            content = message.text.orEmpty(),
                            color = textColor,
                        )
                        val date = message.created
                        if (withDate && date != null) {
                            val moreThanOneDay =
                                (getDurationFromDateToNow(date)?.inWholeDays ?: 0) < -1
                            val dateString =
                                getFormattedDate(
                                    iso8601Timestamp = date,
                                    format =
                                        if (moreThanOneDay) {
                                            "dd/MM/yy • HH:mm"
                                        } else {
                                            "HH:mm"
                                        },
                                )
                            Text(
                                modifier = Modifier.align(Alignment.End),
                                text = dateString,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = textColor.copy(ancillaryTextAlpha),
                            )
                        }
                    }
                }
            }
        }
    }
}
