package com.livefast.eattrash.raccoonforfriendica.feature.circles.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.toReadableMessage
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CircleEditorData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CircleEditorDialog(
    data: CircleEditorData,
    onDataChanged: ((CircleEditorData) -> Unit)? = null,
    onClose: ((Boolean) -> Unit)? = null,
) {
    BasicAlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(CornerSize.xxl)),
        onDismissRequest = {
            onClose?.invoke(false)
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
                text =
                    if (data.id == null) {
                        LocalStrings.current.createCircleTitle
                    } else {
                        LocalStrings.current.editCircleTitle
                    },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.s))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                label = {
                    Text(
                        text = LocalStrings.current.circleEditFieldName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                value = data.title,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                isError = data.titleError != null,
                supportingText = {
                    val error = data.titleError
                    if (error != null) {
                        Text(
                            text = error.toReadableMessage(),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                onValueChange = { value ->
                    val newData = data.copy(title = value)
                    onDataChanged?.invoke(newData)
                },
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onClose?.invoke(false)
                    },
                ) {
                    Text(text = LocalStrings.current.buttonCancel)
                }
                Button(
                    onClick = {
                        onClose?.invoke(true)
                    },
                ) {
                    Text(text = LocalStrings.current.buttonConfirm)
                }
            }
        }
    }
}
