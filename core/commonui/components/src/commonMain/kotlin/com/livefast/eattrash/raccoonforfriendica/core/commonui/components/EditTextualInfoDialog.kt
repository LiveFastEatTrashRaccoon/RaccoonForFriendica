package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextualInfoDialog(
    title: String = LocalStrings.current.actionEdit,
    label: String = "",
    value: String = "",
    onClose: ((String?) -> Unit)? = null,
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value))
    }

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
                text = title,
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
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                value = textFieldValue,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = true,
                    ),
                onValueChange = { value ->
                    textFieldValue = value
                },
            )

            Spacer(modifier = Modifier.height(Spacing.xs))
            Button(
                onClick = {
                    onClose?.invoke(textFieldValue.text)
                },
            ) {
                Text(text = LocalStrings.current.buttonConfirm)
            }
        }
    }
}
