package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.concatDateWithTime
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.extractTimePart
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toIso8601Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateTimeSelectionFlow(
    initialDateMillis: Long,
    onClose: ((String?) -> Unit)? = null,
) {
    var dateSelected by remember { mutableStateOf<Long?>(null) }
    var timePickerOpen by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis,
        )
    DatePickerDialog(
        onDismissRequest = {
            onClose?.invoke(null)
            dateSelected = null
        },
        confirmButton = {
            Button(
                onClick = {
                    dateSelected = datePickerState.selectedDateMillis
                    if (dateSelected != null) {
                        timePickerOpen = true
                    } else {
                        onClose?.invoke(null)
                    }
                },
            ) {
                Text(text = LocalStrings.current.buttonConfirm)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onClose?.invoke(null)
                },
            ) {
                Text(text = LocalStrings.current.buttonCancel)
            }
        },
    ) {
        DatePicker(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            state = datePickerState,
        )
    }

    if (timePickerOpen) {
        val initialValues = initialDateMillis.extractTimePart()
        val timePickerState =
            rememberTimePickerState(
                initialHour = initialValues.first,
                initialMinute = initialValues.second,
                is24Hour = true,
            )
        DatePickerDialog(
            onDismissRequest = {
                timePickerOpen = false
                dateSelected = null
            },
            confirmButton = {
                Button(
                    onClick = {
                        timePickerOpen = false
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute
                        if (dateSelected != null) {
                            val resultingDate =
                                dateSelected
                                    ?.concatDateWithTime(hour, minute, 0)
                                    ?.toIso8601Timestamp(withLocalTimezone = false)
                            onClose?.invoke(resultingDate)
                        }
                    },
                ) {
                    Text(text = LocalStrings.current.buttonConfirm)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        timePickerOpen = false
                    },
                ) {
                    Text(text = LocalStrings.current.buttonCancel)
                }
            },
        ) {
            TimePicker(
                modifier =
                    Modifier
                        .padding(top = Spacing.s)
                        .align(Alignment.CenterHorizontally),
                state = timePickerState,
            )
        }
    }
}
