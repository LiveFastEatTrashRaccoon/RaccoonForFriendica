package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

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
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.isValidUrl
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.toReadableMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertLinkDialog(
    initialAnchor: String? = null,
    onClose: ((Pair<String, String>?) -> Unit)? = null,
) {
    var anchorTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text =
                    if (!initialAnchor.isNullOrBlank()) {
                        initialAnchor
                    } else {
                        ""
                    },
            ),
        )
    }
    var anchorError by remember { mutableStateOf<ValidationError?>(null) }
    var urlTextFieldValue by remember {
        mutableStateOf(TextFieldValue(text = ""))
    }
    var urlError by remember { mutableStateOf<ValidationError?>(null) }

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
                text = LocalStrings.current.insertLinkDialogTitle,
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
                        text = LocalStrings.current.insertLinkFieldAnchor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                isError = anchorError != null,
                supportingText = {
                    val error = anchorError
                    if (error != null) {
                        Text(
                            text = error.toReadableMessage(),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                value = anchorTextFieldValue,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                onValueChange = { value ->
                    anchorTextFieldValue = value
                },
            )

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
                        text = LocalStrings.current.insertLinkFieldUrl,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                isError = urlError != null,
                supportingText = {
                    val error = urlError
                    if (error != null) {
                        Text(
                            text = error.toReadableMessage(),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                value = urlTextFieldValue,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                    ),
                onValueChange = { value ->
                    urlTextFieldValue = value
                },
            )

            Spacer(modifier = Modifier.height(Spacing.xs))
            Button(
                onClick = {
                    val anchor = anchorTextFieldValue.text
                    val url = urlTextFieldValue.text
                    anchorError =
                        if (anchor.isEmpty()) {
                            ValidationError.MissingField
                        } else {
                            null
                        }
                    urlError =
                        if (url.isEmpty()) {
                            ValidationError.MissingField
                        } else if (!url.isValidUrl()) {
                            ValidationError.InvalidField
                        } else {
                            null
                        }
                    if (anchorError == null && urlError == null) {
                        onClose?.invoke(anchor to url)
                    }
                },
            ) {
                Text(text = LocalStrings.current.buttonConfirm)
            }
        }
    }
}
