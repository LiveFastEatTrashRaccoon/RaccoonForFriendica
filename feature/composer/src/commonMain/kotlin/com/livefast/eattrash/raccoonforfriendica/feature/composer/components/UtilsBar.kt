package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.feature.composer.PublicationType

@Composable
internal fun UtilsBar(
    publicationType: PublicationType,
    modifier: Modifier = Modifier,
    hasPoll: Boolean = false,
    supportsRichEditing: Boolean = true,
    supportsInlineImages: Boolean = false,
    onClickLink: (() -> Unit)? = null,
    onClickAttachment: (() -> Unit)? = null,
    onClickBold: (() -> Unit)? = null,
    onClickItalic: (() -> Unit)? = null,
    onClickUnderline: (() -> Unit)? = null,
    onClickStrikethrough: (() -> Unit)? = null,
    onClickCode: (() -> Unit)? = null,
    onClickInlineImage: (() -> Unit)? = null,
    onSubmit: (() -> Unit)? = null,
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
                    onClickAttachment?.invoke()
                },
            ) {
                Icon(
                    imageVector = LocalResources.current.camera,
                    contentDescription = LocalStrings.current.pickFromGalleryDialogTitle,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (supportsInlineImages) {
                IconButton(
                    onClick = {
                        onClickInlineImage?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = LocalResources.current.addPhotoAlternate,
                        contentDescription = LocalStrings.current.actionInsertInlineImage,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (supportsRichEditing) {
                // insert link button
                IconButton(
                    onClick = {
                        onClickLink?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = LocalResources.current.link,
                        contentDescription = LocalStrings.current.actionInsertLink,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // bold format button
                IconButton(
                    onClick = {
                        onClickBold?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = LocalResources.current.formatBold,
                        contentDescription = LocalStrings.current.formatBold,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // italic format button
                IconButton(
                    onClick = {
                        onClickItalic?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = LocalResources.current.formatItalic,
                        contentDescription = LocalStrings.current.formatItalic,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // underlined format button
                IconButton(
                    onClick = {
                        onClickUnderline?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = LocalResources.current.formatUnderlined,
                        contentDescription = LocalStrings.current.formatUnderlined,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // strikethrough format button
                IconButton(
                    onClick = {
                        onClickStrikethrough?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = LocalResources.current.strikethroughS,
                        contentDescription = LocalStrings.current.formatStrikethrough,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // monospace format button
                IconButton(
                    onClick = {
                        onClickCode?.invoke()
                    },
                ) {
                    Icon(
                        imageVector = LocalResources.current.code,
                        contentDescription = LocalStrings.current.formatMonospace,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // send/schedule/save button
        IconButton(
            onClick = {
                onSubmit?.invoke()
            },
        ) {
            Icon(
                imageVector =
                when (publicationType) {
                    PublicationType.Draft -> LocalResources.current.save
                    is PublicationType.Scheduled -> LocalResources.current.scheduleSend
                    else -> LocalResources.current.send
                },
                contentDescription = LocalStrings.current.actionSubmit,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
