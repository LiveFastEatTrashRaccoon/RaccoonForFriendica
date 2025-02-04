package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScheduleSend
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.feature.composer.PublicationType

@Composable
internal fun UtilsBar(
    modifier: Modifier = Modifier,
    hasPoll: Boolean = false,
    supportsRichEditing: Boolean = true,
    supportsInlineImages: Boolean = false,
    publicationType: PublicationType,
    onLinkClicked: (() -> Unit)? = null,
    onAttachmentClicked: (() -> Unit)? = null,
    onBoldClicked: (() -> Unit)? = null,
    onItalicClicked: (() -> Unit)? = null,
    onUnderlineClicked: (() -> Unit)? = null,
    onStrikethroughClicked: (() -> Unit)? = null,
    onCodeClicked: (() -> Unit)? = null,
    onSubmitClicked: (() -> Unit)? = null,
    onInlineImageClicked: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.padding(horizontal = Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Row(
            modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
        ) {
            // add picture (from device gallery) button
            IconButton(
                enabled = !hasPoll,
                onClick = {
                    onAttachmentClicked?.invoke()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = LocalStrings.current.pickFromGalleryDialogTitle,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (supportsInlineImages) {
                IconButton(
                    onClick = {
                        onInlineImageClicked?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = LocalStrings.current.actionInsertInlineImage,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (supportsRichEditing) {
                // insert link button
                IconButton(
                    onClick = {
                        onLinkClicked?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = LocalStrings.current.actionInsertLink,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // bold format button
                IconButton(
                    onClick = {
                        onBoldClicked?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatBold,
                        contentDescription = LocalStrings.current.formatBold,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // italic format button
                IconButton(
                    onClick = {
                        onItalicClicked?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatItalic,
                        contentDescription = LocalStrings.current.formatItalic,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // underlined format button
                IconButton(
                    onClick = {
                        onUnderlineClicked?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatUnderlined,
                        contentDescription = LocalStrings.current.formatUnderlined,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // strikethrough format button
                IconButton(
                    onClick = {
                        onStrikethroughClicked?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatStrikethrough,
                        contentDescription = LocalStrings.current.formatStrikethrough,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // monospace format button
                IconButton(
                    onClick = {
                        onCodeClicked?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = LocalStrings.current.formatMonospace,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // send/schedule/save button
        IconButton(
            onClick = {
                onSubmitClicked?.invoke()
            },
        ) {
            Icon(
                imageVector =
                    when (publicationType) {
                        PublicationType.Draft -> Icons.Default.Save
                        is PublicationType.Scheduled -> Icons.AutoMirrored.Default.ScheduleSend
                        else -> Icons.AutoMirrored.Default.Send
                    },
                contentDescription = LocalStrings.current.actionSubmit,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
