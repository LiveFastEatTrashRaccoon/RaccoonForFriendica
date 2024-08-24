package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getPrettyDuration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmMuteUserBottomSheet(
    userHandle: String,
    sheetState: SheetState = rememberModalBottomSheetState(),
    initialValue: Duration = Duration.INFINITE,
    availableValues: List<Duration> =
        listOf(
            Duration.INFINITE,
            5.minutes,
            30.minutes,
            1.hours,
            6.hours,
            1.days,
            3.days,
            7.days,
        ),
    onClose: (
        (
            Pair<Duration, Boolean>?,
        ) -> Unit
    )? = null,
) {
    var selectedDuration by remember { mutableStateOf(initialValue) }
    var selectedDisableNotifications by remember { mutableStateOf(true) }
    var selectDurationDialogOpened by remember { mutableStateOf(false) }

    if (selectDurationDialogOpened) {
        SelectDurationDialog(
            initialValue = selectedDuration,
            availableValues = availableValues,
            onClose = { newDuration ->
                selectDurationDialogOpened = false
                if (newDuration != null) {
                    selectedDuration = newDuration
                }
            },
        )
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onClose?.invoke(null)
        },
    ) {
        Column(
            modifier = Modifier.padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text =
                    buildString {
                        append(LocalStrings.current.actionMute)
                        if (userHandle.isNotEmpty()) {
                            append(" @$userHandle")
                        }
                    },
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            SettingsRow(
                title = LocalStrings.current.muteDurationItem,
                value =
                    if (selectedDuration.isInfinite()) {
                        LocalStrings.current.muteDurationIndefinite
                    } else {
                        selectedDuration.getPrettyDuration(
                            secondsLabel = LocalStrings.current.timeSecondShort,
                            minutesLabel = LocalStrings.current.timeMinuteShort,
                            hoursLabel = LocalStrings.current.timeHourShort,
                            daysLabel = LocalStrings.current.dateDayShort,
                            finePrecision = false,
                        )
                    },
                onTap = {
                    selectDurationDialogOpened = true
                },
            )
            SettingsSwitchRow(
                title = LocalStrings.current.muteDisableNotificationsItem,
                value = selectedDisableNotifications,
                onValueChanged = {
                    selectedDisableNotifications = it
                },
            )

            Button(
                modifier = Modifier.padding(horizontal = Spacing.m).fillMaxWidth(),
                onClick = {
                    onClose?.invoke(selectedDuration to selectedDisableNotifications)
                },
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = LocalStrings.current.buttonConfirm,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
