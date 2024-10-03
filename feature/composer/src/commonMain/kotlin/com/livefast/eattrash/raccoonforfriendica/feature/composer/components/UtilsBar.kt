package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun UtilsBar(
    modifier: Modifier = Modifier,
    hasPoll: Boolean = false,
    hasSpoiler: Boolean = false,
    onLinkClicked: (() -> Unit)? = null,
    onAttachmentClicked: (() -> Unit)? = null,
    onBoldClicked: (() -> Unit)? = null,
    onItalicClicked: (() -> Unit)? = null,
    onUnderlineClicked: (() -> Unit)? = null,
    onStrikethroughClicked: (() -> Unit)? = null,
    onCodeClicked: (() -> Unit)? = null,
    onSpoilerClicked: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        IconButton(
            enabled = !hasPoll,
            onClick = {
                onAttachmentClicked?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = {
                onLinkClicked?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = {
                onBoldClicked?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.FormatBold,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = {
                onItalicClicked?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.FormatItalic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = {
                onUnderlineClicked?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.FormatUnderlined,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = {
                onStrikethroughClicked?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.FormatStrikethrough,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = {
                onCodeClicked?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (hasSpoiler) {
            FilledIconButton(
                colors =
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                onClick = {
                    onSpoilerClicked?.invoke()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            IconButton(
                onClick = {
                    onSpoilerClicked?.invoke()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
