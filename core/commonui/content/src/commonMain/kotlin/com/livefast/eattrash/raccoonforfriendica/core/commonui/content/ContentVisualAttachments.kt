package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.aspectRatio

@Composable
fun ContentVisualAttachments(
    attachments: List<AttachmentModel>,
    modifier: Modifier = Modifier,
    blurNsfw: Boolean = true,
    autoloadImages: Boolean = true,
    sensitive: Boolean = false,
    cornerSize: Dp = CornerSize.xl,
    onOpenImage: ((List<String>, Int, List<Int>) -> Unit)? = null,
) {
    fun handleClick(index: Int) {
        val urls = attachments.map { it.url }
        val videoIndices =
            attachments.mapIndexedNotNull { idx, attachmentModel ->
                if (attachmentModel.type == MediaType.Video) {
                    idx
                } else {
                    null
                }
            }
        onOpenImage?.invoke(urls, index, videoIndices)
    }

    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { attachments.size },
        )
    val referenceAspectRatio =
        attachments
            .minBy { it.originalHeight ?: 0 }
            .aspectRatio
            .takeIf { it > 0 } ?: (16 / 9f)
    val hasMultipleElements = attachments.size > 1
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        HorizontalPager(
            modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(referenceAspectRatio),
            state = pagerState,
            beyondViewportPageCount = 1,
        ) { index ->

            Box {
                AttachmentElement(
                    modifier = Modifier.fillMaxSize(),
                    attachment = attachments[index],
                    sensitive = blurNsfw && sensitive,
                    autoload = autoloadImages,
                    contentScale = ContentScale.FillWidth,
                    cornerSize = cornerSize,
                    onClick = {
                        handleClick(0)
                    },
                )

                if (hasMultipleElements) {
                    Badge(
                        modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = Spacing.s, end = Spacing.s),
                        containerColor = MaterialTheme.colorScheme.background.copy(0.75f),
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                            text = "${index + 1} / ${pagerState.pageCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
        if (hasMultipleElements) {
            // adds a page indicator
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.xs),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(pagerState.pageCount) { idx ->
                    val activeColor = MaterialTheme.colorScheme.primary
                    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant
                    val bgColor = if (pagerState.currentPage == idx) activeColor else inactiveColor
                    Box(
                        modifier =
                        Modifier
                            .padding(horizontal = Spacing.xxs)
                            .background(color = bgColor, shape = CircleShape)
                            .size(6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentElement(
    attachment: AttachmentModel,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
    blurNsfw: Boolean = true,
    autoload: Boolean = true,
    sensitive: Boolean = false,
    cornerSize: Dp = CornerSize.xl,
    onClick: (() -> Unit)? = null,
) {
    val attachmentWidth = attachment.originalWidth ?: 0
    val attachmentHeight = attachment.originalHeight ?: 0

    when (attachment.type) {
        MediaType.Image -> {
            ContentImage(
                modifier = modifier,
                url = attachment.url,
                contentDescription = attachment.description,
                blurHash = attachment.blurHash,
                originalWidth = attachmentWidth,
                originalHeight = attachmentHeight,
                cornerSize = cornerSize,
                sensitive = blurNsfw && sensitive,
                autoload = autoload,
                contentScale = contentScale,
                onClick = onClick,
            )
        }

        MediaType.Video -> {
            ContentVideo(
                modifier = modifier.fillMaxWidth(),
                url = attachment.url,
                autoload = autoload,
                sensitive = blurNsfw && sensitive,
                onClick = onClick,
            )
        }

        else -> Unit
    }
}
