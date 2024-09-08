package com.livefast.eattrash.feature.userdetail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
fun UserNoteField(
    note: String,
    modifier: Modifier = Modifier,
    editEnabled: Boolean = false,
    onNoteChanged: ((String) -> Unit)? = null,
    onSave: (() -> Unit)? = null,
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            colors =
                OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MaterialTheme.colorScheme.onBackground,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                ),
            modifier = modifier.fillMaxWidth(),
            value = note,
            onValueChange = {
                onNoteChanged?.invoke(it)
            },
            enabled = editEnabled,
            label = { Text(text = LocalStrings.current.userFieldPersonalNote) },
            textStyle = MaterialTheme.typography.bodySmall,
            trailingIcon = {
                if (editEnabled) {
                    IconButton(
                        onClick = {
                            onSave?.invoke()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                        )
                    }
                }
            },
        )
    }
}
