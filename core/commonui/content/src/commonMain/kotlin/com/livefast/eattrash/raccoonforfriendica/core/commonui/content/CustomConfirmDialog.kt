package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
fun CustomConfirmDialog(
    title: String,
    body: String = LocalStrings.current.messageAreYouSure,
    confirmButtonLabel: String? = LocalStrings.current.buttonConfirm,
    cancelButtonLabel: String? = LocalStrings.current.buttonCancel,
    onClose: ((Boolean) -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = {
            onClose?.invoke(false)
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(text = body)
        },
        dismissButton = {
            if (cancelButtonLabel != null) {
                Button(
                    onClick = {
                        onClose?.invoke(false)
                    },
                ) {
                    Text(text = cancelButtonLabel)
                }
            }
        },
        confirmButton = {
            if (confirmButtonLabel != null) {
                Button(
                    onClick = {
                        onClose?.invoke(true)
                    },
                ) {
                    Text(text = confirmButtonLabel)
                }
            }
        },
    )
}
