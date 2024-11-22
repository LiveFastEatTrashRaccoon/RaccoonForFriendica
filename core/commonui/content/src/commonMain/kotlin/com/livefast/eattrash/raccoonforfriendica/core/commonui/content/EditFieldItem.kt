package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel

@Composable
fun EditFieldItem(
    field: FieldModel,
    modifier: Modifier = Modifier,
    onValueChange: ((String, String) -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = LocalStrings.current.editProfileItemFieldKey)
            },
            textStyle = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            value = field.key,
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                ),
            onValueChange = {
                onValueChange?.invoke(it, field.value)
            },
            trailingIcon = {
                IconButton(onClick = { onDelete?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = LocalStrings.current.buttonClose,
                    )
                }
            },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.xxs),
            label = {
                Text(text = LocalStrings.current.editProfileItemFieldValue)
            },
            textStyle = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            value = field.value,
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                ),
            onValueChange = {
                onValueChange?.invoke(field.key, it)
            },
        )
    }
}
