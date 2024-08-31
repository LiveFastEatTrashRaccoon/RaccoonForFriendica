package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            onValueChange = {
                onValueChange?.invoke(it, field.value)
            },
            trailingIcon = {
                IconButton(onClick = { onDelete?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
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
            onValueChange = {
                onValueChange?.invoke(field.key, it)
            },
        )
    }
}