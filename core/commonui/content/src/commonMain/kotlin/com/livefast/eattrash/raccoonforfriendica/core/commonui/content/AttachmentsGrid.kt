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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType

@Composable
fun AttachmentsGrid(
    attachments: List<AttachmentModel>,
    modifier: Modifier = Modifier,
    blurNsfw: Boolean = true,
    sensitive: Boolean = false,
    onOpenImage: ((List<String>, Int) -> Unit)? = null,
) {
    val filteredAttachments =
        attachments.filter { it.type == MediaType.Image }.takeIf { it.isNotEmpty() } ?: return
    val firstAttachment = filteredAttachments.first()
    val firstAttachmentWidth = firstAttachment.originalWidth ?: 0
    val firstAttachmentHeight = firstAttachment.originalHeight ?: 0
    val interItemSpacing = Spacing.xxs

    if (filteredAttachments.size == 1) {
        if (firstAttachment.url.isNotBlank()) {
            GridElement(
                modifier = modifier,
                attachment = firstAttachment,
                sensitive = blurNsfw && sensitive,
                contentScale = ContentScale.FillWidth,
                onClick = {
                    onOpenImage?.invoke(
                        listOf(firstAttachment.url),
                        0,
                    )
                },
            )
        }
    } else if (filteredAttachments.size == 2) {
        val urls = filteredAttachments.map { it.url }
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(interItemSpacing),
        ) {
            filteredAttachments.forEachIndexed { idx, attachment ->
                GridElement(
                    modifier = Modifier.weight(1f),
                    attachment = attachment,
                    maxHeight = 200.dp,
                    sensitive = blurNsfw && sensitive,
                    onClick = {
                        onOpenImage?.invoke(urls, idx)
                    },
                )
            }
        }
    } else {
        val urls = filteredAttachments.map { it.url }
        val chunkSize = 4
        if (firstAttachmentWidth < firstAttachmentHeight) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(interItemSpacing),
            ) {
                // first attachment
                var firstHeight by remember { mutableStateOf(0f) }
                GridElement(
                    modifier =
                        Modifier.weight(0.75f).onGloballyPositioned {
                            firstHeight = it.size.toSize().height
                        },
                    attachment = firstAttachment,
                    sensitive = blurNsfw && sensitive,
                    contentScale = ContentScale.FillWidth,
                    onClick = {
                        onOpenImage?.invoke(urls, 0)
                    },
                )

                // rest of attachments arranged vertically in chunks
                val chunks = filteredAttachments.drop(1).chunked(chunkSize)
                chunks.forEachIndexed { chunkIndex, chunk ->
                    Column(
                        modifier =
                            Modifier
                                .weight(0.25f)
                                .height(with(LocalDensity.current) { firstHeight.toDp() }),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(interItemSpacing),
                    ) {
                        chunk.forEachIndexed { imageIndex, attachment ->
                            val index = imageIndex + chunkIndex * chunkSize + 1
                            GridElement(
                                modifier = Modifier.weight(1f),
                                attachment = attachment,
                                sensitive = blurNsfw && sensitive,
                                contentScale = ContentScale.FillWidth,
                                onClick = {
                                    onOpenImage?.invoke(urls, index)
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
                // first attachment
                ContentImage(
                    modifier = Modifier.fillMaxWidth(),
                    url = firstAttachment.url,
                    altText = firstAttachment.description,
                    blurHash = firstAttachment.blurHash,
                    originalWidth = firstAttachment.originalWidth ?: 0,
                    originalHeight = firstAttachment.originalHeight ?: 0,
                    sensitive = blurNsfw && sensitive,
                    onClick = { onOpenImage?.invoke(urls, 0) },
                )

                // rest of attachments arranged vertically in chunks
                val chunks = filteredAttachments.drop(1).chunked(chunkSize)
                chunks.forEachIndexed { chunkIndex, chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(interItemSpacing),
                    ) {
                        chunk.forEachIndexed { imageIndex, attachment ->
                            val index = imageIndex + chunkIndex * chunkSize + 1
                            GridElement(
                                modifier = Modifier.weight(1f),
                                attachment = attachment,
                                sensitive = blurNsfw && sensitive,
                                maxHeight = 180.dp,
                                contentScale = ContentScale.FillHeight,
                                onClick = {
                                    onOpenImage?.invoke(urls, index)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GridElement(
    modifier: Modifier,
    attachment: AttachmentModel,
    blurNsfw: Boolean = true,
    sensitive: Boolean = false,
    onClick: (() -> Unit)? = null,
    contentScale: ContentScale? = null,
    maxHeight: Dp = Dp.Unspecified,
) {
    val attachmentWidth = attachment.originalWidth ?: 0
    val attachmentHeight = attachment.originalHeight ?: 0

    ContentImage(
        modifier = modifier,
        url = attachment.url,
        altText = attachment.description,
        blurHash = attachment.blurHash,
        originalWidth = attachmentWidth,
        originalHeight = attachmentHeight,
        sensitive = blurNsfw && sensitive,
        maxHeight = maxHeight,
        onClick = onClick,
        contentScale =
            contentScale ?: if (attachmentHeight < attachmentWidth) {
                ContentScale.FillHeight
            } else {
                ContentScale.FillWidth
            },
    )
}
