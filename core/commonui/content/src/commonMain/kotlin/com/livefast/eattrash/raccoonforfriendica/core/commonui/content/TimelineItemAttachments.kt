package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType

@Composable
internal fun TimelineItemAttachments(
    attachments: List<AttachmentModel>,
    modifier: Modifier = Modifier,
    blurNsfw: Boolean = true,
    sensitive: Boolean = false,
    onOpenImage: ((String) -> Unit)? = null,
) {
    val images =
        attachments.filter { it.type == MediaType.Image }.takeIf { it.isNotEmpty() } ?: return
    val firstAttachment = images.first()
    val firstAttachmentWidth = firstAttachment.originalWidth ?: 0
    val firstAttachmentHeight = firstAttachment.originalHeight ?: 0
    val interItemSpacing = Spacing.xxs

    if (attachments.size == 1) {
        if (firstAttachment.url.isNotBlank()) {
            ContentImage(
                modifier = modifier,
                url = firstAttachment.url,
                altText = firstAttachment.description,
                blurHash = firstAttachment.blurHash,
                originalWidth = firstAttachment.originalWidth ?: 0,
                originalHeight = firstAttachment.originalHeight ?: 0,
                sensitive = blurNsfw && sensitive,
                onClick = { onOpenImage?.invoke(firstAttachment.url) },
            )
        }
    } else if (attachments.size == 2) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(interItemSpacing),
        ) {
            for (attachment in attachments) {
                val attachmentWidth = attachment.originalWidth ?: 0
                val attachmentHeight = attachment.originalHeight ?: 0
                ContentImage(
                    modifier = Modifier.weight(1f),
                    url = attachment.url,
                    altText = attachment.description,
                    blurHash = attachment.blurHash,
                    originalWidth = attachmentWidth,
                    originalHeight = attachmentHeight,
                    maxHeight = 200.dp,
                    sensitive = blurNsfw && sensitive,
                    onClick = { onOpenImage?.invoke(attachment.url) },
                    contentScale =
                        if (attachmentHeight < attachmentWidth) {
                            ContentScale.FillHeight
                        } else {
                            ContentScale.FillWidth
                        },
                )
            }
        }
    } else {
        if (firstAttachmentWidth < firstAttachmentHeight) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(interItemSpacing),
            ) {
                // first image
                var firstHeight by remember { mutableStateOf(0f) }
                ContentImage(
                    modifier =
                        Modifier.weight(0.75f).onGloballyPositioned {
                            firstHeight = it.size.toSize().height
                        },
                    url = firstAttachment.url,
                    altText = firstAttachment.description,
                    blurHash = firstAttachment.blurHash,
                    originalWidth = firstAttachment.originalWidth ?: 0,
                    originalHeight = firstAttachment.originalHeight ?: 0,
                    sensitive = blurNsfw && sensitive,
                    onClick = { onOpenImage?.invoke(firstAttachment.url) },
                )

                // rest of images arranged vertically in chunks
                val chunks = attachments.drop(1).chunked(4)
                for (chunk in chunks) {
                    Column(
                        modifier =
                            Modifier
                                .weight(0.25f)
                                .height(with(LocalDensity.current) { firstHeight.toDp() }),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(interItemSpacing),
                    ) {
                        for (attachment in chunk) {
                            val attachmentWidth = attachment.originalWidth ?: 0
                            val attachmentHeight = attachment.originalHeight ?: 0
                            ContentImage(
                                modifier = Modifier.weight(1f),
                                url = attachment.url,
                                altText = attachment.description,
                                blurHash = attachment.blurHash,
                                originalWidth = attachmentWidth,
                                originalHeight = attachmentHeight,
                                sensitive = blurNsfw && sensitive,
                                onClick = { onOpenImage?.invoke(attachment.url) },
                                contentScale =
                                    if (attachmentHeight < attachmentWidth) {
                                        ContentScale.FillHeight
                                    } else {
                                        ContentScale.FillWidth
                                    },
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(interItemSpacing),
            ) {
                // first image
                ContentImage(
                    modifier = Modifier.fillMaxWidth(),
                    url = firstAttachment.url,
                    altText = firstAttachment.description,
                    blurHash = firstAttachment.blurHash,
                    originalWidth = firstAttachment.originalWidth ?: 0,
                    originalHeight = firstAttachment.originalHeight ?: 0,
                    sensitive = blurNsfw && sensitive,
                    onClick = { onOpenImage?.invoke(firstAttachment.url) },
                )

                // rest of images arranged vertically in chunks
                val chunks = attachments.drop(1).chunked(4)
                for (chunk in chunks) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(interItemSpacing),
                    ) {
                        for (attachment in chunk) {
                            val attachmentWidth = attachment.originalWidth ?: 0
                            val attachmentHeight = attachment.originalHeight ?: 0
                            ContentImage(
                                modifier = Modifier.weight(1f),
                                url = attachment.url,
                                altText = attachment.description,
                                blurHash = attachment.blurHash,
                                originalWidth = attachmentWidth,
                                originalHeight = attachmentHeight,
                                sensitive = blurNsfw && sensitive,
                                maxHeight = 180.dp,
                                onClick = { onOpenImage?.invoke(attachment.url) },
                                contentScale =
                                    if (attachmentHeight < attachmentWidth) {
                                        ContentScale.FillHeight
                                    } else {
                                        ContentScale.FillWidth
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}
