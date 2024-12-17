package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getPrettyDuration
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDurationDialog(
    initialValue: Duration,
    availableValues: List<Duration>,
    onClose: ((Duration?) -> Unit)? = null,
) {
    var currentSelection by remember { mutableStateOf(initialValue) }
    BasicAlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(CornerSize.xxl)),
        onDismissRequest = {
            onClose?.invoke(null)
        },
    ) {
        Column(
            modifier =
                Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                    .padding(Spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = LocalStrings.current.selectDurationDialogTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(availableValues) { value ->
                    Row(
                        modifier =
                            Modifier
                                .padding(vertical = Spacing.s, horizontal = Spacing.s)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) {
                                    currentSelection = value
                                },
                    ) {
                        Text(
                            text =
                                if (value.isInfinite()) {
                                    LocalStrings.current.muteDurationIndefinite
                                } else {
                                    value.getPrettyDuration(
                                        secondsLabel = LocalStrings.current.timeSecondShort,
                                        minutesLabel = LocalStrings.current.timeMinuteShort,
                                        hoursLabel = LocalStrings.current.timeHourShort,
                                        daysLabel = LocalStrings.current.dateDayShort,
                                        finePrecision = false,
                                    )
                                },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        RadioButton(
                            modifier = Modifier.size(IconSize.s),
                            selected = value == currentSelection,
                            onClick = {
                                currentSelection = value
                            },
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onClose?.invoke(null)
                    },
                ) {
                    Text(text = LocalStrings.current.buttonCancel)
                }
                Button(
                    onClick = {
                        onClose?.invoke(currentSelection)
                    },
                ) {
                    Text(text = LocalStrings.current.buttonConfirm)
                }
            }
        }
    }
}
