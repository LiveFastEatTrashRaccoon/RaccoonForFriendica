package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.aspectRatio

@Composable
fun AttachmentsGrid(
    attachments: List<AttachmentModel>,
    modifier: Modifier = Modifier,
    blurNsfw: Boolean = true,
    sensitive: Boolean = false,
    onOpenImage: ((List<String>, Int, List<Int>) -> Unit)? = null,
) {
    val filteredAttachments =
        attachments
            .filter { it.type == MediaType.Image || it.type == MediaType.Video }
            .takeIf { it.isNotEmpty() } ?: return
    val firstAttachment = filteredAttachments.first()
    val interItemSpacing = Spacing.xxs
    val videoIndices =
        filteredAttachments.mapIndexedNotNull { index, attachmentModel ->
            if (attachmentModel.type == MediaType.Video) {
                index
            } else {
                null
            }
        }

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
                        videoIndices,
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
            filteredAttachments.forEachIndexed { index, attachment ->
                GridElement(
                    modifier = Modifier.weight(1f),
                    attachment = attachment,
                    maxHeight = 200.dp,
                    sensitive = blurNsfw && sensitive,
                    onClick = {
                        onOpenImage?.invoke(
                            urls,
                            index,
                            videoIndices,
                        )
                    },
                    contentScale =
                        if (attachment.aspectRatio >= 1) {
                            ContentScale.FillHeight
                        } else {
                            ContentScale.FillWidth
                        },
                )
            }
        }
    } else {
        val urls = filteredAttachments.map { it.url }
        val chunkSize = 4
        if (firstAttachment.aspectRatio < 1) {
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
                        onOpenImage?.invoke(
                            urls,
                            0,
                            videoIndices,
                        )
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
                                contentScale =
                                    if (chunkSize == chunk.size) {
                                        ContentScale.FillWidth
                                    } else {
                                        ContentScale.Crop
                                    },
                                onClick = {
                                    onOpenImage?.invoke(
                                        urls,
                                        index,
                                        videoIndices,
                                    )
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
                GridElement(
                    modifier = Modifier.fillMaxWidth(),
                    attachment = firstAttachment,
                    contentScale = ContentScale.FillWidth,
                    sensitive = blurNsfw && sensitive,
                    onClick = {
                        onOpenImage?.invoke(
                            urls,
                            0,
                            videoIndices,
                        )
                    },
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
                                contentScale =
                                    if (chunkSize == chunk.size) {
                                        ContentScale.FillHeight
                                    } else {
                                        ContentScale.Crop
                                    },
                                onClick = {
                                    onOpenImage?.invoke(
                                        urls,
                                        index,
                                        videoIndices,
                                    )
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
    attachment: AttachmentModel,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
    blurNsfw: Boolean = true,
    sensitive: Boolean = false,
    onClick: (() -> Unit)? = null,
    maxHeight: Dp = Dp.Unspecified,
) {
    val attachmentWidth = attachment.originalWidth ?: 0
    val attachmentHeight = attachment.originalHeight ?: 0

    when (attachment.type) {
        MediaType.Image -> {
            ContentImage(
                modifier = modifier,
                url = attachment.url,
                altText = attachment.description,
                blurHash = attachment.blurHash,
                originalWidth = attachmentWidth,
                originalHeight = attachmentHeight,
                sensitive = blurNsfw && sensitive,
                maxHeight = maxHeight,
                contentScale = contentScale,
                onClick = onClick,
            )
        }

        MediaType.Video -> {
            if (attachment.blurHash.isNullOrBlank() || attachmentWidth == 0 || attachmentHeight == 0) {
                ContentImage(
                    modifier =
                        modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    url = attachment.previewUrl.orEmpty(),
                    altText = attachment.description,
                    maxHeight = maxHeight,
                    contentScale = contentScale,
                    onClick = onClick,
                    centerComposable = {
                        Icon(
                            modifier = Modifier.size(IconSize.xl),
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                        )
                    },
                )
            } else {
                ContentImage(
                    modifier = modifier,
                    url = attachment.previewUrl.orEmpty(),
                    altText = attachment.description,
                    blurHash = attachment.blurHash,
                    originalWidth = attachmentWidth,
                    originalHeight = attachmentHeight,
                    sensitive = blurNsfw && sensitive,
                    maxHeight = maxHeight,
                    contentScale = contentScale,
                    onClick = onClick,
                    centerComposable = {
                        Icon(
                            modifier = Modifier.size(IconSize.xl),
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                        )
                    },
                )
            }
        }

        else -> {
            Unit
        }
    }
}
