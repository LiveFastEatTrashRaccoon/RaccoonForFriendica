package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromNowToDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getPrettyDuration
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import kotlin.math.roundToInt

private fun PollOptionModel.getPercentage(total: Int): Float = if (total == 0) {
    0f
} else {
    ((votes / total.toFloat()) * 1000).roundToInt() / 10f
}

@Composable
fun PollCard(
    poll: PollModel,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    emojis: List<EmojiModel> = emptyList(),
    onVote: ((List<Int>) -> Unit)? = null,
) {
    val canVote = enabled && !poll.expired && poll.ownVotes.isEmpty()
    var showingResults by remember { mutableStateOf(false) }
    val selectedOptions = remember { mutableStateListOf<Int>() }
    val maxPercentage = poll.options.maxOf { it.getPercentage(poll.votes) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Spacer(modifier = Modifier.height(Spacing.xs))

        poll.options.forEachIndexed { index, option ->
            val percentage = option.getPercentage(poll.votes)
            val isVoted = selectedOptions.contains(index)
            PollCardOption(
                modifier = Modifier.fillMaxWidth(),
                title = option.title,
                emojis = emojis,
                percentage = percentage,
                enabled = canVote,
                reactToClickEnabled = !showingResults,
                isCurrentVote = isVoted && !showingResults,
                isOwnVote = poll.ownVotes.contains(index),
                isShowingResults = showingResults || poll.expired,
                isScoreHigher = (showingResults || poll.expired) && percentage > 0 && percentage >= maxPercentage,
                onVote = {
                    if (isVoted) {
                        selectedOptions -= index
                    } else {
                        if (!poll.multiple) {
                            selectedOptions.clear()
                        }
                        selectedOptions += index
                    }
                },
            )
        }

        PollCardFooter(
            modifier = Modifier.padding(horizontal = Spacing.xxs),
            expired = poll.expired,
            expirationDate = poll.expiresAt,
            votes = poll.votes,
            isShowingResults = showingResults,
            onChangeShowingResults = { newValue ->
                showingResults = newValue
            },
        )

        if (canVote) {
            Button(
                enabled = selectedOptions.isNotEmpty() && !showingResults,
                content = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (poll.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(IconSize.s),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = LocalStrings.current.actionVote,
                            textAlign = TextAlign.Center,
                        )
                    }
                },
                onClick = {
                    if (!poll.loading) {
                        onVote?.invoke(selectedOptions)
                    }
                },
            )
        }
    }
}

@Composable
private fun PollCardOption(
    title: String,
    modifier: Modifier = Modifier,
    percentage: Float = 0f,
    enabled: Boolean = false,
    reactToClickEnabled: Boolean = false,
    isCurrentVote: Boolean = false,
    isScoreHigher: Boolean = false,
    isOwnVote: Boolean = false,
    isShowingResults: Boolean = false,
    emojis: List<EmojiModel> = emptyList(),
    onVote: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(CornerSize.xl)
    Row(
        modifier =
        modifier
            .padding(vertical = Spacing.xxxs)
            .border(
                width = Dp.Hairline,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(CornerSize.xl),
            ).then(
                if (isScoreHigher) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.onBackground.copy(0.1f),
                        shape = shape,
                    )
                } else {
                    Modifier
                },
            ).clip(shape)
            .then(
                if (enabled) {
                    Modifier.clickable {
                        if (reactToClickEnabled) {
                            onVote?.invoke()
                        }
                    }
                } else {
                    Modifier
                },
            ).padding(vertical = Spacing.s, horizontal = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        // leading part
        Box(
            modifier =
            Modifier
                .size(IconSize.s)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape,
                ),
        ) {
            if (isCurrentVote) {
                Box(
                    modifier =
                    Modifier
                        .padding(Spacing.xs)
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape,
                        ),
                )
            } else if (isOwnVote || (!enabled && isScoreHigher)) {
                Icon(
                    modifier =
                    Modifier
                        .padding(Spacing.xxs)
                        .fillMaxSize(),
                    imageVector = Icons.Default.Check,
                    contentDescription = LocalStrings.current.highestScore,
                )
            }
        }

        // option title
        TextWithCustomEmojis(
            modifier = Modifier.weight(1f).padding(start = Spacing.xxs),
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            emojis = emojis,
        )

        // trailing part
        if (isShowingResults) {
            Text(
                modifier = Modifier.widthIn(min = 40.dp),
                text =
                buildString {
                    if ((percentage * 10).roundToInt() % 10 == 0) {
                        append(percentage.roundToInt())
                    } else {
                        append(percentage)
                    }
                    append("%")
                },
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha),
            )
        }
    }
}

@Composable
private fun PollCardFooter(
    modifier: Modifier = Modifier,
    votes: Int = 0,
    expired: Boolean = false,
    expirationDate: String? = null,
    isShowingResults: Boolean = false,
    onChangeShowingResults: ((Boolean) -> Unit)? = null,
) {
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text =
            buildString {
                append(LocalStrings.current.pollVote(votes))
                append(" • ")
                if (expired) {
                    append(LocalStrings.current.pollExpired)
                } else {
                    val remainingTime =
                        expirationDate
                            ?.let {
                                getDurationFromNowToDate(it)
                            }?.getPrettyDuration(
                                secondsLabel = LocalStrings.current.timeSecondShort,
                                minutesLabel = LocalStrings.current.timeMinuteShort,
                                hoursLabel = LocalStrings.current.timeHourShort,
                                daysLabel = LocalStrings.current.dateDayShort,
                                finePrecision = false,
                            ) ?: LocalStrings.current.shortUnavailable
                    append(LocalStrings.current.pollExpiresIn)
                    append(" ")
                    append(remainingTime)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = ancillaryColor,
            modifier = Modifier.weight(1f),
        )

        if (!expired) {
            TwoStateButton(
                isProminent = isShowingResults,
                label = LocalStrings.current.actionHideResults,
                prominentLabel = LocalStrings.current.actionShowResults,
                prominentColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                onValueChange = onChangeShowingResults,
            )
        }
    }
}
